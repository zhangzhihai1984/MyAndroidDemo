package com.usher.demo.text;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.usher.demo.R;

public class MarqueeTextActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private boolean mFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee_text);
    }

    private String getHello() {
        int a = 1;
        int b = 2;
        String c = a + b + "";
        return c + "";
    }
}
