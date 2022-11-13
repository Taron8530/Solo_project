package com.example.solo_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;   //json
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
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
    private TextView back;
    private String TAG = "Chat_Activity";
    private TextView sender_nickname;
    private ImageView sender_profile;
    private Button chat_plus;
    private LinearLayout top_bar;
    private String sender;
    //시간
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        chat_btn = findViewById(R.id.send_chating);
        Intent i = getIntent();
        nickname = i.getStringExtra("my_nickname");
        sender = i.getStringExtra("sender");
        back = findViewById(R.id.chat_exit);
        sender_nickname = findViewById(R.id.sender_nickname);
        sender_nickname.setText(sender);
        sender_profile = findViewById(R.id.sender_profile);
        top_bar = findViewById(R.id.chat_top_bar);
        chat_plus = findViewById(R.id.chat_plus);
        Glide.with(chating.this)
                .load("http://35.166.40.164/profile/"+sender+".png")
                .circleCrop()
                .error(R.drawable.app_icon)
                .into(sender_profile);

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
                    dataList.add(new chat_item(message.getText().toString(),nickname,"",null,2));
                    recyclerView.scrollToPosition(dataList.size() - 1);
                    adapter.notifyDataSetChanged();
                    sendMsg(message.getText().toString(),sender);
                    Log.e(TAG,"눌림");
                }else{
                    Toast.makeText(chating.this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();             }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chat_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getG();
            }
        });
        top_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(chating.this,Profile_view.class);
                I.putExtra("nickname",sender);
                startActivity(I);
            }
        });
    }
//    private void setStringArrayPref(Context context, String key, ArrayList<chat_item> values) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        JSONArray data = new JSONArray();
//        JSONObject temp_data = new JSONObject();
//        for (int i = 0; i < values.size(); i++) {
//            try {
//                temp_data.put("content",values.get(i).getContent());
//                temp_data.put("name",values.get(i).getName());
//                temp_data.put("time",values.get(i).getTime());
//                temp_data.put("view_type",values.get(i).getViewType());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            data.put(temp_data);
//        }
//        if (!values.isEmpty()) {
//            editor.putString(key, data.toString());
//        } else {
//            editor.putString(key, null);
//        }
//        editor.apply();
//    }
//
//    private ArrayList<chat_item> getStringArrayPref(Context context, String key) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        String json = prefs.getString(key, null);
//        ArrayList<chat_item> chat_datas = new ArrayList<>();
//        if (json != null) {
//            try {
//                JSONArray chat_data_Json = new JSONArray(json);
//                for (int i = 0; i < chat_data_Json.length(); i++) {
//                    String url = chat_data_Json.optString(i);
//                    chat_datas.add(new chat_item(chat_data_Json.ge));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return chat_datas;
//    }
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
//                    wrapObject.put("messege",jsonArray);
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            socket.close();
//            sendWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG+"onstop",e.toString());
//        }
//    }

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
                            if(read.equals("보내짐")){
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        check_time(read);
                                    }
                                });
                            }else{
                                mHandler.post(new MsgUpdate(read));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG+"Wait_msg",e.toString());
                } }}.start();
    }
    private void check_time(String time){
        for(int i =0;i<dataList.size();i++){
            if (dataList.get(i).getTime().equals("")&&dataList.get(i).getViewType() == 2) {
                dataList.get(i).setTime(time);
                adapter.notifyDataSetChanged();

            }
        }
    }
    public void getG() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE) {
//            if (resultCode == RESULT_OK) {
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                Log.e("이미지 URI", String.valueOf(data.getData()));
//                ((ImageButton) findViewById(R.id.profilebtn)).setImageBitmap(imageBitmap);
//            }
//        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri Uri = data.getData();
                try {
                    String path = getRealPathFromURI(Uri);
                    Log.e("ㅇㅇ", path);
                    dataList.add(new chat_item("",nickname,"",Uri,3));
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size());
                }catch (Exception e){
                    Log.e("dd",e.toString());

                }
            }

        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
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
            dataList.add(new chat_item(Msgs[2],Msgs[1],Msgs[3],null,1));
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(dataList.size() - 1);
        }
    }

}