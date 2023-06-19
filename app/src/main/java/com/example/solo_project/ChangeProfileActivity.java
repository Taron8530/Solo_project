package com.example.solo_project;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeProfileActivity extends AppCompatActivity {
    private String TAG = "ChangeProfileActivity";
    private String nickname;
    private ImageView profileImage;
    private TextView nickname_view;
    private int REQUEST_Image = 1000;
    private Button submit;
    private boolean checked = false; // 0 이면 변경사항이 없음 1이면 변경사항이 있음.
    private ActivityResultLauncher<Intent> photoActivityForResult;
    private String filepath;

    @Override
    protected void onStart() {
        super.onStart();
        photoActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            Glide.with(ChangeProfileActivity.this)
                                    .load(uri)
                                    .override(600,600)
                                    .error(R.drawable.app_icon)
                                    .into(profileImage);
                            checked = true;
                            filepath = getRealPathFromURI(uri,nickname);
                        }
                    }
                }
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        init_view();
    }
    private void init_view(){
        setTitle("프로필 사진 변경");
        profileImage = findViewById(R.id.change_profile_profile);
        nickname_view = findViewById(R.id.change_profile_nickname);
        Intent i = getIntent();
        nickname = i.getStringExtra("nickname");
        nickname_view.setText(nickname);
        submit = findViewById(R.id.change_profile_submit);
        Glide.with(ChangeProfileActivity.this)
                .load("http://35.166.40.164/profile/"+nickname+".png")
                .override(600,600)
                .error(R.drawable.app_icon)
                .into(profileImage);
        init_ClickListener();
    }
    private void init_ClickListener(){
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_PICK);
                photoActivityForResult.launch(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("변경 사항 저장");
        if (checked){
            builder.setMessage("확인 버튼을 누르시면 변경사항이 저장됩니다.");
        }else{
            builder.setMessage("변경사항이 없습니다. 프로필을 변경하고 싶으시다면 취소 버튼을 누르세요.");
        }

        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소 버튼이 클릭된 경우 실행할 코드를 작성하세요.
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checked) {
                    uploadFile();
                }else {
                    finish();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
    private void uploadFile(){
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
                    Log.e(TAG,response.body().getResponse());
                    if(response.isSuccessful()){
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Signup_model> call, Throwable t) {
                    Log.e(TAG,t.toString());
                }
            });
        }
    }
}