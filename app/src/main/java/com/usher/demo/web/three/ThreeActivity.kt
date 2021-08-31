package com.usher.demo.web.three

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_three.*

class ThreeActivity : BaseActivity(R.layout.activity_three) {

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        webview.settings.run {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
//            setSupportZoom(false)

            builtInZoomControls = false
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
//            allowUniversalAccessFromFileURLs = true
            defaultTextEncodingName = "utf-8"
        }

        webview.webChromeClient = WebChromeClient()
        webview.loadUrl("file:///android_asset/three/index.html")
        //        mWebView.loadUrl("https://twigcodes.com/three/index.html");
    }
}