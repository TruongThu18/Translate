package com.example.translater;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.translater.R;
import com.example.translater.UI.AdapterSp;
import com.example.translater.model.ModelTemp;
import com.example.translater.model.Translate;
import com.example.translater.repo.TranslateDataBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class FloatingWindowGFG extends Service  {

    private ViewGroup floatView;
    private int LAYOUT_TYPE;
    private WindowManager.LayoutParams floatWindowLayoutParam;
    private WindowManager windowManager;
    ImageView btnmax, btnswitch,btnx;
    EditText in;
    TextView out;
    Button btntran;
    Spinner sp1,sp2;
    List<String> listsp;
    int langCode, fromLangCode, toLangCode=0;
    String lang1, lang2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        floatView = (ViewGroup) inflater.inflate(R.layout.float_window, null);
        //
        btnmax = floatView.findViewById(R.id.btnmax);
        in = floatView.findViewById(R.id.in3);
        out = floatView.findViewById(R.id.out3);
        btntran = floatView.findViewById(R.id.btntran3);
        btnx = floatView.findViewById(R.id.btnx3);
        btnswitch = floatView.findViewById(R.id.btnswitch3);
        sp1 = floatView.findViewById(R.id.spfrom3);
        sp2 = floatView.findViewById(R.id.spto3);

        AdapterSp adapterSp = new AdapterSp();
        sp1.setAdapter(adapterSp);
        sp2.setAdapter(adapterSp);
        sp1.setSelection(ModelTemp.lang1);
        sp2.setSelection(ModelTemp.lang2);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLangCode = getLangCode(adapterSp.getListsp().get(i));
                lang2 = adapterSp.getListsp().get(i);
                ModelTemp.lang2 = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLangCode = getLangCode(adapterSp.getListsp().get(i));
                lang1 = adapterSp.getListsp().get(i);
                ModelTemp.lang1 = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setText("");
                out.setText("");
            }
        });
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tmp1 = sp1.getSelectedItemPosition();
                int tmp2 = sp2.getSelectedItemPosition();
                sp1.setSelection(tmp2);
                sp2.setSelection(tmp1);
                in.setText(out.getText().toString());
                out.setText("");
            }
        });

        if(!ModelTemp.inText.equals("")) in.setText(ModelTemp.inText);
        if(!ModelTemp.outText.equals("")) out.setText(ModelTemp.outText);
        //ktra api > 26 thif hiển thị float còn nhỏ hơn thì toast thông báo lỗi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }
        floatWindowLayoutParam = new WindowManager.LayoutParams(
                (int) (width * (0.6f)),
                (int) (height * (0.4f)),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        floatWindowLayoutParam.gravity = Gravity.RIGHT| Gravity.TOP;
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;
        windowManager.addView(floatView, floatWindowLayoutParam);
        floatView.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        px = event.getRawX();
                        py = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x - event.getRawX()) + px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);
                        break;
                }
                return false;
            }
        });
        btnmax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!out.getText().toString().equals(""))
                    ModelTemp.outText = out.getText().toString();
                if(!in.getText().toString().equals(""))
                    ModelTemp.inText = in.getText().toString();
                ModelTemp.lang1 = sp1.getSelectedItemPosition();
                ModelTemp.lang2 = sp2.getSelectedItemPosition();
                stopSelf();
                windowManager.removeView(floatView);
                Intent intent = new Intent(FloatingWindowGFG.this, MainActivity.class);
               // Cờ Flag_activity_new_task giúp hoạt động bắt đầu một nhiệm vụ mới trên ngăn xếp lịch sử,
                // nếu cái tác vụ đang hoạt động trên cửa sổ nổi thì cái nhiệm vụ mới này sẽ k đc bắt đầu
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        //Float window flag đang đc xét ở dạng not_focus vì vậy k thể nhập đc vào từ bàn phím đc gọi từ float window
        // --> p thay đổi cờ để có thể nhập input vào từ bàn phím
        in.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                WindowManager.LayoutParams floatWindowUpdateFlag = floatWindowLayoutParam;
                floatWindowUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(floatView, floatWindowUpdateFlag);
                return false;
            }
        });
        btntran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(in.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Vui lòng nhập văn bản muốn dịch!!",Toast.LENGTH_SHORT).show();
                }else {
                    if(fromLangCode== toLangCode){
                        Toast.makeText(getApplicationContext(),"Vui lòng chọn ngôn ngữ muốn dịch khác với ngôn ngữ bạn nhập",Toast.LENGTH_SHORT).show();
                    }else {
                        TranslateText(toLangCode,fromLangCode,in.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        // Window is removed from the screen
        windowManager.removeView(floatView);
    }
    private int getLangCode(String s) {
        switch (s){
            case "English":
                return FirebaseTranslateLanguage.EN;
            case "French":
                return FirebaseTranslateLanguage.FR;
            case "Germany":
                return FirebaseTranslateLanguage.DE;
            case "Korean":
                return FirebaseTranslateLanguage.KO;
            case "Catalan":
                return FirebaseTranslateLanguage.CA;
            case "Italian":
                return FirebaseTranslateLanguage.BE;
            case "Urdu":
                return FirebaseTranslateLanguage.UR;
            case "Czech":
                return FirebaseTranslateLanguage.CS;
            case "Welsh":
                return FirebaseTranslateLanguage.CY;
            case "Hindi":
                return FirebaseTranslateLanguage.HI;
            case "Japan":
                return FirebaseTranslateLanguage.JA;
            case "Vietnamese":
                return FirebaseTranslateLanguage.VI;
            case "Chinese":
                return FirebaseTranslateLanguage.ZH;
        }
        return FirebaseTranslateLanguage.AF;
    }
    private void TranslateText(int toLangCode, int fromLangCode, String text) {
        out.setHint("Please wait...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLangCode)
                .setTargetLanguage(toLangCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        out.setText(s);
                        Translate translate = new Translate(in.getText().toString(), s,lang1,lang2 );
                        TranslateDataBase.getInstance(getApplicationContext()).transDao().add(translate);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Lỗi biên dịch, Vui lòng thử lại!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Kết nối Internet thất bại, vui lòng kiểm tra kết nối Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
