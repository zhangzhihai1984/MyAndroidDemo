package com.usher.demo.web.three;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.usher.demo.R;

import androidx.appcompat.app.AppCompatActivity;

public class ThreeActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_three);

        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mWebView = findViewById(R.id.webview);
        mWebView.setVerticalScrollBarEnabled(false);
//        mWebView.setInitialScale(100);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);

        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setDefaultTextEncodingName("utf-8");

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.loadUrl("file:///android_asset/three/index.html");
//        mWebView.loadUrl("https://twigcodes.com/three/index.html");
    }
}
