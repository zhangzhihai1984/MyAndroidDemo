package com.usher.demo.base

import android.annotation.SuppressLint
import android.os.Bundle
import com.twigcodes.ui.activity.BaseActivity
import com.usher.demo.R

@SuppressLint("Registered")
open class BaseActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (isDarkMode())
//            setTheme(R.style.DarkTheme)
//        else {
//            setTheme(R.style.LightTheme)
//            statusBarTheme = Theme.LIGHT
//        }
    }
}