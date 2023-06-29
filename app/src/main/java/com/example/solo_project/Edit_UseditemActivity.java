package com.example.solo_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Edit_UseditemActivity extends AppCompatActivity implements Serializable {
    private String used_Name;
    private String detail;
    private String price;
    private ArrayList<String> del_File_List;
    private List<MultipartBody.Part> add_File_List;
    private ArrayList<String> images;
    private EditText used_Name_EditText;
    private EditText used_Detail_EditText;
    private EditText used_Price_EditText;
    private Edit_Used_Adapter adapter;
    private RecyclerView recyclerView;
    private TextView image_size;
    private String num;
    private String TAG = "아이템 수정";
    private Button add_Btn;
    private Button submit_Btn;
    private List<Uri> uriList = null;
    private ArrayList<String> filepath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_useditem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("판매글 수정");
        layout_init();
        Intent i = getIntent();
        used_Name = i.getStringExtra("used_name");
        detail = i.getStringExtra("detail");
        price = i.getStringExtra("price");
        images = (ArrayList<String>) i.getSerializableExtra("images");
        num = i.getStringExtra("num");
        Log.e(TAG, "onCreate: " + images);
        adapter = new Edit_Used_Adapter(images, Edit_UseditemActivity.this, num);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapter.notifyDataSetChanged();
        used_Name_EditText.setText(used_Name);
        used_Detail_EditText.setText(detail);
        used_Price_EditText.setText(String.valueOf(price));
        used_Price_EditText.addTextChangedListener(new UsedAddCustomTextWatchar(used_Price_EditText));
        if (images.size() != 0 && images != null) {
            image_size.setText(images.size() + "/5");
            if (images.size() >= 5) {
                image_size.setTextColor(Color.parseColor("#ff0000"));
            }
        }

        adapter.setOnItemClickListener(new main_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                del_File_List.add(images.get(position));
                Log.e(TAG, "onItemClick: " + del_File_List);
                images.remove(position);
                adapter.notifyDataSetChanged();
                image_size.setText(images.size() + "/5");


            }
        });
        add_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Edit_UseditemActivity.this, "5장까지 선택 가능합니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 2222);
            }
        });
        submit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean condition = used_Name_EditText.getText().toString().trim().length() <= 0 || used_Detail_EditText.getText().toString().trim().length() <= 0 || used_Name_EditText.getText().toString().trim().length() <= 0 || used_Price_EditText.getText().toString().trim().length() <= 0;
                if (condition) {
                    Toast.makeText(Edit_UseditemActivity.this, "게시글을 모두 채우지 않으면 안됩니다!", Toast.LENGTH_SHORT).show();
                } else {
//                    ProgressDialog loagindDialog = ProgressDialog.show(Edit_UseditemActivity.this, "물건 올리는중","잠시만 기다려주세요", true, false);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                    builder.setTitle("게시물").setMessage("올리는중...");
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
                    submit_Btn.setClickable(false);
                    setFilepath(uriList);
                    for (int i = 0; i < filepath.size(); i++) {
                        Log.d(TAG, "onClick: " + filepath.size());
                        Log.d(TAG, "onClick: " + i);
                        add_File_List.add(getMultipart(filepath.get(i), i));
                    }
                    ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    String used_name = used_Name_EditText.getText().toString();
                    String detail = used_Detail_EditText.getText().toString();
                    int price = Integer.parseInt(used_Price_EditText.getText().toString().replaceAll(",",""));
                    if (del_File_List.size() <= 0) {
                        del_File_List.add("Empty");
                    }
                    Call<Signup_model> call = apiInterface.used_update(num, used_name, detail, price, add_File_List, del_File_List, add_File_List.size());
                    call.enqueue(new Callback<Signup_model>() {
                        @Override
                        public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                            Log.e(TAG, "온 리스폰스" + response);
                            if (response.isSuccessful()) {
                                Log.e(TAG, "리스폰스 200 CODE " + response.body().getResponse());
                                if (response.body().getResponse().equals("ok")) {
                                    Toast.makeText(Edit_UseditemActivity.this, "완료.", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Edit_UseditemActivity.this,MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Signup_model> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t);
                        }
                    });

                }
            }
        });

    }

    public void layout_init() {
        recyclerView = findViewById(R.id.update_recyclerview);
        used_Detail_EditText = findViewById(R.id.update_used_detail);
        used_Name_EditText = findViewById(R.id.update_used_name);
        used_Price_EditText = findViewById(R.id.update_used_price);
        add_Btn = findViewById(R.id.update_add_imagebtn);
        image_size = findViewById(R.id.update_image_size);
        submit_Btn = findViewById(R.id.used_update_submit);
        del_File_List = new ArrayList<>();
        add_File_List = new ArrayList<>();
        uriList = new ArrayList<>();
        filepath = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(Edit_UseditemActivity.this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        } else {   // 이미지를 하나라도 선택한 경우
            if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                images.add(imageUri.toString());
                uriList.add(imageUri);
                image_size.setText(images.size() + "/5");
                if (images.size() >= 5) {
                    image_size.setTextColor(Color.parseColor("#ff0000"));
                }
                adapter.notifyDataSetChanged();
            } else {      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if (clipData.getItemCount() + images.size() > 5) {   // 선택한 이미지가 5장 이상인 경우
                    Toast.makeText(Edit_UseditemActivity.this, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                } else {   // 선택한 이미지가 1장 이상 5장 이하인 경우
                    Log.e(TAG, "multiple choice");
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        // 선택한 이미지들의 uri를 가져온다.
                        try {
                            images.add(imageUri.toString());  //uri를 list에 담는다.
                            uriList.add(imageUri);
                            image_size.setText(images.size() + "/5");
                            if (images.size() >= 5) {
                                image_size.setTextColor(Color.parseColor("#ff0000"));
                            }
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
                }
            }
        }
    }

    public String saveBitmapToJpeg(Bitmap bitmap, String imgName) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);    //회전시킬 각도
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, //bmp를 matrix로 회전하여 newBmp에
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);// 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            newBmp.compress(Bitmap.CompressFormat.JPEG, 60, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
//            Toast.makeText(getApplicationContext(), tempFile.getPath(), Toast.LENGTH_SHORT).show();
            return getCacheDir() + "/" + imgName;
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private MultipartBody.Part getMultipart(String filepath, int i) {
        File file = new File(filepath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploaded_file" + i, String.valueOf(i), requestBody);
        Log.e(TAG, "getMultipart: " + fileToUpload);
        return fileToUpload;
    }

    private void setFilepath(List<Uri> uri) {
        ContentResolver resolver = getContentResolver();
        InputStream instream = null;
        for (int i = 0; i < uri.size(); i++) {
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

            filepath.add(saveBitmapToJpeg(imgBitmap, String.valueOf(i)));
            Log.e(TAG, "setFilepath: " + filepath.get(i));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}