package com.example.solo_project;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class used_add extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView image_size;
    private int size = 0;
    private Button btn;
    private Button submit;
    private String TAG = "frag_addused";
    private List<Uri> uriList = null;
    private List<MultipartBody.Part> multi;
    private ArrayList<String> filepath;
    private MultiImageAdapter adapter;
    private String nickname = null;
    private EditText e_used_name;
    private EditText e_detail;
    private EditText e_price;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_add);
        recyclerView = findViewById(R.id.add_recyclerview);
        submit = findViewById(R.id.used_add_submit);
        btn = findViewById(R.id.multi_imagebtn);
        image_size = findViewById(R.id.image_size);
        uriList = new ArrayList<>();
        multi = new ArrayList<>();
        filepath = new ArrayList<>();
        Intent i = getIntent();
        nickname = i.getStringExtra("nickname");
        e_used_name = findViewById(R.id.add_used_name);
        e_price = findViewById(R.id.add_used_price);
        e_detail = findViewById(R.id.add_used_detail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(used_add.this, "5장까지 선택 가능합니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (e_used_name.getText().toString().equals("") || e_detail.getText().toString().equals("") || e_price.getText().toString().equals("")) {
                    Toast.makeText(used_add.this, "게시글을 모두 채워주세요", Toast.LENGTH_SHORT).show();
                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                    builder.setTitle("게시물").setMessage("올리는중...");
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
                    submit.setClickable(false);
                    setFilepath(uriList);
                    for (int i = 0; i < filepath.size(); i++) {
                        Log.d(TAG, "onClick: " + filepath.size());
                        Log.d(TAG, "onClick: " + i);
                        multi.add(getMultipart(filepath.get(i), i));
                    }
                    apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    Call<Signup_model> call = apiInterface.used_insert(nickname, e_used_name.getText().toString(), e_detail.getText().toString(), Integer.parseInt(e_price.getText().toString()), multi, multi.size(),getTime());
                    call.enqueue(new Callback<Signup_model>() {
                        @Override
                        public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                            if (response.body() != null) {
                                Log.e(TAG, "onResponse: " + response.body().getResponse());
                                Log.e(TAG, "onResponsen: " + response.body().getNickname());
                                if (response.body().getResponse().trim().equals("success")&&response.body().getNickname().equals("성공")) {
//                                    alertDialog.dismiss();
                                    Intent i = new Intent(used_add.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    multi.clear();
                                    Toast.makeText(used_add.this, "잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                    submit.setClickable(true);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Signup_model> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t);
                            submit.setClickable(true);
                        }
                    });
                }

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(used_add.this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);
                size = size+1;
                image_size.setText(size+"/5");
                adapter = new MultiImageAdapter(uriList, used_add.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(used_add.this, LinearLayoutManager.HORIZONTAL, true));
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() + uriList.size()> 5 ){   // 선택한 이미지가 5장 이상인 경우
                    Toast.makeText(used_add.this, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 5장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.
                            size = size + 1;
                            image_size.setText(size+"/5");
                            if(size == 5){
                                image_size.setTextColor(Color.parseColor("#ff0000"));
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                }
            }
        }
    }
    private void setFilepath(List<Uri> uri){
        ContentResolver resolver = getContentResolver();
        InputStream instream = null;
        for(int i = 0;i<uri.size();i++){
            Bitmap imgBitmap = null;
            try {
                instream = resolver.openInputStream(uri.get(i));
                imgBitmap = BitmapFactory.decodeStream(instream);
                instream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            filepath.add(saveBitmapToJpeg(imgBitmap,String.valueOf(i)));
            Log.e(TAG, "setFilepath: "+filepath.get(i));
        }
    }
    public String saveBitmapToJpeg(Bitmap bitmap,String imgName) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
//            Toast.makeText(getApplicationContext(), tempFile.getPath(), Toast.LENGTH_SHORT).show();
            return getCacheDir()+"/"+imgName;
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    private MultipartBody.Part getMultipart(String filepath,int i){
        File file = new File(filepath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploaded_file"+i, String.valueOf(i), requestBody);
        Log.e(TAG, "getMultipart: "+fileToUpload);
        return fileToUpload;
    }
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }
    private String getTime(){
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");;
        return mFormat.format(mDate);
    }
    //    private Bitmap UritoBitmap(String uri){
//        Bitmap bit = null;
//        try {
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
//                bit = ImageDecoder.decodeBitmap(ImageDecoder.createSource());
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        return bit;
//    }
    private String getImageViewToFileResizing(Bitmap bitmap,String dirFile){

        String mUri = "";


        if(bitmap != null) {
            FileOutputStream fout = null;
            try {
                int MAX_IMAGE_SIZE = 150 * 1024; // max final file size
                int compressQuality = 100; // quality decreasing by 5 every loop. (start from 99)
                int streamLength = MAX_IMAGE_SIZE;
                String fname = System.currentTimeMillis() + "_pic.jpg";

                File file = new File(dirFile,fname);
                while(streamLength >= MAX_IMAGE_SIZE){
                    fout = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, fout);
                    streamLength = (int) file.length();
                    compressQuality -= 5;
                }

                fout.flush();
                mUri = file.getPath();


            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                if(fout!=null){
                    try{
                        fout.close();
                    }catch(Exception e){
                    }
                }else{
                }
            }

        }

        return mUri;
    }
}