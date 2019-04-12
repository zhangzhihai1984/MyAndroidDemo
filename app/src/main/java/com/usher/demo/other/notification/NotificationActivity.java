package com.usher.demo.other.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.usher.demo.R;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class NotificationActivity extends AppCompatActivity {
    private static final String MESSAGE_CHANNEL_GROUP_ID = "MESSAGE_CHANNEL_GROUP_ID";
    private static final String MESSAGE_CHANNEL_GROUP_NAME = "消息中心消息提示类型";

    private static final String VIBRATE_CHANNEL_ID = "VIBRATE_CHANNEL_ID";
    private static final String VIBRATE_CHANNEL_NAME = "震动";

    private static final String SOUND_CHANNEL_ID = "SOUND_CHANNEL_ID";
    private static final String SOUND_CHANNEL_NAME = "提示音";

    private static final String ALL_CHANNEL_ID = "ALL_CHANNEL_ID";
    private static final String ALL_CHANNEL_NAME = "震动 + 提示音";

    private static final String NONE_CHANNEL_ID = "NONE_CHANNEL_ID";
    private static final String NONE_CHANNEL_NAME = "无";

    private NotificationManager mNotificationManager;

    private int mNotificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initData();
        initView();
        doTest();
    }

    private void initData() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelGroup group = new NotificationChannelGroup(MESSAGE_CHANNEL_GROUP_ID, MESSAGE_CHANNEL_GROUP_NAME);
            mNotificationManager.createNotificationChannelGroup(group);

            NotificationChannel vibrateChannel = new NotificationChannel(VIBRATE_CHANNEL_ID, VIBRATE_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            vibrateChannel.setGroup(MESSAGE_CHANNEL_GROUP_ID);
            vibrateChannel.enableLights(true);
            vibrateChannel.setShowBadge(true);
            vibrateChannel.enableVibration(true);
            vibrateChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(vibrateChannel);

            NotificationChannel soundChannel = new NotificationChannel(SOUND_CHANNEL_ID, SOUND_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            soundChannel.setGroup(MESSAGE_CHANNEL_GROUP_ID);
            soundChannel.enableLights(true);
            soundChannel.setShowBadge(true);
            soundChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(soundChannel);

            NotificationChannel allChannel = new NotificationChannel(ALL_CHANNEL_ID, ALL_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            allChannel.setGroup(MESSAGE_CHANNEL_GROUP_ID);
            allChannel.enableLights(true);
            allChannel.setShowBadge(true);
            allChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(allChannel);

            NotificationChannel noneChannel = new NotificationChannel(NONE_CHANNEL_ID, NONE_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            noneChannel.setGroup(MESSAGE_CHANNEL_GROUP_ID);
            noneChannel.enableLights(true);
            noneChannel.setShowBadge(true);
            noneChannel.enableVibration(false);
            noneChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(noneChannel);
        }

        registerReceiver(mReceiver, new IntentFilter("FullScreenIntentAction"));
    }

    private void initView() {
        findViewById(R.id.vibrate_button).setOnClickListener(v -> {
            Notification notification = getNotification(VIBRATE_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                    .build();

            mNotificationManager.notify(mNotificationId++, notification);
        });

        findViewById(R.id.sound_button).setOnClickListener(v -> {
            Notification notification = getNotification(SOUND_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                    .build();

            mNotificationManager.notify(mNotificationId++, notification);
        });

        findViewById(R.id.all_button).setOnClickListener(v -> {
            Notification notification = getNotification(ALL_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setFullScreenIntent(PendingIntent.getBroadcast(this, 0, new Intent("FullScreenIntentAction"), PendingIntent.FLAG_UPDATE_CURRENT), true)
//                    .setFullScreenIntent(null, false)
                    .build();

            notification.flags |= Notification.FLAG_INSISTENT;

            findViewById(R.id.all_button).postDelayed(() -> mNotificationManager.notify(mNotificationId++, notification), 3000);
//            mNotificationManager.notify(mNotificationId++, notification);
        });

        findViewById(R.id.none_button).setOnClickListener(v -> {
            Notification notification = getNotification(NONE_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .build();

            mNotificationManager.notify(mNotificationId++, notification);
        });
    }

    private PendingIntent getPendingIntent() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setComponent(new ComponentName("com.usher.another", "com.usher.another.MainActivity"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        return PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Builder getNotification(String channelId) {
        return new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker("Ticker")
                .setContentTitle("ContentTitle")
                .setContentText("ContentText")
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent())
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis());
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("zzh", "Open FullScreenIntent");
        }
    };

    private void doTest() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                Log.i("zzh", "[ProcessInfo] processName: " + processInfo.processName);
            }

            List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfos) {
                Log.i("zzh", "[TaskInfo] packageName: " + taskInfo.baseActivity.getPackageName());
                Log.i("zzh", "[TaskInfo] className: " + taskInfo.baseActivity.getClassName());
            }
            List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(10);
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
                Log.i("zzh", "[ServiceInfo] clientPackage: " + serviceInfo.clientPackage);
                Log.i("zzh", "[ServiceInfo] process:  " + serviceInfo.process);
            }
        }

        Intent intent = new Intent("TestAction");
        intent.setComponent(new ComponentName("com.usher.another", "com.usher.another.MyReceiver"));
        sendBroadcast(intent);

        /*Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 100;
        params.width = 100;
        window.setAttributes(params);*/

        Log.i("zzh", "Current PackageName: " + getPackageName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }
}
