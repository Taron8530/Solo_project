package com.example.solo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class Profile_view extends AppCompatActivity {
    private TextView nickname_view;
    private ImageView profile_view;
    private String nickname;
    private ViewPager2 viewPager;

    private history_content_adapter history_content_adapter;

    private frag_salehistory_ing frag_salehistory_ing;
    private frag_salehistory_suc frag_salehistory_suc;
    private TabLayout tabLayout;
    private TextView back;
    private TabLayout tl;
    final List<String> tabel = Arrays.asList("판매중","판매완료");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        nickname_view = findViewById(R.id.profile_nickname);
        profile_view = findViewById(R.id.profile_image);
        Intent i = getIntent();
        nickname = i.getStringExtra("nickname");
        Log.e("닉네임 확인",nickname);
        nickname_view.setText(nickname);
        Glide.with(getApplicationContext())

                .load("http://35.166.40.164/profile/"+nickname+".png")
                .circleCrop()
                .override(600,600)
                .error(R.drawable.app_icon)
                .into(profile_view);
        back = findViewById(R.id.profile_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        createFragment();

        createViewpager();
//
//        settingTabLayout();
    }
    private void createFragment() {
        frag_salehistory_ing = new frag_salehistory_ing(nickname);

        frag_salehistory_suc = new frag_salehistory_suc();


    }

    private void createViewpager() {

        viewPager = (ViewPager2) findViewById(R.id.profile_viewpager_control);

        tl = (TabLayout) findViewById(R.id.tablayout_profile_control);

        history_content_adapter = new history_content_adapter(getSupportFragmentManager(), getLifecycle());

        history_content_adapter.addFragment(frag_salehistory_ing);

        history_content_adapter.addFragment(frag_salehistory_suc);

        viewPager.setAdapter(history_content_adapter);

        new TabLayoutMediator(tl, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView Tx = new TextView(getApplicationContext());
                Tx.setGravity(Gravity.CENTER);
                Tx.setText(tabel.get(position));
                tab.setCustomView(Tx);

            }
        }).attach();

    }

}