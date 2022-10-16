package com.example.solo_project;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaSession2;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class Login extends AppCompatActivity {
    String TAG = "Login Acticity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sf = getSharedPreferences("user_verify",MODE_PRIVATE);
        String v = sf.getString("user_verify","");
        if(v!=""){
            Intent I = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(I);
            finish();
        }else{
            TextView Signup = findViewById(R.id.Signup); //회원가입 액티비티 이동
            Signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent I = new Intent(getApplicationContext(),Signup.class);
                    startActivity(I);
                    finish();
                }
            });
            TextView findpw = findViewById(R.id.findPw);
            findpw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getApplicationContext(),Signup_prof.class);
                    startActivity(i);
                }
            });
            Button submit = findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText e = findViewById(R.id.e_mail);
                    EditText P = findViewById(R.id.password_login);
                    String email = e.getText().toString().trim();
                    String PW = P.getText().toString().trim();
                    if(email.equals("")||PW.equals("")){
                        Toast.makeText(Login.this,"이메일이나 패스워드를 제대로 입력해주세요!",Toast.LENGTH_SHORT).show();
                    }else {
                        selectTest(email, PW);
                    }
                }
            });
            ImageButton kakao_btn = findViewById(R.id.kakao_login_btn);
            kakao_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("getHash",getKeyHash(Login.this));

                    KakaoSdk.init(Login.this, "8666d84ac4195731f47eea9eae8e7c17");

                    if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(Login.this)){
                        UserApiClient.getInstance().loginWithKakaoTalk(Login.this,(oAuthToken, error) -> {
                            if (error != null) {
                                Log.e("tag", "로그인 실패", error);
                                Toast myToast = Toast.makeText(getApplicationContext(),"로그인 실패 다시 시도해주세요.".toString(), Toast.LENGTH_SHORT);
                                myToast.show();
                            } else if (oAuthToken != null) {
                                Log.i("tag", "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                                Requestme();
                            }
                            return null;
                        });
                    }else{
                        UserApiClient.getInstance().loginWithKakaoAccount(Login.this, (Token,error)->{
                            if(error != null){
                                Log.e("tag", "로그인 실패", error);
                            }else if(Token != null){
                                Log.i("tag", "로그인 성공(토큰) : " + Token.getAccessToken());
                                Requestme();
                            }
                            return null;
                        });
                    }

                }
            });
        }
        ImageButton Google_btn = findViewById(R.id.naver_login_btn);
        Google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Login.this,MainActivity.class);
                startActivity(I);

            }
        });
    }
    public static String getKeyHash(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void Requestme(){
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e("tag", "사용자 정보 요청 실패", meError);
            } else {
                System.out.println("로그인 완료");
                Log.e("tag", user.toString());
                {
                    ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    Call<Signup_model> call = apiInterface.getLogin(user.getKakaoAccount().getEmail(),"");
                    call.enqueue(new Callback<Signup_model>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<Signup_model> call, @NonNull Response<Signup_model> response)
                        {
                            boolean test = response.isSuccessful();
                            Log.e("selectTest","연결 후");
                            System.out.println("selectTest"+test);
                            if (response.isSuccessful() && response.body() != null)
                            {
                                String t = response.toString();
                                Log.e("selectTest",t);
                                String getted_email = response.body().getE_mail();
                                String getted_NK = response.body().getNickname();
                                String massage = response.body().getResponse();
                                String verify_c = response.body().getVerify();
                                Log.e("selectTest()", "서버에서 이메일 : " + getted_email + ", 서버에서 받아온 닉네임 : " + getted_NK+"메세지: "+massage+"/인증코드:"+verify_c);
                                if(massage.equals("failed")){
                                    Intent I = new Intent(Login.this,Signup_prof.class);
                                    I.putExtra("email",user.getKakaoAccount().getEmail());
                                    I.putExtra("nickname",user.getKakaoAccount().getProfile().getNickname());
                                    I.putExtra("Image",user.getKakaoAccount().getProfile().getProfileImageUrl());
                                    startActivity(I);
                                    finish();
                                }else{
                                    SharedPreferences sv = getSharedPreferences("user_verify",MODE_PRIVATE);
                                    SharedPreferences.Editor E = sv.edit();
                                    E.putString("user_verify",verify_c);
                                    E.commit();
                                    Intent i = new Intent(Login.this,MainActivity.class);
                                    startActivity(i);
                                    Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                            else {
                                Log.e("selectTest", "연결이 안댐");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Signup_model> call, @NonNull Throwable t)
                        {
                            Toast.makeText(Login.this,"연결이 원활하지 않아 나중에 다시 시도하십시오",Toast.LENGTH_SHORT).show();
                            Log.e("selectTest()", "에러 : " + t.getMessage());
                        }
                    });
                }

            }
            return null;
        });
    }
    public void findPW(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.findpw, null, false);
//        builder.setView(view);
//
//        final AlertDialog dialog = builder.create();
//        dialog.show();
    }

    public void selectTest(String email,String PW){
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<Signup_model> call = apiInterface.getLogin(email,PW);
        final String Nickname;
        Log.d("selectTest", "연결 하기 전 ");
        call.enqueue(new Callback<Signup_model>()
        {
            @Override
            public void onResponse(@NonNull Call<Signup_model> call, @NonNull Response<Signup_model> response)
            {
                boolean test = response.isSuccessful();
                Log.e("selectTest","연결 후");
                System.out.println("selectTest"+test);
                if (response.isSuccessful() && response.body() != null)
                {
                    String t = response.toString();
                    Log.e("selectTest",t);
                    String getted_email = response.body().getE_mail();
                    String getted_NK = response.body().getNickname();
                    String massage = response.body().getResponse();
                    String verify_c = response.body().getVerify();
                    Log.e("selectTest()", "서버에서 이메일 : " + getted_email + ", 서버에서 받아온 닉네임 : " + getted_NK+"메세지: "+massage+"/인증코드:"+verify_c);
                    if(massage.equals("failed")){
                        Toast T = Toast.makeText(Login.this,"아이디 또는 패스워드를 확인해주세요",Toast.LENGTH_LONG);
                        T.show();
                    }else{
                        SharedPreferences sv = getSharedPreferences("user_verify",MODE_PRIVATE);
                        SharedPreferences.Editor E = sv.edit();
                        E.putString("user_verify",verify_c);
                        E.commit();
                        Intent i = new Intent(Login.this,MainActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    Log.e("selectTest", "연결이 안댐");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Signup_model> call, @NonNull Throwable t)
            {
                Log.e("selectTest()", "에러 : " + t.getMessage());
            }
        });
    }
}