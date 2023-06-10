package com.example.solo_project.webrtc;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solo_project.ApiInterface;
import com.example.solo_project.Apiclient;
import com.example.solo_project.F_chating;
import com.example.solo_project.F_home;
import com.example.solo_project.F_profile;
import com.example.solo_project.R;
import com.google.common.collect.ImmutableList;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video_call_Activity extends AppCompatActivity implements OnCall_Choice_ClickListener,OnCallingClickListener{
    private Signaling_Socket socket;
    private String sender;
    private String status;
    private String receiver;
    private CallingFragment callingFragment;
    private CallChoiceFragment callChoiceFragment;
    private VideoCallFragment videoCallFragment;
    private String TAG = "Video_call_activity";
    private boolean setOffer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        try {
            init_();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void init_() throws IOException {
        Intent i = getIntent();
        status = i.getStringExtra("status");
        sender = i.getStringExtra("sender");
        receiver = i.getStringExtra("receiver");
        socket = new Signaling_Socket(sender);
        if(status.equals("call_request")){
            callingFragment = new CallingFragment(socket,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, callingFragment).commit();
        }else{
            callChoiceFragment = new CallChoiceFragment(socket,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, callChoiceFragment).commit();
        }
        socket.setOnItemClickListener(new Signaling_Socket.readMsgListener() {
            @Override
            public void onServerMsgRead(String msg) throws JSONException {
                Log.d(TAG, "onServerMsgRead: "+msg);
                JSONObject jsonObject = new JSONObject(msg);
                String type = (String) jsonObject.get("type");
                Log.d(TAG, "onServerMsgRead: "+type);
                if(type.equals("call_accept")){
                    Log.d(TAG, "onServerMsgRead: 첫번째 조건문 호출됨");
                    videoCallFragment = new VideoCallFragment(socket,true,sender,receiver);
                    getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, videoCallFragment).commit();
                    onSpeaker();
                }else if(type.equals("call_failed")) {
                    Log.d(TAG, "onServerMsgRead: 두번째 조건문 호출됨");
                    finish();
                    Log.d(TAG, "onServerMsgRead: 두번째 조건문 호출됨2");
                }
            }
        });
    }

    @Override
    public void onFailedButtonClicked() {
        // 콜을 받았을때 취소 버튼 클릭
        socket.sendMsg(sender,receiver,"call_failed");
        finish();
    }

    @Override
    public void onAcceptButtonClicked() {
        // 콜을 받았을때 수락 버튼 클릭
        socket.sendMsg(sender,receiver,"call_accept");
        videoCallFragment = new VideoCallFragment(socket,false,sender,receiver);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, videoCallFragment).commit();
        onSpeaker();
    }
    private void onSpeaker(){
        // Audio Manager를 생성할 때 Context 객체를 전달해야 함
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


// 전화 스피커폰 모드로 설정
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
    }
    private void offSpeaker(){
        // Audio Manager를 생성할 때 Context 객체를 전달해야 함
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


// 전화 스피커폰 모드로 설정
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);
    }
    @Override
    public void onCancelButtonClicked() {
        // 콜을 걸다가 취소를 눌렀을때 버튼 클릭
        socket.sendMsg(sender,receiver,"call_cancel");
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.socket_disconnect(sender);
            offSpeaker();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}