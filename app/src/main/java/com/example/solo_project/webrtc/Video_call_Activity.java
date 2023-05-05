package com.example.solo_project.webrtc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.solo_project.ApiInterface;
import com.example.solo_project.Apiclient;
import com.example.solo_project.R;
import com.google.common.collect.ImmutableList;

import org.webrtc.*;

;import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video_call_Activity extends AppCompatActivity {

    private PeerConnectionFactory peerConnectionFactory;
    public TextView test;
    private String TAG = "Video_Call_Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        test = findViewById(R.id.test_ip);

        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions.builder(this)
                .createInitializationOptions());


        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();


        MediaConstraints constraints = new MediaConstraints();
        constraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));


        PeerConnection.RTCConfiguration config = new PeerConnection.RTCConfiguration(
                ImmutableList.of(new PeerConnection.IceServer("stun:stun.l.google.com:19302")));
        config.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        config.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        config.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        config.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        config.keyType = PeerConnection.KeyType.ECDSA;
        PeerConnection pc = peerConnectionFactory.createPeerConnection(
                config,
                new PCObserver());

        DataChannel.Init init = new DataChannel.Init();
        init.ordered = true;
        init.negotiated = false;
        init.maxRetransmits = -1;
        init.maxRetransmitTimeMs = -1;
        init.id = 1;
        DataChannel dc = pc.createDataChannel("test", init);


        // Offer 생성
        pc.createOffer(new SDPObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                String nickname = "테스트우";
                // LocalDescription 설정
                Log.e(TAG,"offer 생성됨: " + sdp.description);
                pc.setLocalDescription(new SDPObserver(), sdp);
                test.setText(String.valueOf(sdp));
                try {
                    ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    Call<String> call = apiInterface.send_offer(sdp.description, nickname);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t);

                        }
                    });
                }catch (Exception e){
                    Log.e(TAG, "onCreateSuccess: "+e );
                }
                
            }
        }, constraints);
    }
}