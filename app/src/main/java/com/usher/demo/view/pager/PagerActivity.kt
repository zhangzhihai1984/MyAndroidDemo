package com.usher.demo.view.pager

import android.os.Bundle
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_pager.*

class PagerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        initView()
    }

    private fun initView() {
        val mPagerAdapter = PagerFragmentAdapter(supportFragmentManager)
        viewpager.adapter = mPagerAdapter
        indicatorview.setViewPager(viewpager)
        indicatorview2.setViewPager(viewpager)
    }
}