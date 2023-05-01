package com.example.solo_project.webrtc;

import android.util.Log;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SDPObserver implements SdpObserver {
    String TAG = "SDPObserver";
    @Override
    public void onCreateSuccess(SessionDescription sdp) {
        Log.e(TAG,"onCreateSuccess 호출 : "+sdp.description);
    }

    @Override
    public void onSetSuccess() {
        Log.e(TAG,"onSetSuccess 호출 : ");
    }

    @Override
    public void onCreateFailure(String error) {
        Log.e(TAG,"onCreateFailure 호출 : "+error);

    }

    @Override
    public void onSetFailure(String error) {
        Log.e(TAG,"onSetFailure 호출 : "+error);
    }
}

