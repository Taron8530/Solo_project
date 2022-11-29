package com.example.solo_project;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private String room_num;
    private DBHelper myDb;
    //시간
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        chat_btn = findViewById(R.id.send_chating);
        Intent i = getIntent();
        nickname = i.getStringExtra("my_nickname");
        sender = i.getStringExtra("sender");
        room_num = i.getStringExtra("room_num");
        dataList = new ArrayList<>();
        myDb = new DBHelper(chating.this);
        chat_data_db_Helper db = new chat_data_db_Helper(chating.this);
        dataList = db.SelectAllKids(Integer.parseInt(room_num));

        Log.e("chat_room_num",String.valueOf(room_num));
        back = findViewById(R.id.chat_exit);
        sender_nickname = findViewById(R.id.sender_nickname);
        sender_nickname.setText(sender);
        sender_profile = findViewById(R.id.sender_profile);
        top_bar = findViewById(R.id.chat_top_bar);
        chat_plus = findViewById(R.id.chat_plus);
        chat_btn.setClickable(false);
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
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어뎁터 장착
        recyclerView.scrollToPosition(dataList.size());
        Waiting_msg(); //서버에서 보내는 메세지 받을 준비
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().trim().equals("")){
                    dataList.add(new chat_item(message.getText().toString(),nickname,"",2));
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size());
                    myDb.last_msg_update(Integer.parseInt(room_num),message.getText().toString());
                    sendMsg(message.getText().toString(),sender,room_num);
//                    chat_data_db_Helper db = new chat_data_db_Helper(chating.this);
//                    db.insert_data(room_num,nickname,message.getText().toString(),"",2);
                    message.setText("");
                    Log.e(TAG,"눌림");
                }else{
                    Toast.makeText(chating.this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();             }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                socket_Disconnect();
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
        //버튼 활성화 비 활성화 로직 구현하기!!
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
    public void send_nickname(String nickname,String room_num){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sendWriter.println(room_num+"/"+nickname);
                    sendWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void sendMsg(String msg,String sender,String room_num){
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

                    sendWriter.println(room_num+"/"+sender+"/"+nickname +"/"+msg);
                    sendWriter.flush();
                    message.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket_Disconnect();
    }

    public void socket_Disconnect() {
        try {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sendWriter.println(nickname + "/" + "접속해제");
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

    public void Waiting_msg(){
        new Thread() {
            public void run() {
                try {
                    Log.e(TAG,"Waiting_msg") ;
                    serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    send_nickname(nickname,room_num);
                    while(true){
                        read = input.readLine();
                        if(read!=null){
                            String[] str = read.split("/");
                            Log.e("chat",read);
                            if(str[0].equals("보내짐")){
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        check_time(str[1]);
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
            if (dataList.get(i).getTime().equals("")&&dataList.get(i).getViewType() >= 2) {
                dataList.get(i).setTime(time);
                recyclerView.scrollToPosition(dataList.size());
                adapter.notifyDataSetChanged();
                chat_data_db_Helper db = new chat_data_db_Helper(chating.this);
                db.insert_data(room_num,nickname,dataList.get(i).getContent(),dataList.get(i).getTime(),dataList.get(i).getViewType());

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
    protected void onStop() {
        super.onStop();
        socket_Disconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Waiting_msg();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                ContentResolver resolver = getContentResolver();
                InputStream instream = null;
                Bitmap imgBitmap = null;
                try {
                    instream = resolver.openInputStream(Uri);
                    imgBitmap = BitmapFactory.decodeStream(instream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.e("테스트 로그","진입");
                try {
                    instream.close();
                    String filenum = get_file_number();
                    String path = saveBitmapToJpeg(imgBitmap,filenum);
                    Log.e("ㅇㅇ", path);
                    ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    File file = new File(path);
                    Log.e("테스트 로그","파일 생성");
                    Log.e("realPath",path);
//                    if (!file.exists()) {       // 원하는 경로에 폴더가 있는지 확인
//                        file.mkdirs();    // 하위폴더를 포함한 폴더를 전부 생성
//                    }
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    Log.e("테스트 로그",filenum);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploaded_file",filenum, requestBody);
                    Call<String> call = apiInterface.chat_file_upload(fileToUpload,filenum);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.body() != null){
                                if(response.equals("실패")){
                                    Toast.makeText(chating.this, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.e("테스트 로그 확인!","http://35.166.40.164/file/"+response.body());
                                    sendMsg(response.body(),sender,room_num);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(chating.this, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                            Log.e("테스트 로그",t.toString());
                        }
                    });
                    dataList.add(new chat_item(Uri.toString(),nickname,"",3));
                    myDb.last_msg_update(Integer.parseInt(room_num),"사진");
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size());
                }catch (Exception e){
                    Log.e("dd",e.toString());

                }
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String get_file_number(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit,rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
    public String saveBitmapToJpeg(Bitmap bitmap,String imgName) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Log.e("파일 저장",tempFile.getPath());
            return getCacheDir()+"/"+imgName;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            return null;
        }
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
            chat_data_db_Helper myDb = new chat_data_db_Helper(chating.this);
            DBHelper myDbs = new DBHelper(chating.this);
            if(Msgs[2].contains(".jpeg")){
                dataList.add(new chat_item("http://35.166.40.164/file/"+Msgs[2],Msgs[1],Msgs[3],0));
                myDb.insert_data(room_num,Msgs[1],"http://35.166.40.164/file/"+Msgs[2],Msgs[3],0);
                myDbs.last_msg_update(Integer.parseInt(room_num),"사진");
            }else{
                dataList.add(new chat_item(Msgs[2],Msgs[1],Msgs[3],1));
                myDb.insert_data(room_num,Msgs[1],Msgs[2],Msgs[3],1);
                myDbs.last_msg_update(Integer.parseInt(room_num),Msgs[2]);
            }
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(dataList.size() - 1);
        }
    }

}