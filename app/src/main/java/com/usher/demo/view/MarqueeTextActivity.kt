package com.usher.demo.view

import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_marquee_text.*

class MarqueeTextActivity : BaseActivity(R.layout.activity_marquee_text) {

    override fun initView() {
        val content = "Centers the image in the display and keeps its aspect ratio. Image is scaled up or down to fit its shorter side to the display. The longer side of the image is cropped."
        marquee_textview.text = content
    }
}