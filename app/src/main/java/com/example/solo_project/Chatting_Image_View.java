package com.example.solo_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

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