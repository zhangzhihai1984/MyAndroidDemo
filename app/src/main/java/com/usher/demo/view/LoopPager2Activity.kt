package com.usher.demo.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.jakewharton.rxbinding4.viewpager2.pageScrollStateChanges
import com.jakewharton.rxbinding4.viewpager2.pageSelections
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_loop_pager2.*
import kotlinx.android.synthetic.main.fragment_pager.*

class LoopPager2Activity : BaseActivity(R.layout.activity_loop_pager2, Theme.LIGHT_AUTO) {

    override fun initView() {
        viewpager2.adapter = LoopPagerFragmentAdapter(this, PagerFragmentAdapter(this))
        viewpager2.currentItem = 1

        indicatorview.createIndicators(5)
        indicatorview2.createIndicators(5)

        viewpager2.pageSelections()
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    val revisedPos = (position - 1 + 5) % 5
                    indicatorview.selectPage(revisedPos)
                    indicatorview2.selectPage(revisedPos)
                }

//        val pages = viewpager2.pageSelections()
//                .doOnNext { position ->
//                    indicatorview.selectPage(position)
//                    indicatorview2.selectPage(position)
//                }

        viewpager2.pageScrollStateChanges()
                .filter { it == ViewPager2.SCROLL_STATE_IDLE }
                .to(RxUtil.autoDispose(this))
                .subscribe { }
    }

    private class PagerFragmentAdapter(activity: FragmentActivity, private var mSize: Int = 5) : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment = Pager2Fragment.newInstance((position - 1 + mSize) % mSize)

        override fun getItemCount(): Int = mSize
    }

    private class LoopPagerFragmentAdapter(activity: FragmentActivity, private val rawAdapter: FragmentStateAdapter) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int =
                when {
                    rawAdapter.itemCount <= 1 -> rawAdapter.itemCount
                    else -> rawAdapter.itemCount + 2
                }

        override fun createFragment(position: Int): Fragment = rawAdapter.createFragment(position)

    }

    class Pager2Fragment : BasePagerFragment(R.layout.fragment_pager) {
        companion object {
            private const val POSITION = "POSITION"

            fun newInstance(position: Int): Pager2Fragment =
                    Pager2Fragment().apply {
                        arguments = Bundle().apply {
                            putInt(POSITION, position)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                textview.text = "${getInt(POSITION)}"
            }

            val color = Color.rgb((0..256).random(), (0..256).random(), (0..256).random())

            pager_imageview.setImageDrawable(ColorDrawable(color))
        }
    }
}