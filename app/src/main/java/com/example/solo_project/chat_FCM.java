package com.example.solo_project;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class chat_FCM extends FirebaseMessagingService {
    private SharedPreferences pref;
    private String nickname;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
//        //token을 서버로` 전송
//        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
//        Call<Signup_model> call = apiInterface.profile_sel(verify);
        Log.e("FCM_token", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("chat_FCM", "onMessageReceived: 호출됨 ");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); //fcm이 날라왔는데 look 상태일때
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(3000);
        if (remoteMessage.getData() != null) {
            // 여기가 webrtc fcm 부분
//            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
            Log.e("노티피케이션", String.valueOf(remoteMessage.getData()));
        }

        if (remoteMessage.getNotification() != null) {
            pref = getSharedPreferences("user_verify", Context.MODE_PRIVATE);
            nickname = pref.getString("user_nickname", "");
            String message = null;
            String time = null;
            String room_num = null;
            String receiver = null;
            Log.e("노티피케이션", remoteMessage.getNotification().getTitle() + remoteMessage.getNotification().getBody());

            Log.e("body", remoteMessage.getNotification().getBody());
//            String[] bodys = remoteMessage.getNotification().getBody().split("/"); //노티피케이션 body를 구분자 / 기준으로 자른것
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getNotification().getBody());
                receiver = jsonObject.getString("receiver");
                message = jsonObject.getString("message");
                time = jsonObject.getString("time");
                room_num = jsonObject.getString("room_num");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("title", remoteMessage.getNotification().getTitle());
            chat_data_db_Helper myDb = new chat_data_db_Helper(chat_FCM.this); //채팅 데이터 객체화
            DBHelper dbHelper = new DBHelper(chat_FCM.this);//채팅 룸 객체화

            dbHelper.msg_count_update(Integer.parseInt(room_num));
            if (dbHelper.check_room(Integer.parseInt(room_num))) { //채팅방이 없을시 채팅방 생성 하는 로직
                dbHelper.insert_data(Integer.parseInt(room_num), remoteMessage.getNotification().getTitle(), receiver, remoteMessage.getNotification().getTitle(), 1);
            }
            try {
                if (message.contains(".jpeg")) { //보내온 메세지가 사진일때
                    showNotification(remoteMessage.getNotification().getTitle(), "사진을 보냄", room_num, String_extract_time(time));

                    myDb.insert_data(room_num, remoteMessage.getNotification().getTitle(), "http://35.166.40.164/file/" + message, time, 0);
                    dbHelper.last_msg_update(Integer.parseInt(room_num), "사진", time);
                } else { //보내온 메세지가 문자일때
                    showNotification(remoteMessage.getNotification().getTitle(), message, room_num, String_extract_time(time));
                    myDb.insert_data(room_num, remoteMessage.getNotification().getTitle(), message, time, 1);
                    dbHelper.last_msg_update(Integer.parseInt(room_num), message, time);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new msg_box("보내짐"));
        //수신한 메시지를 처리
    }

    public String String_extract_time(String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        String new_time = new SimpleDateFormat("HH:mm").format(date);
        return new_time;
    }

    private RemoteViews getCustomDesign(String title, String message, String time) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setTextViewText(R.id.noti_time, time);
        remoteViews.setImageViewResource(R.id.noti_icon, R.drawable.app_icon);
        return remoteViews;
    }

    public void showNotification(String title, String message, String room_num, String time) {
        Intent intent = new Intent(this, chating.class);
        intent.putExtra("my_nickname", nickname);
        intent.putExtra("sender", title);
        intent.putExtra("room_num", room_num);

        String channel_id = "";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.app_icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getCustomDesign(title, message, time));
        } else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.app_icon).setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}
