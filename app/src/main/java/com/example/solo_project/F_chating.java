package com.example.solo_project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class F_chating extends Fragment {
    String nickname; //닉네임
    private MyAdapter adapter;
    private ArrayList<chat_item> dataList; //채팅 리스트
    private Handler mHandler;//핸들러 변수
    private InetAddress serverAddr; //IP주소
    private Socket socket; //소켓 변수
    private PrintWriter sendWriter;//서버로 문자열 출력
    private final String ip = "35.166.40.164"; //서버 아이피
    private final int port = 8888; // 포트번호
    EditText message; //메세지
    String read; //서버에서 보내오는 문자열
    private RecyclerView recyclerView;
    private Button chat_btn;
    private HorizontalScrollView scroll;
    //시간

    public F_chating(String nickname){
        this.nickname = nickname;
    }
    String TAG = "F_chating";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.e(TAG,nickname);
        View root = inflater.inflate(R.layout.fragment_f_chating, container, false); // 뷰 변수
        chat_btn = root.findViewById(R.id.send_chating);
        mHandler = new Handler();//핸들러 변수
        recyclerView = root.findViewById(R.id.chating_recyclerview); //리사이클러뷰 할당
        message = root.findViewById(R.id.chating_text);

        LinearLayoutManager manager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);//리사이클러뷰 매니저

        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        recyclerView.setAdapter(adapter); //리사이클러뷰에 어뎁터 장착

        Waiting_msg(); //서버에서 보내는 메세지 받을 준비

        scroll = root.findViewById(R.id.scrollView3);

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().trim().equals("")){
                    sendMsg(message.getText().toString());
                    Log.e(TAG,"눌림");
                }
            }
        });

        return root;
    }

    public void sendMsg(String msg){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sendWriter.println(nickname +"/"+ msg);
                    sendWriter.flush();
                    message.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void Waiting_msg(){
        new Thread() {
            public void run() {
                try {
                    Log.e(TAG,"Waiting_msg") ;
                    serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        read = input.readLine();
                        if(read!=null){
                            Log.e("chat",read);
                            mHandler.post(new MsgUpdate(read));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();
    }
    class MsgUpdate implements Runnable{
        String str;
        String[] Msgs;

        public MsgUpdate(String str){
            this.str = str;
            Log.e("chat",str);
            Msgs = str.split("/");
        }

        @Override
        public void run() {
            if(Msgs[0].equals(nickname)){
                dataList.add(new chat_item(Msgs[1],Msgs[0],Msgs[2],2));
            }else{
                dataList.add(new chat_item(Msgs[1],Msgs[0],Msgs[2],1));
            }
            adapter.notifyDataSetChanged();
            scroll.fullScroll(View.FOCUS_DOWN);
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach: ");
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList); //어뎁터 할당x`
    }
}