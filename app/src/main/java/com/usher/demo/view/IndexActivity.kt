package com.usher.demo.view

import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_index.*

class IndexActivity : BaseActivity(R.layout.activity_index) {

    override fun initView() {
        val indexData = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")

        indexview.setData(indexData)
        indexview2.setData(indexData)
    }
}