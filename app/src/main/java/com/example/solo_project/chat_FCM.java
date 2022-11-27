package com.example.solo_project;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import retrofit2.Call;

public class chat_FCM extends FirebaseMessagingService{
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
//        //token을 서버로 전송
//        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
//        Call<Signup_model> call = apiInterface.profile_sel(verify);
        Log.e("FCM_token",token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("노티피케이션",remoteMessage.getNotification().getTitle()+remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0)
        {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
            Log.e("노티피케이션",remoteMessage.getData().get("title")+remoteMessage.getData().get("message"));
        }

        if (remoteMessage.getNotification() != null)
        {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
            @SuppressLint("InvalidWakeLockTag")
            PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
            wakeLock.acquire(3000);
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
        //수신한 메시지를 처리
    }
    private RemoteViews getCustomDesign(String title, String message)
    {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.noti_icon, R.drawable.app_icon);
        return remoteViews;
    }
    public void showNotification(String title, String message)
    {
        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = "";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.app_icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            builder = builder.setContent(getCustomDesign(title, message));
        }
        else
        {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.app_icon);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}
