package com.example.solo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

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
        FirebaseApp.initializeApp(this);
        select_nickname();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                                           @Override
                                           public void onComplete(@NonNull Task<String> task) {
                                               if (!task.isSuccessful()) {
                                                   Log.w("Token", "Fetching FCM registration token failed", task.getException());
                                                   return;
                                               }
                                               token_update(nickname,task.getResult());
                                               Log.e("Token", String.valueOf(task.getResult()));
                                           }

                                       });
//        test_token();
        Log.e("Main",nickname+"/"+email);
        profile_fregment = new F_profile(nickname,email);
        chating_fregment = new F_chating(nickname);
        home_fregment = new F_home(nickname);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fregment).commit();
        setTitle("물건");
        BottomNavigationView bottom = findViewById(R.id.bottom_menu);
//        MenuItem tab = findViewById(R.id.tab_add);
//        tab.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return false;
//            }
//        })
        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fregment).commit();
                        Log.e("프래그먼트", "여긴 홈");
                        setTitle("물건");
                        return true;
                    case R.id.tab_chating:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, chating_fregment).commit();
                        Log.e("프래그먼트", "여긴 채팅");
                        setTitle("채팅 목록");
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
                        return false;
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
    public void token_update(String nickname,String token){
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<String> call = apiInterface.token_update(token,nickname);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body() != null){
                    Log.e("토큰 저장 성공",response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("토큰 저장",t.toString());
            }
        });
    }
}