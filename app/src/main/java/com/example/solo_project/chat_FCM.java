package com.example.solo_project;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;

public class chat_FCM extends FirebaseMessagingService{
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
//        //token을 서버로 전송
//        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
//        Call<Signup_model> call = apiInterface.profile_sel(verify);
        Log.e("FCM_token",token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //수신한 메시지를 처리
    }
}
