package com.usher.demo.loading;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TestView extends View {
    public TestView(Context context) {
        super(context);
        Log.i("zzh", "1");
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i("zzh", "2");
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("zzh", "3");
    }
}
