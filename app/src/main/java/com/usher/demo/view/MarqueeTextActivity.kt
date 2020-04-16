package com.usher.demo.view

import android.os.Bundle
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_marquee_text.*

class MarqueeTextActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marquee_text)
        initView()
    }

    private fun initView() {
        val content = "Centers the image in the display and keeps its aspect ratio. Image is scaled up or down to fit its shorter side to the display. The longer side of the image is cropped."
        marquee_textview.text = content
    }
}