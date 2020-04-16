package com.usher.demo.view

import android.os.Bundle
import com.twigcodes.ui.wave.WaveView
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wave.*

class WaveActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave)
        initView()
    }

    private fun initView() {
        wave_view.setProgress(100)
    }
}