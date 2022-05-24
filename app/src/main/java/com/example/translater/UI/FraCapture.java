package com.example.translater.UI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.translater.BuildConfig;
import com.example.translater.R;
import com.example.translater.model.Translate;
import com.example.translater.repo.TranslateDataBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.Arrays;
import java.util.List;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class FraCapture extends Fragment {
    Spinner sp1,sp2;
    Button btntran;
    ImageView  btnswitch, inimg;
    TextView out,in;
    List<String> listsp;
    ImageButton btncam, btncap;
    String lang1,lang2;
    int langCode, fromLangCode, toLangCode=0;
    static final int REQUEST_CODE_CAPTURE = 2;
    static final int REQUEST_CODE_GALLEY = 3;
    Bitmap imgBitmap;
    Uri image;
    String currentPhotoPath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra2,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btntran = view.findViewById(R.id.btntran2);
        btnswitch = view.findViewById(R.id.btnswitch2);
        out = view.findViewById(R.id.out2);
        in = view.findViewById(R.id.in2);
        sp1 = view.findViewById(R.id.spfrom2);
        sp2 = view.findViewById(R.id.spto2);
        btncam = view.findViewById(R.id.btncam);
        btncap = view.findViewById(R.id.btncap);
        inimg = view.findViewById(R.id.inImg);
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

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item,listsp);
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);
        sp2.setSelection(11);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLangCode = getLangCode(listsp.get(i));
                lang2 = listsp.get(i);
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
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btncam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    out.setText("");
                    in.setText("");
                    caputureImage();
                }else {
                    requestPermission();
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
        btncap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    out.setText("");
                    in.setText("");
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,REQUEST_CODE_GALLEY);
                }else {
                    requestPermission();
                }
            }
        });
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

    private boolean checkPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(getContext(), CAMERA);
        int galleyPermission = ContextCompat.checkSelfPermission(getContext(),READ_EXTERNAL_STORAGE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && galleyPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void  requestPermission(){
        int PERMISSION_CODE=200;
        ActivityCompat.requestPermissions(getActivity(),new String[]{CAMERA, READ_EXTERNAL_STORAGE},PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            boolean camPermission = grantResults[0] ==PackageManager.PERMISSION_GRANTED;
            boolean galPermission = grantResults[1]==PackageManager.PERMISSION_GRANTED;
            if(camPermission && galPermission){
                Toast.makeText(getContext(),"Đã cấp quyền...",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),"Từ chối cấp quyền...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void caputureImage(){
        Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName="photo";
        File store = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(fileName, ".jpg",store);
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID+".provider",imageFile);
            intentCam.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            if(intentCam.resolveActivity(getActivity().getPackageManager())!=null){
                startActivityForResult(intentCam,REQUEST_CODE_CAPTURE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK){
            imgBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            inimg.setImageBitmap(imgBitmap);
            detectText();
        }
        if(requestCode == REQUEST_CODE_GALLEY && data!= null){
           Uri imgUri = data.getData();
           inimg.setImageURI(imgUri);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) inimg.getDrawable();
            imgBitmap= bitmapDrawable.getBitmap();
               detectText();
        }
    }
    private void detectText(){
        InputImage inputImage = InputImage.fromBitmap(imgBitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block : text.getTextBlocks()){
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line: block.getLines()){
                        String lineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();
                        for(Text.Element element : line.getElements()){
                            String elementText = element.getText();
                            result.append(elementText );
                        }
                        in.setText(blockText);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Biên dịch thất bại..", Toast.LENGTH_SHORT).show();
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


}
