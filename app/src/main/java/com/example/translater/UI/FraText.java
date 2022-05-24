package com.example.translater.UI;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.translater.FloatingWindowGFG;
import com.example.translater.R;
import com.example.translater.model.ModelTemp;
import com.example.translater.model.Translate;
import com.example.translater.repo.TranslateDataBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class FraText extends Fragment {
    Spinner sp1,sp2;
    Button btntran;
    ImageView btnvoice, btnswitch,btnx,btnspeaker1, btnspeaker2, btnRestore;
    TextView out;
    TextInputEditText in;
    List<String> listsp;
    private static final int REQUEST_PERMISSION_CODE_VOICE = 1;
    int langCode, fromLangCode, toLangCode=0;
    String lang1, lang2;
    TextToSpeech tts;
    private AlertDialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra1,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btntran = view.findViewById(R.id.btntran1);
        btnswitch = view.findViewById(R.id.btnswitch1);
        btnvoice = view.findViewById(R.id.btnmic);
        btnx = view.findViewById(R.id.btnx);
        btnspeaker1 = view.findViewById(R.id.btnspeaker1);
        btnspeaker2 = view.findViewById(R.id.btnspeaker2);
        btnRestore = view.findViewById(R.id.btnrestore);
        out = view.findViewById(R.id.out1);
        in = view.findViewById(R.id.in1);
        sp1 = view.findViewById(R.id.spfrom1);
        sp2 = view.findViewById(R.id.spto1);
        listsp = new ArrayList<>();
        listsp = Arrays.asList(getResources().getStringArray(R.array.listSp));
        /*listsp.add("English");
        listsp.add("French");
        listsp.add("Germany");
        listsp.add("Korean");
        listsp.add("Urdu");
        listsp.add("Italian");
        listsp.add("Catalan");
        listsp.add("Czech");
        listsp.add("Welsh");
        listsp.add("Hindi");
        listsp.add("Japan");
        listsp.add("Vietnamese");
        listsp.add("Chinese");*/

        if(isMyServiceRunning())
            getActivity().stopService(new Intent(getContext(), FloatingWindowGFG.class));

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item,listsp);
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);
        in.setText(ModelTemp.inText);
        if(!ModelTemp.outText.equals("")) out.setText(ModelTemp.outText);
        sp1.setSelection(ModelTemp.lang1);
        sp2.setSelection(ModelTemp.lang2);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLangCode = getLangCode(listsp.get(i));
                lang2 = listsp.get(i);
                ModelTemp.lang2 = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLangCode = getLangCode(listsp.get(i));
                lang1 = listsp.get(i);
                ModelTemp.lang1 = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                out.setText("");
                in.setText("");
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something do translate");
                try {
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE_VOICE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(),""+ e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        btntran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(in.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Vui lòng nhập văn bản muốn dịch!!",Toast.LENGTH_SHORT).show();
                }else {
                    if(fromLangCode== toLangCode){
                        Toast.makeText(getContext(),"Vui lòng chọn ngôn ngữ muốn dịch khác với ngôn ngữ bạn nhập",Toast.LENGTH_SHORT).show();
                    }else {
                        TranslateText(toLangCode,fromLangCode,in.getText().toString());
                    }
                }
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
        btnx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.setText("");
                out.setText("");
            }
        });
        btnspeaker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale l = getLocale(listsp.get(sp1.getSelectedItemPosition()));
                if(l==null) Toast.makeText(getContext(),"Ngôn ngữ này chưa hỗ trợ chuyển sang giọng nói", Toast.LENGTH_SHORT).show();
                else {
                    tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS && in.getText().toString()!=null){
                                tts.setLanguage(l);
                                tts.setSpeechRate(1.0f);
                                tts.speak(in.getText().toString(),TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    });
                }
            }
        });
        btnspeaker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale l = getLocale(listsp.get(sp2.getSelectedItemPosition()));
                if(l==null) Toast.makeText(getContext(),"Ngôn ngữ này chưa hỗ trợ chuyển sang giọng nói", Toast.LENGTH_SHORT).show();
                else {
                    tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if(i==TextToSpeech.SUCCESS && out.getText().toString()!=null){
                                tts.setLanguage(l);
                                tts.setSpeechRate(1.0f);
                                tts.speak(out.getText().toString(),TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    });
                }
            }
        });
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOverDispalyPermission()){
                    if(!out.getText().toString().equals(""))
                        ModelTemp.outText = out.getText().toString();
                    if(!in.getText().toString().equals(""))
                        ModelTemp.inText = in.getText().toString();
                    ModelTemp.lang1 = sp1.getSelectedItemPosition();
                    ModelTemp.lang2 = sp2.getSelectedItemPosition();
                    getActivity().startService(new Intent(getContext(), FloatingWindowGFG.class));
                    getActivity().finish();
                }else requestOverlayDisplayPermission();
            }
        });
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
                        TranslateDataBase.getInstance(getContext()).transDao().add(translate);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Lỗi biên dịch, Vui lòng thử lại!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Kết nối Internet thất bại, vui lòng kiểm tra kết nối Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE_VOICE){
            List<String> rs = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            in.setText(rs.get(0));
        }
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
            case "Chinese":
                return FirebaseTranslateLanguage.ZH;
            case "Vietnamese":
                return FirebaseTranslateLanguage.VI;
        }
        return FirebaseTranslateLanguage.AF;
    }
    private Locale getLocale(String lang){
        switch (lang){
            case "English":
                return Locale.US;
            case "Japan":
                return Locale.JAPAN;
            case "Chinese":
                return Locale.CHINESE;
            case "French":
                return Locale.FRANCE;
            case "Italian":
                return Locale.ITALIAN;
            case "Germany":
                return Locale.GERMANY;
            case "Vietnamese":
                return Locale.ENGLISH;
        }
        return null;
    }
    private boolean checkOverDispalyPermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(getContext())){
                return false;
            }else return true;
        }else  return true;
    }
    private void requestOverlayDisplayPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Screen Overlay Permission Needed");
        builder.setMessage("Enable 'Display over other apps' from System Settings.");
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, RESULT_OK);
            }
        });
        dialog = builder.create();
        dialog.show();
    }
    private boolean isMyServiceRunning(){
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(FloatingWindowGFG.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}

