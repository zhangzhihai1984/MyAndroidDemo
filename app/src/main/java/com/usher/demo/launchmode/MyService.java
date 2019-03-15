package com.usher.demo.launchmode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.usher.demo.R;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        Log.i("zzh", "MyService onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("zzh", "MyService onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("zzh", "MyService onStartCommand");

//        Intent i = new Intent(getApplicationContext(), BActivity.class);
//        startActivity(i);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel("SERVICE_CHANNEL_ID", "ForegroundService", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

//        startForeground(1, getNotification("SERVICE_CHANNEL_ID").build());

//        Log.i("zzh", "Crash Log: " + (1/0));

        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationCompat.Builder getNotification(String channelId) {
        return new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Demo foreground serivce is running")
                .setContentText("ContentText")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis());
    }

    @Override
    public void onDestroy() {
        Log.i("zzh", "MyService onDestroy");
        super.onDestroy();
    }
}
