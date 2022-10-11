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
        Log.e("Main",nickname+"/"+email);
        home_fregment = new F_home();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fregment).commit();
        BottomNavigationView bottom = findViewById(R.id.bottom_menu);
        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fregment).commit();
                        Log.e("프래그먼트", "여긴 홈");
                        return true;
                    case R.id.tab_chating:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, chating_fregment).commit();
                        Log.e("프래그먼트", "여긴 채팅");
                        return true;
                    case R.id.tab_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profile_fregment).commit();
                        Log.e("프래그먼트", "여긴 프로필");
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
        String verify = pref.getString("user_verify", "");
        Log.e("onCreate","찍힘");
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<Signup_model> call = apiInterface.profile_sel(verify);
        call.enqueue(new Callback<Signup_model>() {
            @Override
            public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                if (response.body() != null) {
                    Log.e("MainActivity",response.body().getNickname()+"/"+response.body().getE_mail());
                    email = response.body().getE_mail();
                    nickname = response.body().getNickname();
                    profile_fregment = new F_profile(nickname,email);
                    chating_fregment = new F_chating(nickname);
                }
            }

            @Override
            public void onFailure(Call<Signup_model> call, Throwable t) {
                Log.e("MainActivity",t.toString());
            }
        });
    }
}