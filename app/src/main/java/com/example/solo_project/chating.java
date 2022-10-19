package com.example.solo_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;

import org.json.JSONArray;
import org.json.JSONObject;   //json

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class chating extends AppCompatActivity {
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
    private String TAG = "Chat_Activity";
    //시간
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String sender;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        chat_btn = findViewById(R.id.send_chating);
        Intent i = getIntent();
        nickname = i.getStringExtra("my_nickname");
        sender = i.getStringExtra("sender");
        Log.e("닉네임 화깅ㄴ",nickname);
        mHandler = new Handler();//핸들러 변수
        recyclerView = findViewById(R.id.chating_recyclerview); //리사이클러뷰 할당
        message = findViewById(R.id.chating_text);

        LinearLayoutManager manager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);//리사이클러뷰 매니저
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어뎁터 장착
        Waiting_msg(); //서버에서 보내는 메세지 받을 준비
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().trim().equals("")){
                    sendMsg(message.getText().toString(),sender);
                    Log.e(TAG,"눌림");
                }
            }
        });
    }
    public void send_nickname(String nickname){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sendWriter.println(nickname);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void sendMsg(String msg,String sender){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    JSONObject jsonObject = new JSONObject();
//                    JSONArray jsonArray = new JSONArray();
//                    JSONObject wrapObject = new JSONObject();
//                    jsonObject.put("sender",sender);
//                    jsonObject.put("messege",msg);
//                    jsonObject.put("nickname",nickname);
//                    jsonArray.put(jsonObject);
//                    wrapObject.put("list",jsonArray);
//                    Log.e("JSON",wrapObject.toString());

                    sendWriter.println(sender+"/"+nickname +"/"+msg);
                    sendWriter.flush();
                    message.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            sendWriter.println("close"+nickname);
            sendWriter.flush();
            socket.close();
            sendWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    send_nickname(nickname);
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
            if(Msgs[1].equals(nickname)){
                Log.e("봐바라",Msgs[0]);
                dataList.add(new chat_item(Msgs[2],Msgs[1],Msgs[3],2));
            }else{
                dataList.add(new chat_item(Msgs[2],Msgs[1],Msgs[3],1));
            }
            adapter.notifyDataSetChanged();
        }
    }

}