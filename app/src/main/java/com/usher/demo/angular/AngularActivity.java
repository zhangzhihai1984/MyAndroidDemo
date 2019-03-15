package com.usher.demo.angular;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.usher.demo.R;

public class AngularActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_angular);

        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        WebView webView = findViewById(R.id.webview);

        webView.setWebViewClient(mWebViewClient);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("https://zhangzhihai1984.github.io/MyAngularDemo/");
    }

    private final WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.i("zzh", request.getUrl().toString());
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    };
}
