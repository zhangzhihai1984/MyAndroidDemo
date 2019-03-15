package com.usher.another;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        Log.i("zzh", "MyAnotherService onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("zzh", "MyAnotherService onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("zzh", "MyAnotherService onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("zzh", "MyAnotherService onDestroy");
        super.onDestroy();
    }
}
