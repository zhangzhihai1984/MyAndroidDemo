package com.usher.demo.view

import android.os.Bundle
import com.usher.demo.R
import com.usher.demo.base.BaseActivity

class MarqueeTextActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marquee_text)
    }
}