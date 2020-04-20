package com.usher.demo.view

import android.os.Bundle
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
    }

    override fun onDestroy() {
        super.onDestroy()
        scanview.destroy()
    }
}