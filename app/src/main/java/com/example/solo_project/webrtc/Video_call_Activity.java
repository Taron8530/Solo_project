package com.example.solo_project.webrtc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.solo_project.R;
import com.google.common.collect.ImmutableList;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.*;

import java.util.List;
import java.util.stream.Stream;;

public class Video_call_Activity extends AppCompatActivity {

    private PeerConnectionFactory peerConnectionFactory;
    public TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        test = findViewById(R.id.test_ip);
        // PeerConnectionFactory 초기화
        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions.builder(this)
                .createInitializationOptions());

        // PeerConnectionFactory 생성
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();

        // MediaConstraints 생성
        MediaConstraints constraints = new MediaConstraints();
        constraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        // RTCPeerConnection 생성
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

        // DataChannel 생성
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
                // LocalDescription 설정
                pc.setLocalDescription(new SDPObserver(), sdp);
            }
        }, constraints);
    }

    private class PCObserver implements PeerConnection.Observer {
        @Override
        public void onIceCandidate(IceCandidate candidate) {
            // 공인 IP 주소 추출
            String ip = candidate.sdp.split(" ")[4].split("/")[1];
            Log.d("Video_call_Activity", "Public IP address: " + ip);
//            test.setText(candidate.sdp);

        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] candidates) {}

        @Override
        public void onSignalingChange(PeerConnection.SignalingState newState) {}

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState newState) {}

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {}

        @Override
        public void onIceConnectionReceivingChange(boolean receiving) {}

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onSelectedCandidatePairChanged(CandidatePairChangeEvent event) {}

        @Override
        public void onStandardizedIceConnectionChange(PeerConnection.IceConnectionState newState) {}

        @Override
        public void onAddStream(MediaStream stream) {}

        @Override
        public void onRemoveStream(MediaStream stream) {}

        @Override
        public void onDataChannel(DataChannel channel) {}

        @Override
        public void onRenegotiationNeeded() {}

        @Override
        public void onAddTrack(RtpReceiver receiver, MediaStream[] streams) {}
    }

    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sdp) {}

        @Override
        public void onSetSuccess() {}

        @Override
        public void onCreateFailure(String error) {}

        @Override
        public void onSetFailure(String error) {}
    }
}