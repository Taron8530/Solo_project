package com.example.solo_project.webrtc;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.example.solo_project.MyAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Signaling_Socket {

    private Handler mHandler;//핸들러 변수
    private InetAddress serverAddr; //IP주소
    private Socket socket; //소켓 변수
    private PrintWriter sendWriter;//서버로 문자열 출력
    private final String ip = "35.166.40.164"; //서버 아이피
    private final int port = 6060; // 포트번호
    private BufferedReader input;
    private String TAG = "SignalingSocket";
    private readMsgListener webrtcEventListener;
    Signaling_Socket(String nickname) throws IOException {
        init_socket(nickname);
    }
    public void init_socket(String nickname) throws IOException {
        new Thread() {
            public void run() {
                try {
                    Log.d(TAG, "init_socket: 호출 ");
                    serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    sendNickname(nickname);
                    readMsg();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

    }
    public void sendOffer(String sender,String receiver,String sdp){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sdp",sdp);
                    jsonObject.put("sender",sender);
                    jsonObject.put("receiver",receiver);
                    jsonObject.put("type","offer");
                    Log.e("sendOffer", String.valueOf(jsonObject));
                    Log.e("sendOffer",sendWriter.toString());

                    sendWriter.println(jsonObject);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void sendNickname(String nickname){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("sendNickname",sendWriter.toString());

                    sendWriter.println(nickname);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void sendAnswer(String sender,String receiver,String sdp){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sdp",sdp);
                    jsonObject.put("sender",sender);
                    jsonObject.put("receiver",receiver);
                    jsonObject.put("type","answer");
                    Log.e("sendAnswer", String.valueOf(jsonObject));
                    Log.e("sendAnswer",sendWriter.toString());

                    sendWriter.println(jsonObject);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void sendIce_Candidate(String sender, String receiver, IceCandidate iceCandidates){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sdp",iceCandidates.sdp);
                    jsonObject.put("sdpMid",iceCandidates.sdpMid);
                    jsonObject.put("sdpMLineIndex",iceCandidates.sdpMLineIndex);
                    jsonObject.put("sender",sender);
                    jsonObject.put("receiver",receiver);
                    jsonObject.put("type","ice_candidate");
                    Log.e("sendAnswer", String.valueOf(jsonObject));
                    Log.e("sendAnswer",sendWriter.toString());

                    sendWriter.println(jsonObject);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void sendMsg(String sender,String receiver,String type){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sender",sender);
                    jsonObject.put("receiver",receiver);
                    jsonObject.put("type",type);
                    Log.e("sendmsg", String.valueOf(jsonObject));
                    Log.e("sendmsg",sendWriter.toString());

                    sendWriter.println(jsonObject);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void readMsg() throws IOException {
        new Thread() {
            public void run() {
                try {
                    while(true) {
                        String read; //서버에서 보내오는 문자열
                        read = input.readLine();
                        if(read != null) {
                            if(webrtcEventListener != null){
                                webrtcEventListener.onServerMsgRead(read);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //                        socket_disconnect();
                    Log.d(TAG, "run: "+e);
                    Log.e(TAG+"Wait_msg",e.toString());
                } }}.start();
    }
    public void socket_disconnect(String nickname) throws IOException, JSONException {
        try {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("nickname",nickname);
                        jsonObject.put("type","disconnect");
                        sendWriter.println(jsonObject);
                        sendWriter.flush();
                        socket.close();
                        sendWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setOnItemClickListener(Signaling_Socket.readMsgListener webrtcEventListener) {
        this.webrtcEventListener = webrtcEventListener ;
    }
    public interface readMsgListener {
        void onServerMsgRead(String msg) throws JSONException;
    }
}
