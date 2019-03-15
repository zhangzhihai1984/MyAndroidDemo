package com.usher.demo.launchmode;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.List;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        checkProcess();
    }

    private void checkProcess() {
        Log.i("zzh", "MyApplication MainProcessName:: " + getPackageName());
        Log.i("zzh", "MyApplication MyPid: " + Process.myPid());
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                Log.i("zzh", "MyApplication processName: " + processInfo.processName);
                Log.i("zzh", "MyApplication pid: " + processInfo.pid);
            }
        }
    }
}
