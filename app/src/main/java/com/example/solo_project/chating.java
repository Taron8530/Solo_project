package com.example.solo_project;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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

    private ActivityResultLauncher<Intent> mStartForResult;
    private ActivityResultLauncher<Intent> location_start_for_result;
    private LinearLayout container;

    @Override
    protected void onPause() {
        super.onPause();
//        socket_Disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        dataList = new ArrayList<>();
        location_start_for_result = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            long mNow = System.currentTimeMillis();
                            Date mDate = new Date(mNow);
                            SimpleDateFormat mFormat = new SimpleDateFormat("hh시mm분");
                            Intent i = result.getData();
                            double lati = i.getDoubleExtra("lati",0);
                            double longs = i.getDoubleExtra("long",0);
                            Log.d(TAG,i.getStringExtra("location") + lati + longs);
                            dataList.add(new chat_item(i.getStringExtra("location") +">"+ lati +">"+ longs,nickname,"전송중...",5));
                            sendMsg(i.getStringExtra("location") +">"+ lati +">"+ longs,sender,room_num,"Location_share");
                            Log.e("위치공유","위치공유"+i.getStringExtra("location") +"/"+ lati +"/"+ longs);
//                            myDb.insert_data(room_num,"",Msgs[2].replaceAll("위치____공유",""),Msgs[3],4);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Log.d("Chating_activity", intent.getStringExtra("time"));
                        Log.d("Chating_activity", intent.getStringExtra("date"));
                        Log.d("Chating_activity",nickname);
                        Log.d("Chating_activity",room_num);
                        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                        Call<String> call = apiInterface.chat_promise_insert(room_num,intent.getStringExtra("date"),intent.getStringExtra("time"),nickname);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response != null){
                                    Log.d("chating",response.body());
                                    //여기서 소켓으로 메세지 던질거임
                                    sendMsg("약____속",sender,room_num,"Promise");
                                    Promise_select(room_num);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(chating.this,"오류가 발생했습니다",Toast.LENGTH_SHORT).show();
                                Log.d("chating",t.toString());
                            }
                        });
                    }
                }
        );
    }

    //시간
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        chat_btn = findViewById(R.id.send_chating);
        container = findViewById(R.id.chating_container);
        Intent i = getIntent();
        nickname = i.getStringExtra("my_nickname");
        Log.e(TAG, "onCreate: 닉네임 들어옴" );
        sender = i.getStringExtra("sender");
        room_num = i.getStringExtra("room_num");
        container.setVisibility(View.GONE);
        Promise_select(room_num);
        dataList = new ArrayList<>();
        myDb = new DBHelper(chating.this);
        chat_data_db_Helper db = new chat_data_db_Helper(chating.this);
        try {
            dataList = db.SelectAllKids(Integer.parseInt(room_num));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        myDb.msg_count_reset(Integer.parseInt(room_num));

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
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if(dataList.size() > 0) {
                    recyclerView.smoothScrollToPosition(dataList.size() - 1);
                }
            }
        }); // auto Scroll 코드
        LinearLayoutManager manager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);//리사이클러뷰 매니저
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어뎁터 장착
        recyclerView.scrollToPosition(dataList.size()-1);
        Waiting_msg(); //서버에서 보내는 메세지 받을 준비
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onImageClick(View v, int position) {
                Intent i = new Intent(chating.this,Chatting_Image_View.class);
                i.putExtra("image_url",dataList.get(position).getContent());
                startActivity(i);
            }

            @Override
            public void onMapIVew(View v, int position) {
                Intent i = new Intent(chating.this,Map_view_1.class);
                i.putExtra("location",dataList.get(position).getContent());
                startActivity(i);
            }
        });
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().trim().equals("")){
                    dataList.add(new chat_item(message.getText().toString(),nickname,"전송중...",2));
                    adapter.notifyDataSetChanged();
//                    myDb.last_msg_update(Integer.parseInt(room_num),message.getText().toString());
                    sendMsg(message.getText().toString(),sender,room_num,"General");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.chating_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.promise:
                //약속잡기 구현 호출
                promise();
                break;
            case R.id.location_share:
                Location_Share();
                break;
            case R.id.chating_exit:
                //채팅방 데이터 모두 지우기
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void Location_Share(){
        Intent i = new Intent(chating.this,Map_View.class);
        location_start_for_result.launch(i);
    }
    public void promise(){
        Intent intent = new Intent(this,chat_promise.class);
        mStartForResult.launch(intent);

    }
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
    public void sendMsg(String msg,String sender,String room_num,String type){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("room_num",room_num);
                    jsonObject.put("message",msg);
                    jsonObject.put("sender",nickname);
                    jsonObject.put("receiver",sender);
                    jsonObject.put("type",type);
                    Log.e("채팅 액티비티", String.valueOf(jsonObject));
                    Log.e("채팅 엑티비티",sendWriter.toString());

                    sendWriter.println(jsonObject);
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
                                        try {
                                            check_time(str[1]);
                                            Same_time();

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }else if(str[0].equals("약____속")){
                                //함수로 하기
                                Promise_select(room_num);
                            }else{
                                mHandler.post(new MsgUpdate(read));
                                Same_time();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    socket_Disconnect();
                    Log.e(TAG+"Wait_msg",e.toString());
                } }}.start();
    }
    private void Promise_select(String room_num){
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<chat_promise_model> call = apiInterface.chat_promise_select(room_num);
        call.enqueue(new Callback<chat_promise_model>() {
            @Override
            public void onResponse(Call<chat_promise_model> call, Response<chat_promise_model> response) {
                if(response.body() != null){
                    if(response.body().getResponse().equals("성공")){
                        Log.d("chating",response.body().getPromise_date());
                        Log.d("chating",response.body().getPromise_time());
                        Log.d("chating",response.body().getNickname());
                        Log.d("chating",response.body().getTime());
                        container.setVisibility(View.VISIBLE);
                        TextView promise_date = findViewById(R.id.promise_date);
                        promise_date.setText(response.body().getPromise_date());
                        container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(chating.this);
                                builder.setTitle("약속 시간").setMessage("\n"+response.body().getPromise_time());
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getApplicationContext(), "Yeah!!", Toast.LENGTH_LONG).show();

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
//                        Log.d("chating",String.valueOf(response.body().getRoom_num()));
                    }else{
                        Log.e("ChattingActivity",response.body().getResponse());
                    }
                }
            }

            @Override
            public void onFailure(Call<chat_promise_model> call, Throwable t) {
                Log.e("chating",t.toString());
            }
        });
    }
    private void check_time(String time) throws ParseException {
        for(int i =0;i<dataList.size();i++){
            if (dataList.get(i).getTime().equals("전송중...")&&dataList.get(i).getViewType() >= 2) {
                dataList.get(i).setTime(String_extract_time(time));
                adapter.notifyDataSetChanged();
                chat_data_db_Helper db = new chat_data_db_Helper(chating.this);
                db.insert_data(room_num,nickname,dataList.get(i).getContent(),time,dataList.get(i).getViewType());
                if(dataList.get(i).getContent().contains("image") || dataList.get(i).getContent().contains(".jpeg")){
                    myDb.last_msg_update(Integer.parseInt(room_num),"사진",time);
                }else if(dataList.get(i).getViewType() == 5){
                    myDb.last_msg_update(Integer.parseInt(room_num),"위치를 공유했어요!",time);
                }else{
                    myDb.last_msg_update(Integer.parseInt(room_num),dataList.get(i).getContent(),time);
                }

            }
        }
    }
    public void Same_time(){
        if(dataList.size() >= 3) {
            if (dataList.get(dataList.size() - 1).getTime().equals(dataList.get(dataList.size() - 2).getTime()) &&dataList.get(dataList.size() - 1).getViewType()== dataList.get(dataList.size() - 2).getViewType()) {
                dataList.get(dataList.size() - 2).setTime("");
            }
        }
    }
    public String String_extract_time(String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        String new_time = new SimpleDateFormat("H시 mm분").format(date);
        return new_time;
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
//        socket_Disconnect();
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
                                    sendMsg(response.body(),sender,room_num,"General");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(chating.this, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                            Log.e("테스트 로그",t.toString());
                        }
                    });
                    dataList.add(new chat_item(Uri.toString(),nickname,"전송중...",3));
//                    myDb.last_msg_update(Integer.parseInt(room_num),"사진");
                    adapter.notifyDataSetChanged();
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
    
    public String saveBitmapToJpeg(Bitmap bitmap,String imgName) throws IOException {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);    //회전시킬 각도
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, //bmp를 matrix로 회전하여 newBmp에
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            newBmp.compress(Bitmap.CompressFormat.JPEG, 30, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
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
        JSONObject jsonObject;

        public MsgUpdate(String str){
            this.str = str;
            Log.e("chat",str);
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public String String_extract_time(String time) throws ParseException {
            if(time != null){
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
                String new_time = new SimpleDateFormat("HH:mm").format(date);
                return new_time;
            }
            else{
                return null;
            }
        }
        @Override
        public void run() {
            chat_data_db_Helper myDb = new chat_data_db_Helper(chating.this);
            DBHelper myDbs = new DBHelper(chating.this);
            String message = null;
            String receiver = null;
            String type = null;
            String sender= null;
            String time = null;
            try {
                 message = jsonObject.getString("message");
//                 receiver = jsonObject.getString("receiver");
                 type = jsonObject.getString("type");
                 sender = jsonObject.getString("sender");
                 time = jsonObject.getString("time");
                 Log.e(TAG,"type: "+type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
            if(message.contains(".jpeg")){
                dataList.add(new chat_item("http://35.166.40.164/file/"+message,sender,String_extract_time(time),0));
                myDb.insert_data(room_num,sender,"http://35.166.40.164/file/"+message,time,0);
                myDbs.last_msg_update(Integer.parseInt(room_num),"사진",time);
            }else if(type.equals("Location_share")){
                dataList.add(new chat_item(message,sender,String_extract_time(time),4)); //end point
                myDb.insert_data(room_num,sender,message,time,4);
                myDbs.last_msg_update(Integer.parseInt(room_num),"위치를 공유했어요!",time);
            }else{
                    dataList.add(new chat_item(message,sender,String_extract_time(time),1));
                    myDb.insert_data(room_num,sender,message,time,1);
                    myDbs.last_msg_update(Integer.parseInt(room_num),message,time);
            }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(dataList.size() - 1);
        }
    }

}