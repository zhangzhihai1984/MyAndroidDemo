package com.usher.demo.view

import android.os.Bundle
import com.usher.demo.R
import com.usher.demo.base.BaseActivity

class ColorPickerActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)
    }
}