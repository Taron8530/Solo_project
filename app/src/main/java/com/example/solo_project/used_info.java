package com.example.solo_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class used_info extends AppCompatActivity implements Serializable {
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private TextView nickname;
    private TextView used_name;
    private TextView price;
    private TextView detail;
    private Button chat_btn;
    private ImageView profile;
    private int image_size = 0;
    private String MyNickname;
    private String receiver;
    private PagerAdapter adapter;
    private TextView exit;
    private String num;
    private String str_price;
    private String str_detail;
    private ArrayList<String> images;
    private ArrayList<String> image_names;
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
        exit = findViewById(R.id.used_info_exit);
        num = i.getStringExtra("num");
        image_names = (ArrayList<String>) i.getSerializableExtra("image_names");
        Log.e("sold_out",num);
        images = new ArrayList<>();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        MyNickname = i.getStringExtra("my_nickname");
        receiver = i.getStringExtra("nickname");
        String t = i.getStringExtra("price");
        str_price = comma_to_int(t);
        str_detail = i.getStringExtra("detail");
        nickname.setText("판매자:   " +receiver);

        detail.setText(str_detail);
        price.setText(str_price+" 원");
        used_name.setText(i.getStringExtra("used_name"));
        Glide.with(this)
                .load("http://35.166.40.164/profile/"+i.getStringExtra("nickname")+".png")
                .circleCrop()
                .error(R.drawable.app_icon)
                .into(profile);
//        image_size = i.getIntExtra("image_size",0);
        if(image_names != null){
            image_size = image_names.size();
        }
        sliderViewPager = findViewById(R.id.viewpager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);
        if(image_size == 0){
            FrameLayout F = findViewById(R.id.viewpager_frame);
            LinearLayout L = findViewById(R.id.layoutIndicators);
            F.setVisibility(View.GONE);
            L.setVisibility(View.GONE);
        }else {
            for (String image_name : image_names) {
                Log.e("number check", image_name);
                images.add("http://35.166.40.164//used_image/" + num + "/" + image_name);
            }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(MyNickname.equals(receiver)){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.used_info_menu, menu);
            chat_btn.setVisibility(View.GONE);
            return true;
        }
        else{
            return false;
        }
    }
    public String comma_to_int(String number){
        if (number.length() == 0) {
            return "";
        }
        long value = Long.parseLong(number);
        DecimalFormat df = new DecimalFormat("###,###");
        String money = df.format(value);
        return money;
    }
    public void sold_out(){
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<String> call = apiInterface.used_sold_out(num,"1");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("sold_out",response.body().toString());
                if(response.body().equals("성공")){
                    Toast.makeText(used_info.this,"성공.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(used_info.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("sold_out",t.toString());
            }
        });
    }
    public void used_update(){
        Intent i = new Intent(this,Edit_UseditemActivity.class);
        i.putExtra("images",image_names);
        i.putExtra("used_name",used_name.getText().toString());
        i.putExtra("detail",str_detail);
        i.putExtra("price",str_price);
//        i.putExtra("nickname",nickname)
        i.putExtra("num",num);
        startActivity(i);
    }
    public void used_delete(){
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<String> call = apiInterface.used_delete(num);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("used_delete",response.body().toString());
                if(response.body().equals("성공")){
                    Toast.makeText(used_info.this,"성공.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(used_info.this,MainActivity.class);
                    startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("used_delete",t.toString());
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.used_sold_out:
                sold_out();
                break;
            case R.id.used_delete:
                used_delete();
                break;
            case R.id.used_edit:
                used_update();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}