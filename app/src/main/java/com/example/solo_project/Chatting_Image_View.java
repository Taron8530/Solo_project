package com.example.solo_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.LongFunction;

public class Chatting_Image_View extends AppCompatActivity {
    Bitmap bitmap;
    PhotoView photoView;
    TextView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_chatting_image_view);
        Intent i = getIntent();
        String Image_Url = i.getStringExtra("image_url");
        photoView = findViewById(R.id.chating_image_View);
        exit = findViewById(R.id.image_view_exit);
        getImageFromURL(Image_Url);
        Setting_Listener();
        Log.e("image_view",Image_Url);
//        photoView.setImageBitmap(bitmap);
    }
    public void getImageFromURL(String imageURL){
        Glide.with(getApplicationContext()).asBitmap().load(imageURL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        photoView.setImageBitmap(resource);
                    }
                });
//
//        Thread uThread = new Thread() {
//            @Override
//            public void run(){
//                try{
//                    // 이미지 URL 경로
//                    URL url = new URL(imageURL);
//
//                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
//                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                    conn.setDoInput(true); // 서버로부터 응답 수신
//                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
//
//                    InputStream is = conn.getInputStream(); //inputStream 값 가져오기
//                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
//
//                }catch (MalformedURLException e){
//                    e.printStackTrace();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        uThread.start();
//        try{
//            //메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야 한다.
//            //join() 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리도록 한다.
//            //join() 메서드는 InterruptedException을 발생시킨다.
//            uThread.join();
//
//            //작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
//            //UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지 지정
//            photoView.setImageBitmap(bitmap);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
    }
    public void Setting_Listener(){
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}