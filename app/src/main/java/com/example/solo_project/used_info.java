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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private PagerAdapter adapter;

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
        adapter = new PagerAdapter(this, images);
        sliderViewPager.setAdapter(adapter);
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
        setupIndicators(images.size());
        adapter.setOnItemClickListener(new main_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent i = new Intent(used_info.this,Chatting_Image_View.class);
                i.putExtra("image_url",images.get(position));
                startActivity(i);
            }
        });
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(used_info.this,chating.class);
                I.putExtra("my_nickname",MyNickname);
                I.putExtra("sender",i.getStringExtra("nickname"));
                ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                Call<String> call = apiInterface.chat_room_check(MyNickname,i.getStringExtra("nickname"));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body() != null){
                            Log.e("리스폰스 확인",response.body());
                            DBHelper myDb = new DBHelper(used_info.this);
                            try{
                                myDb.insert_data(Integer.parseInt(response.body()),i.getStringExtra("nickname"),i.getStringExtra("nickname"),MyNickname,0);
                            }
                            catch (NumberFormatException ex){
                                Toast.makeText(used_info.this, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            }
                            I.putExtra("room_num",response.body());
                            startActivity(I);
                        }else{
                            Toast.makeText(used_info.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
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