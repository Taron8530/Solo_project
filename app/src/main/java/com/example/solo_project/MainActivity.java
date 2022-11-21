package com.example.solo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private F_home home_fregment;
    private F_chating chating_fregment;
    private F_profile profile_fregment;
    public static String nickname;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_nickname();
//        test_token();
        Log.e("Main",nickname+"/"+email);
        profile_fregment = new F_profile(nickname,email);
        chating_fregment = new F_chating(nickname);
        home_fregment = new F_home(nickname);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fregment).commit();
        setTitle("홈");
        BottomNavigationView bottom = findViewById(R.id.bottom_menu);
        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fregment).commit();
                        Log.e("프래그먼트", "여긴 홈");
                        setTitle("홈");
                        return true;
                    case R.id.tab_chating:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, chating_fregment).commit();
                        Log.e("프래그먼트", "여긴 채팅");
                        setTitle("채팅");
                        return true;
                    case R.id.tab_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profile_fregment).commit();
                        Log.e("프래그먼트", "여긴 프로필");
                        setTitle("프로필");
                        return true;
                    case R.id.tab_add:
                        Intent I = new Intent(MainActivity.this,used_add.class);
                        I.putExtra("nickname",nickname);
                        startActivity(I);
                        return true;
                }
                return false;
            }
        });
    }
    public void select_nickname(){
        Log.e("MainActivity","select_nickname 호출");
        SharedPreferences pref = getSharedPreferences("user_verify", Context.MODE_PRIVATE);
        nickname = pref.getString("user_nickname","");
        email = pref.getString("user_email","");
        Log.e("onCreate","찍힘");
    }
    public void test_token(){
        Log.e("FCM_token", FirebaseMessaging.getInstance().getToken().getResult());
    }
}