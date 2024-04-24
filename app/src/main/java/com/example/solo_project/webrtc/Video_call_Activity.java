package com.example.solo_project.webrtc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.solo_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Video_call_Activity extends AppCompatActivity implements OnCall_Choice_ClickListener, OnCallingClickListener {
    private Signaling_Socket socket;
    private String sender;
    private String status;
    private String receiver;
    private CallingFragment callingFragment;
    private CallChoiceFragment callChoiceFragment;
    private VideoCallFragment videoCallFragment;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String TAG = "Video_call_activity";
    private boolean setOffer = false;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

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
        requestPermissions();
        Intent i = getIntent();
        status = i.getStringExtra("status");
        sender = i.getStringExtra("sender");
        receiver = i.getStringExtra("receiver");
        socket = new Signaling_Socket(sender);
        if (status.equals("call_request")) {
            callingFragment = new CallingFragment(socket, receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, callingFragment).commit();
        } else {
            callChoiceFragment = new CallChoiceFragment(socket, receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, callChoiceFragment).commit();
        }
        socket.setOnItemClickListener(new Signaling_Socket.readMsgListener() {
            @Override
            public void onServerMsgRead(String msg) throws JSONException {
                Log.d(TAG, "onServerMsgRead: " + msg);
                JSONObject jsonObject = new JSONObject(msg);
                String type = (String) jsonObject.get("type");
                Log.d(TAG, "onServerMsgRead: " + type);
                if (type.equals("call_accept")) {
                    Log.d(TAG, "onServerMsgRead: 첫번째 조건문 호출됨");
                    videoCallFragment = new VideoCallFragment(socket, true, sender, receiver);
                    getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, videoCallFragment).commit();
                    onSpeaker();
                } else if (type.equals("call_failed")) {
                    Log.d(TAG, "onServerMsgRead: 두번째 조건문 호출됨");
                    finish();
                    Log.d(TAG, "onServerMsgRead: 두번째 조건문 호출됨2");
                }
            }
        });
    }

    @Override
    public void onFailedButtonClicked() {
        socket.sendMsg(sender, receiver, "call_failed");
        finish();
    }

    @Override
    public void onAcceptButtonClicked() {
        socket.sendMsg(sender, receiver, "call_accept");
        videoCallFragment = new VideoCallFragment(socket, false, sender, receiver);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_call_container, videoCallFragment).commit();
        onSpeaker();
    }

    private void onSpeaker() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
    }

    private void offSpeaker() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);
    }

    @Override
    public void onCancelButtonClicked() {
        // 콜을 걸다가 취소를 눌렀을때 버튼 클릭
        socket.sendMsg(sender, receiver, "call_cancel");
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

    private void requestPermissions() {
        ActivityCompat.requestPermissions(Video_call_Activity.this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Video_call_Activity.this, "권한 요청 거부됨. 앱이 제대로 작동하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show();
                    break;
                } else {

                }
            }
        }
    }
}
