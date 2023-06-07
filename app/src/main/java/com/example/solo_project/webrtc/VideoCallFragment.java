package com.example.solo_project.webrtc;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.solo_project.R;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase14;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Capturer;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.HardwareVideoEncoderFactory;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaSource;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.VideoCodecType;
import org.webrtc.RtpTransceiver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoder;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class VideoCallFragment extends Fragment
{
    private PeerConnectionFactory peerConnectionFactory;
    private String TAG = "VideoCallFragment";
    private MediaConstraints audioConstraints;
    private MediaConstraints videoConstraints;
    private AudioTrack localAudioTrack;
    private AudioSource audioSource;
    private VideoSource videoSource;
    private VideoTrack videoTrack;
    private MediaStream stream;

    //    PeerConnectionFactory.Options options;
    private PeerConnection peerConnection;
    private List<PeerConnection.IceServer> iceServers;
    private Signaling_Socket socket;
    private boolean status;
    private SurfaceViewRenderer my_view;
    private SurfaceViewRenderer other_view;
    private String receiver;
    private String sender;
    private View root;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private EglBase eglBase;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private boolean signalingStatus;
    public VideoCallFragment(Signaling_Socket socket,boolean status,String sender,String receiver) {
        this.socket = socket;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
        // Required empty public constructor
    }
    public VideoCallFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_video_call, container, false);
        requestPermissions();
        initWebRTC();
        init_view();
        return root;
    }

    private void init_view(){
        other_view = root.findViewById(R.id.otherSurface);
        my_view = root.findViewById(R.id.mySurface);
        my_view.setMirror(true);
        my_view.init(eglBase.getEglBaseContext(),null);
        videoTrack.addSink(my_view);
        Log.d(TAG, "init_view: status check" + status);

        socket.setOnItemClickListener(new Signaling_Socket.readMsgListener() {
            @Override
            public void onServerMsgRead(String msg) throws JSONException {
                if(msg != null){
                    JSONObject jsonObject = new JSONObject(msg);
                    String type = (String) jsonObject.get("type");
                    if(type.equals("offer")){
                        createAnswer((String)jsonObject.get("sdp"));
                    }else if(type.equals("answer")){
                        setAnswer((String)jsonObject.get("sdp"));
                    }else if(type.equals("ice_candidate")){
                        String iceCandidate_sdp = (String) jsonObject.get("sdp");
                        String iceCandidate_sdp_mid = (String) jsonObject.get("sdpMid");
                        int iceCandidate_sdpMLineIndex = (int) jsonObject.get("sdpMLineIndex");
                        IceCandidate iceCandidate = new IceCandidate(iceCandidate_sdp_mid,iceCandidate_sdpMLineIndex,iceCandidate_sdp);
                        Log.d(TAG, "onServerMsgRead: "+iceCandidate.sdp);
                        peerConnection.addIceCandidate(iceCandidate);
                    }else if(type.equals("close_call")){
                        // 종료되었다고 알림
                        getActivity().finish();
                    }

                }
            }
        });
        if (status) {
            createOffer();
        }
    }
    public void initWebRTC() {
        eglBase = EglBase.create();
        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions.builder(getContext())
                .createInitializationOptions());

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory()
        ;
//        CameraEnumerator cameraEnumerator =
//        VideoCapturer videoCapturer = createCameraCapture();
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();
        initIceServers();
        peerConnection = peerConnectionFactory.createPeerConnection(
                new PeerConnection.RTCConfiguration(iceServers),
                new PCObserver(){
                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        super.onIceCandidate(iceCandidate);
                        socket.sendIce_Candidate(sender,receiver,iceCandidate);
                    }

                    @Override
                    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                        super.onSignalingChange(signalingState);
                        if(signalingState.equals("STABLE")){
                            signalingStatus = true;
                        }
                    }

//                    @Override
//                    public void onTrack(RtpTransceiver transceiver) {
//                        Log.d(TAG, "onTrack: 호출됨. " + transceiver.getReceiver().track());
//                        super.onTrack(transceiver);
//                        MediaStreamTrack track = transceiver.getReceiver().track();
//                        if (track instanceof VideoTrack) {
//                            // 원격 비디오 트랙을 처리하는 로직
//                            VideoTrack remoteVideoTrack = (VideoTrack) track;
//                            SurfaceViewRenderer remoteVideoView = root.findViewById(R.id.otherSurface);
//                            remoteVideoTrack.addSink(remoteVideoView);
//                        } else if (track instanceof AudioTrack) {
//                            // 원격 오디오 트랙을 처리하는 로직
//                            AudioTrack remoteAudioTrack = (AudioTrack) track;
//                            // 오디오 트랙을 사용하려면 중복 처리, 재생하는 로직 구현이 필요합니다.
//                        }
//                    }

                    @Override
                    public void onAddStream(MediaStream mediaStream) {
                        super.onAddStream(mediaStream);
                        Log.d(TAG, "onAddStream: 호출");
                        gotRemoteStream(mediaStream);
                    }

                    @Override
                    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                        super.onIceConnectionChange(iceConnectionState);
                        if(iceConnectionState.toString().equals("FAILED")){
                            socket.sendMsg(sender,receiver,"close_call");
                            getActivity().finish();
                        }else if(iceConnectionState.toString().equals("CONNECTED")){
                            Log.d(TAG, "onIceConnectionChange: stream" + stream);
                        }else if(iceConnectionState.toString().equals("COMPLETED")){
                            peerConnection.addStream(stream);
                        }
                    }

                    @Override
                    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                        super.onAddTrack(rtpReceiver, mediaStreams);
                    }
                });
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        VideoCapturer videoCapturer = createCameraCapturer();
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getActivity().getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(1000, 1000, 30);
        videoTrack = peerConnectionFactory.createVideoTrack("103",videoSource);
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
        localAudioTrack.setEnabled(true);
        videoTrack.setEnabled(true);
        stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(videoTrack);
        stream.addTrack(localAudioTrack);
        peerConnection.addStream(stream);

    }
    private void createOffer() {
        MediaConstraints constraints = new MediaConstraints();
// Add video codecs
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("videoCodec", "H.264H"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("audioCodec", "opus"));

// Set other constraints
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
//        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", "1000"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", "1000"));
        constraints.optional.add(new MediaConstraints.KeyValuePair("maxFrameRate", "30"));

        peerConnection.createOffer(new SDPObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                // LocalDescription 설정
                Log.e(TAG, "offer 생성됨: " + sdp.description);
                peerConnection.setLocalDescription(new SDPObserver(), sdp);
                socket.sendOffer(sender,receiver,sdp.description);
            }
        }, constraints);

    }
    public void setAnswer(String remoteSdp){
        MediaConstraints constraints = new MediaConstraints();
        String sdpType = "answer"; // "offer" 또는 "answer"
        Log.e(TAG, "answer 이 날라와서 셋팅됨: " + remoteSdp);
        SessionDescription receivedSessionDescription = new SessionDescription(SessionDescription.Type.fromCanonicalForm(sdpType), remoteSdp);
        peerConnection.setRemoteDescription(new SDPObserver(),receivedSessionDescription);

    }
    private void createAnswer(String remoteSdp) {
        MediaConstraints constraints = new MediaConstraints();
        String sdpType = "offer"; // "offer" 또는 "answer"
        SessionDescription receivedSessionDescription = new SessionDescription(SessionDescription.Type.fromCanonicalForm(sdpType), remoteSdp);
        peerConnection.setRemoteDescription(new SDPObserver(), receivedSessionDescription);
        peerConnection.createAnswer(new SDPObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                // LocalDescription 설정
                Log.e(TAG, "answer 생성됨: " + sdp.description);
                peerConnection.setLocalDescription(new SDPObserver(), sdp);
                socket.sendAnswer(sender,receiver,sdp.description);
            }
        }, constraints);
    }
    private void initIceServers() {
        iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
    }
    private VideoCapturer createCameraCapturer() {
        Camera1Enumerator enumerator = new Camera1Enumerator(false);
        String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "권한 요청 거부됨. 앱이 제대로 작동하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            init_view();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stop_Call();
    }

    private void gotRemoteStream (MediaStream stream) {
        List<VideoTrack> videoTracks = stream.videoTracks;
        List<AudioTrack> audioTracks = stream.audioTracks;
        Log.d(TAG, "gotRemoteStream Video Track: "+videoTracks.toString());
        Log.d(TAG, "gotRemoteStream audio Track: "+audioTracks.toString());
        getActivity().runOnUiThread(() -> {
            try {
                Log.d(TAG, "run: gotremotestream 여기 들어옴?");
                if(!videoTracks.isEmpty() && !audioTracks.isEmpty()){
                    VideoTrack videoTrack = videoTracks.get(0);
                    AudioTrack audioTrack = audioTracks.get(0);
                    Log.d(TAG, "run: gotremotestream 여기 들어옴1"+videoTrack);
                    other_view.setMirror(true);
                    other_view = root.findViewById(R.id.otherSurface); // Replace with your actual ID
                    other_view.init(eglBase.getEglBaseContext(), null);
                    other_view.setEnabled(true);
                    other_view.setZOrderMediaOverlay(false);
                    other_view.setEnableHardwareScaler(true);
                    videoTrack.addSink(other_view);
                    audioTrack.setEnabled(true);
                }

                // If you want to process the remote audio
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
    private void stop_Call(){
        peerConnection.close();
        peerConnection =null;

    }
}