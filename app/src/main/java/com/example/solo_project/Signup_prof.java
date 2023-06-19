package com.example.solo_project;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup_prof extends AppCompatActivity {
    final int REQUEST_IMAGE_CAPTURE = 0;
    final int REQUEST_Image = 1;
    private String filepath = null;
    private Uri Uri;
    String email;
    String PW;
    Boolean checkname = false;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_prof);
        Button next = findViewById(R.id.next1);
        Button checkn = findViewById(R.id.checknickname);
        ImageButton profilebtn = findViewById(R.id.profilebtn);
        Intent I = getIntent();
        EditText nk = (EditText) findViewById(R.id.editNickname);
        email = I.getStringExtra("email");
        PW = I.getStringExtra("PW");
        phone_number = I.getStringExtra("phone_number");
        Log.e("insert", email + "/" + PW+"/"+phone_number);
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Signup_prof.this);
                View view1 = LayoutInflater.from(Signup_prof.this).inflate(R.layout.profile_menu, null, false);
                builder.setView(view1);
                AlertDialog dialog = builder.create();
                Button camera = view1.findViewById(R.id.camera);
                Button Gelary = view1.findViewById(R.id.Galary);
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //카메라 접근
                        int permission = ContextCompat.checkSelfPermission(Signup_prof.this, Manifest.permission.CAMERA);
                        int RequestCode = 0;
                        if (permission == PackageManager.PERMISSION_DENIED) {

                            ActivityCompat.requestPermissions(Signup_prof.this, new String[]{Manifest.permission.CAMERA}, RequestCode);

                        } else if (ActivityCompat.shouldShowRequestPermissionRationale(Signup_prof.this, Manifest.permission.CAMERA)) {
                            ActivityCompat.requestPermissions(Signup_prof.this, new String[]{Manifest.permission.CAMERA}, RequestCode);
                        } else {
                            getcamera();
                            dialog.dismiss();
                        }
                    }
                });
                Gelary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //갤러리 접근
                        int permission = ContextCompat.checkSelfPermission(Signup_prof.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permission == PackageManager.PERMISSION_DENIED) {

                            ActivityCompat.requestPermissions(Signup_prof.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

                        } else if (ActivityCompat.shouldShowRequestPermissionRationale(Signup_prof.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(Signup_prof.this, new String[]{Manifest.permission.CAMERA}, 0);
                        } else {
                            getG();
                            dialog.dismiss();
                        }

                    }
                });
                dialog.show();
            }
        });
        EditText nickname = findViewById(R.id.editNickname); //edit text
        checkn.setOnClickListener(new View.OnClickListener() { // 중복 확인 부분.
            @Override
            public void onClick(View view) {
                if(checkn.getText().toString().equals("수정")){
                    nickname.setInputType(InputType.TYPE_CLASS_TEXT);
                    checkn.setText("중복체크");
//                    next.setClickable(false);

                }else{
                    TextView Check = findViewById(R.id.checking);
                    if (!nickname.getText().toString().trim().equals("")) {
                        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                        Call<String> call = apiInterface.checknickname(nickname.getText().toString());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body() != null) {
                                    if (response.body().equals("가능")) {
                                        nickname.setInputType(InputType.TYPE_NULL);
                                        checkn.setText("수정");
                                        Check.setText("사용가능한 닉네임입니다.");
                                        Check.setTextColor(Color.parseColor("#00ff22"));
//                                        next.setClickable(true);
                                        checkname = true;
                                    } else {
                                        Check.setText("사용불가능한 닉네임입니다.");
                                        Check.setTextColor(Color.parseColor("#ff0000"));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("체크", "onFailure: " + t);
                            }
                        });
                    }
                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText n = findViewById(R.id.editNickname);
                String nickname = n.getText().toString();
                if (checkname == false) {
                    Toast.makeText(Signup_prof.this, "닉네임 중복확인을 먼저 진행해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("insert", email + "/" + PW+"/"+phone_number);
                    ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    Call<Signup_model> call = apiInterface.insertaccount(email, PW, nickname,phone_number);
                    filepath = getRealPathFromURI(Uri,nickname);
                    UpdatePhoto(nickname);
                    call.enqueue(new Callback<Signup_model>() {
                        @Override
                        public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                            if (response.body() != null) {
                                if (response.body().getResponse().equals("success")) {
                                    Toast T = Toast.makeText(getApplicationContext(), "회원가입 완료!", Toast.LENGTH_SHORT);
                                    T.show();
                                    Log.e("insert", response.body().getE_mail() + "/" + response.body().getPW() + "/" + response.body().getNickname());
                                    Intent I = new Intent(getApplicationContext(), Login.class);
                                    startActivity(I);
                                    finish();
                                } else {
                                    Toast T = Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT);
                                    T.show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Signup_model> call, Throwable t) {
                            Log.e("insert", t.toString());
                        }
                    });
                }
            }
//
        });
    }

    public void getcamera() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Log.e("이미지 URI", String.valueOf(data.getData()));
//                Glide.with(getApplicationContext()).load(data.getData()).into((ImageView) findViewById(R.id.profilebtn));
                ((ImageButton) findViewById(R.id.profilebtn)).setImageBitmap(imageBitmap);
            }
        }
        if (requestCode == REQUEST_Image) {
            if (resultCode == RESULT_OK) {
                Uri = data.getData();
                Log.e("onActivityResult: ", Uri.toString());
                Glide.with(getApplicationContext()).load(Uri).into((ImageView) findViewById(R.id.profilebtn));
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void getG() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(i, REQUEST_Image);
    }

    private void UpdatePhoto(String nickname) {
        if (filepath != null) {
            ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
            File file = new File(filepath);
            Log.e("realPath",filepath);
            if (!file.exists()) {       // 원하는 경로에 폴더가 있는지 확인
                file.mkdirs();    // 하위폴더를 포함한 폴더를 전부 생성
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploaded_file", nickname.trim(), requestBody);
            Call<Signup_model> call = apiInterface.uploadImage(fileToUpload);
            call.enqueue(new Callback<Signup_model>() {
                @Override
                public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                    Log.e("UPdatePhoto",response.body().getResponse());
                }
                @Override
                public void onFailure(Call<Signup_model> call, Throwable t) {
                    Log.e("updatephoto 연결 실패",t.toString());
                }
            });
        }
    }
    private String getRealPathFromURI(Uri contentUri,String nickname) {
        ContentResolver resolver = getContentResolver();
        InputStream instream = null;
        Bitmap imgBitmap = null;
        String path = null;
        
        
        try {
            instream = resolver.openInputStream(contentUri);
            imgBitmap = BitmapFactory.decodeStream(instream);
            path = saveBitmapToJpeg(imgBitmap,nickname);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("테스트 로그", "진입");
        try {
            instream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
    public String saveBitmapToJpeg(Bitmap bitmap,String imgName) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
//            Toast.makeText(getApplicationContext(), tempFile.getPath(), Toast.LENGTH_SHORT).show();
            return getCacheDir()+"/"+imgName;
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
            return null;
        }
    } 

}