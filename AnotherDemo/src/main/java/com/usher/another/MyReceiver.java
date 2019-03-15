package com.usher.another;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("zzh", "onReceive");
//        context.startActivity(new Intent(context, LoginActivity.class));
        context.startService(new Intent(context, MyService.class));
    }
}
