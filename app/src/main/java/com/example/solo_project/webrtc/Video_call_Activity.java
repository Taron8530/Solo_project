package com.example.solo_project.webrtc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.solo_project.ApiInterface;
import com.example.solo_project.Apiclient;
import com.example.solo_project.R;
import com.google.common.collect.ImmutableList;

import org.webrtc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video_call_Activity extends AppCompatActivity{

    private PeerConnectionFactory peerConnectionFactory;
    public TextView test;
    private String TAG = "Video_Call_Activity";
    private MediaConstraints audioConstraints;
    private AudioTrack localAudioTrack;
    private AudioSource audioSource;
    private MediaStream stream;
    PeerConnectionFactory.Options options;
    private PeerConnection local_peer;
    private List<PeerConnection.IceServer> iceServers;
    private PeerConnection remote_peer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        initWebRTC();
    }
    public void init_socket(){

    }
    public void initWebRTC() {
        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions.builder(this)
                .createInitializationOptions());

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();

        audioConstraints = new MediaConstraints();
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
        localAudioTrack.setEnabled(true);
        stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        initIceServers();

        local_peer = peerConnectionFactory.createPeerConnection(
                new PeerConnection.RTCConfiguration(iceServers),
                new PCObserver());
        createOffer();
    }
    private void createOffer() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
        local_peer.createOffer(new SDPObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                // LocalDescription 설정
                Log.e(TAG, "offer 생성됨: " + sdp.description);
                local_peer.setLocalDescription(new SDPObserver(), sdp);
            }
        }, constraints);
    }
    private void initIceServers() {
        iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
    }
    private void join_websocket(String nickname){

    }
    private void send_offer(String nickname, String sdp){

    }
    public void call(){

    }
    private VideoCapturer createCameraCapture(CameraEnumerator enumerator){
        final String[] deviceNames = enumerator.getDeviceNames();

        for(String deviceName : deviceNames){
            if(enumerator.isBackFacing(deviceName)){
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName,null);
                if(videoCapturer != null){
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames){
            if(!enumerator.isBackFacing(deviceName)){
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName,null);
                if(videoCapturer != null){
                    return videoCapturer;
                }
            }
        }
        return null;
    }
}