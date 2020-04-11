package com.usher.demo.view.pager

import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_pager.*

class PagerActivity : BaseActivity(Theme.LIGHT) {
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

        add_imageview.clicks()
                .`as`(RxUtil.autoDispose(this))
                .subscribe { mPagerAdapter.add() }

        remove_imageview.clicks()
                .`as`(RxUtil.autoDispose(this))
                .subscribe { mPagerAdapter.remove() }

//        val d = add_imageview.drawable
//        d.colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
    }
}