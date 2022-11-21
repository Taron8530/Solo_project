package com.example.solo_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class used_info extends AppCompatActivity {
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private TextView nickname;
    private TextView used_name;
    private TextView price;
    private TextView detail;
    private Button chat_btn;
    private ImageView profile;
    private int image_size;
    private String MyNickname;

    private ArrayList<String> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_info);
        Intent i = getIntent();
        nickname = findViewById(R.id.used_info_nickname);
        detail = findViewById(R.id.used_info_detail);
        price = findViewById(R.id.used_info_price);
        used_name = findViewById(R.id.used_info_name);
        profile = findViewById(R.id.user_profile);
        chat_btn = findViewById(R.id.go_chating);
        images = new ArrayList<>();
        MyNickname = i.getStringExtra("my_nickname");
        nickname.setText("판매자:   " +i.getStringExtra("nickname"));
        detail.setText("설명글 \n\n\n"+i.getStringExtra("detail"));
        price.setText(i.getStringExtra("price")+" 원");
        used_name.setText(i.getStringExtra("used_name"));
        Glide.with(this)
                .load("http://35.166.40.164/profile/"+i.getStringExtra("nickname")+".png")
                .circleCrop()
                .into(profile);
        image_size = i.getIntExtra("image_size",0);
        sliderViewPager = findViewById(R.id.viewpager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);
        if(image_size == 0){
            FrameLayout F = findViewById(R.id.viewpager_frame);
            F.setVisibility(View.GONE);
        }
        for(int j =0;j<image_size;j++){
            Log.e("number check",i.getStringExtra("num")+j);
            images.add("http://35.166.40.164//used_image/"+i.getStringExtra("num")+j+".jpeg");
        }
        sliderViewPager.setAdapter(new PagerAdapter(this, images));
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
        setupIndicators(images.size());
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(used_info.this,chating.class);
                I.putExtra("my_nickname",MyNickname);
                I.putExtra("sender",i.getStringExtra("nickname"));
                startActivity(I);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Profile_view.class);
                intent.putExtra("nickname",i.getStringExtra("nickname"));
                startActivity(intent);
            }
        });
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Profile_view.class);
                intent.putExtra("nickname",i.getStringExtra("nickname"));
                startActivity(intent);
            }
        });
    }
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

}