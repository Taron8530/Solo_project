package com.example.solo_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
/////
// params.put("cid", "TC0ONETIME"); // 가맹점 코드
//         params.put("partner_order_id", "1001"); // 가맹점 주문 번호
//         params.put("partner_user_id", "gorany"); // 가맹점 회원 아이디
//         params.put("item_name", productName); // 상품 이름
//         params.put("quantity", "1"); // 상품 수량
//         params.put("total_amount", productPrice); // 상품 총액
//         params.put("tax_free_amount", "0"); // 상품 비과세
//         params.put("approval_url", "https://www.naver.com/success"); // 결제 성공시 돌려 받을 url 주소
//         params.put("cancel_url", "https://www.naver.com/cancel"); // 결제 취소시 돌려 받을 url 주소
//         params.put("fail_url", "https://www.naver.com/fali"); // 결제 실패시 돌려 받을 url 주소

public class PaymentActivity extends AppCompatActivity {
    private Retrofit retrofit;

    String cid = "TC0ONETIME";
    String order_Id = "1004";
    String user_Id;
    String item_name;
    String total_Amount;
    String free_Amount = "0";
    String approval_url = "http://www.taron.duckdns.org/sucsec.php";
    String cancel = "http://www.taron.duckdns.org/cancel";
    String fail = "http://www.taron.duckdns.org/fail";
    WebView webView;
    MyWebViewClient myWebViewClient;
    String pgToken;
    String tidPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent i = getIntent();
        user_Id=i.getStringExtra("nickname");
        item_name = i.getStringExtra("itemname");
        total_Amount = i.getStringExtra("total_amount");
        myWebViewClient = new MyWebViewClient();
        Log.e("카카오페이", "onCreate: "+item_name+total_Amount );
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);
        myWebViewClient.readyRequest();
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("Debug", "url" + url);
            if (url != null && url.contains("pg_token=")) {
                String pg_Token = url.substring(url.indexOf("pg_token=") + 9);
                pgToken = pg_Token;
                approveRequest();
//                update_token(pg_Token);
//                approveRequest(); // 결제요청
//                finish();
                return false;

            } else if (url != null && url.startsWith("intent://")) {
                try {
                    Log.e("카카오페이","url 변경");
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(url.contains("taron.duckdns.org")){
                finish();
            }
            view.loadUrl(url);
            return false;
//            return super.shouldOverrideUrlLoading(view, url);
        }
        public void update_token(String token){
            ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
            Call<Signup_model> call = apiInterface.update_Credit(user_Id,total_Amount,token);
            call.enqueue(new Callback<Signup_model>() {
                @Override
                public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                    Log.e("카카오페이 서버", String.valueOf(response));
                    if(response.isSuccessful()){
                        Log.e("카카오페이 서버",response.body().getResponse());
                        if(response.body().getResponse().equals("ok")){
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Signup_model> call, Throwable t) {
                    Log.e("카카오페이",t.toString());
                }
            });
        }
        public void approveRequest(){
            if(retrofit == null){
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://kapi.kakao.com/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
            }
            Log.e("카카오페이","결제요청"+pgToken);
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Map<String, String> params = new HashMap<>();
            params.put("cid", "TC0ONETIME");
            params.put("tid", tidPin);
            params.put("partner_order_id", order_Id);
            params.put("partner_user_id", user_Id);
            params.put("pg_token", pgToken);
            params.put("total_amount", total_Amount);
            Log.e("카카오페이",params.toString());
            Call<Payment> call = apiInterface.kakao_pay_approve_request(params);
            call.enqueue(new Callback<Payment>() {
                @Override
                public void onResponse(Call<Payment> call, Response<Payment> response) {
                    //결제 완료 내 서버로 결제 완료 요청 리스폰스 -> 내 서버로 온 결제 확인 후 토큰 충전
                    Log.e("카카오페이 결제",response.toString());
                    if(response.isSuccessful()){
                        Toast.makeText(PaymentActivity.this, "결재가 완료되었습니다", Toast.LENGTH_SHORT).show();
                        Log.e("카카오페이 결제", String.valueOf(response));
                    }
                }

                @Override
                public void onFailure(Call<Payment> call, Throwable t) {
                    Log.e("카카오페이??",String.valueOf(t));
                }
            });
        }

        public void readyRequest() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://kapi.kakao.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Log.e("카카오페이", "요청함");
            ApiInterface apiclient = retrofit.create(ApiInterface.class);
            Map<String, String> params = new HashMap<>();
            params.put("cid", cid); // 가맹점 코드
            params.put("partner_order_id", order_Id); // 가맹점 주문 번호
            params.put("partner_user_id", user_Id); // 가맹점 회원 아이디
            params.put("item_name", item_name); // 상품 이름
            params.put("quantity", "1"); // 상품 수량
            params.put("total_amount", total_Amount); // 상품 총액
            params.put("tax_free_amount", free_Amount); // 상품 비과세
            params.put("approval_url", approval_url); // 결제 성공시 돌려 받을 url 주소
            params.put("cancel_url", cancel); // 결제 취소시 돌려 받을 url 주소
            params.put("fail_url", fail); // 결제 실패시 돌려 받을 url 주소
            Call<Kakao_pay_item_model> call = apiclient.kakao_pay_ready_request(params);
            call.enqueue(new Callback<Kakao_pay_item_model>() {
                @Override
                public void onResponse(Call<Kakao_pay_item_model> call, Response<Kakao_pay_item_model> response) {
                    Log.e("카카오페이", "응답받음0" + response + "/" + response.body());
                    if (response.isSuccessful()) {
                        Log.e("카카오페이", "응답받음");
                        Log.e("카카오페이", response.body().getTid());
                        Log.e("카카오페이", response.body().getUrl());
                        webView.loadUrl(response.body().getUrl());
                        tidPin = response.body().getTid();

                    }
                }

                @Override
                public void onFailure(Call<Kakao_pay_item_model> call, Throwable t) {
                    Log.e("카카오페이", "응답없음: " + t.toString());
                    Toast.makeText(PaymentActivity.this, "인터넷상태를 확인해주세요!", Toast.LENGTH_SHORT).show();
                }

            });

        }
    }
}