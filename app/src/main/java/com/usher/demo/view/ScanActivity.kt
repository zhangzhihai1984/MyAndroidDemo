package com.usher.demo.view

import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : BaseActivity(R.layout.activity_scan, Theme.DARK_ONLY) {

    override fun onDestroy() {
        super.onDestroy()
        scanview.destroy()
    }
}