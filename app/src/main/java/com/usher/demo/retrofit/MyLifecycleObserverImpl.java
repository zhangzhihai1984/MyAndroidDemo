package com.usher.demo.retrofit;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.Log;

public class MyLifecycleObserverImpl implements MyLifecycleObserver {
    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.i("zzh", "Observer onCreate");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.i("zzh", "Observer onDestroy");
    }

    @Override
    public void onLifecycleChanged(@NonNull LifecycleOwner owner, @NonNull Lifecycle.Event event) {
        Log.i("zzh", "Observer onLifecycleChanged " + event.name());
    }
}
