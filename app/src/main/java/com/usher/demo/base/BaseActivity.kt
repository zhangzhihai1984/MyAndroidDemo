package com.usher.demo.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.twigcodes.ui.activity.BaseActivity
import com.usher.demo.main.DemoListActivity

@SuppressLint("Registered")
open class BaseActivity(contentLayoutId: Int, statusBarThemeForDayMode: Theme = Theme.LIGHT_AUTO, fullScreen: Boolean = true, hideNavigationBar: Boolean = false) : BaseActivity(contentLayoutId, statusBarThemeForDayMode, fullScreen, hideNavigationBar) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onUIModeChanged(isNightMode: Boolean) {
        startActivity(Intent(this, DemoListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
//        updateStatusBarTheme(isNightMode)
    }
}