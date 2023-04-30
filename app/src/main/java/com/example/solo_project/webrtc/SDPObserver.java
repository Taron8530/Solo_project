package com.example.solo_project.webrtc;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SDPObserver implements SdpObserver {
    @Override
    public void onCreateSuccess(SessionDescription sdp) {}

    @Override
    public void onSetSuccess() {}

    @Override
    public void onCreateFailure(String error) {}

    @Override
    public void onSetFailure(String error) {}
}

