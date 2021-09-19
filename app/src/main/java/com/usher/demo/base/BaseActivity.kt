package com.usher.demo.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.twigcodes.ui.activity.BaseActivity
import com.usher.demo.main.DemoListActivity
import com.usher.demo.utils.CommonUtil

@SuppressLint("Registered")
open class BaseActivity(contentLayoutId: Int, statusBarThemeForDayMode: Theme = Theme.LIGHT_AUTO, fullScreen: Boolean = true, hideNavigationBar: Boolean = false) : BaseActivity(contentLayoutId, statusBarThemeForDayMode, fullScreen, hideNavigationBar) {
    enum class NightModeStrategy {
        AUTO,
        YES,
        NO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nightMode = when (CommonUtil.getNightModeStrategy(this)) {
            NightModeStrategy.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            NightModeStrategy.YES -> AppCompatDelegate.MODE_NIGHT_YES
            NightModeStrategy.NO -> AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    override fun onUIModeChanged(isNightMode: Boolean) {
        startActivity(Intent(this, DemoListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
//        updateStatusBarTheme(isNightMode)
    }
}