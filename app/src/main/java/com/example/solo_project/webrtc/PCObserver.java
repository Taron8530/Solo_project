package com.example.solo_project.webrtc;

import android.util.Log;

import org.webrtc.CandidatePairChangeEvent;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;

public class PCObserver implements PeerConnection.Observer{
    String TAG = "PCObserver";
    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.e(TAG, "onSignalingChange: "+signalingState);

    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.e(TAG, "onIceConnectionChange: "+iceConnectionState);
    }

    @Override
    public void onStandardizedIceConnectionChange(PeerConnection.IceConnectionState newState) {
        PeerConnection.Observer.super.onStandardizedIceConnectionChange(newState);
        Log.e(TAG, "onStandardizedIceConnectionChange: "+newState);
    }

    @Override
    public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
        PeerConnection.Observer.super.onConnectionChange(newState);
        Log.e(TAG, "onConnectionChange: "+newState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.e(TAG, "onIceConnectionReceivingChange: "+b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.e(TAG, "onIceGatheringChange: "+iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.e(TAG, "onIceCandidate: "+iceCandidate.sdp);
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.e(TAG, "onIceCandidatesRemoved: "+iceCandidates);
    }

    @Override
    public void onSelectedCandidatePairChanged(CandidatePairChangeEvent event) {
        PeerConnection.Observer.super.onSelectedCandidatePairChanged(event);
        Log.e(TAG, "onSelectedCandidatePairChanged: "+event );
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.e(TAG, "onAddStream: "+mediaStream);
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.e(TAG, "onRemoveStream: "+mediaStream );
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.e(TAG, "onDataChannel: "+dataChannel );
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.e(TAG, "onRenegotiationNeeded: " );
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.e(TAG, "onAddTrack: " );
    }

    @Override
    public void onTrack(RtpTransceiver transceiver) {
        PeerConnection.Observer.super.onTrack(transceiver);
        Log.e(TAG, "onTrack: "+transceiver);
    }
}
