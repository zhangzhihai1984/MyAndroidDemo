package com.usher.demo.demonstration

import android.os.Bundle
import com.usher.demo.R
import com.usher.demo.base.BaseActivity

class LightingColorFilterActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lighting_colorfilter)
    }
}