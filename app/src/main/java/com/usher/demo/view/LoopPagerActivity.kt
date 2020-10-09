package com.usher.demo.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.pager.LoopViewPager
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_loop_pager.*
import kotlinx.android.synthetic.main.fragment_pager.*
import kotlin.math.max

class LoopPagerActivity : BaseActivity(R.layout.activity_loop_pager, Theme.LIGHT_AUTO) {

    override fun initView() {
        val pagerAdapter = PagerFragmentAdapter(supportFragmentManager)

        viewpager.adapter = pagerAdapter
        indicatorview.setViewPager(viewpager)
        indicatorview2.setViewPager(viewpager)

        add_imageview.clicks()
                .to(RxUtil.autoDispose(this))
                .subscribe { pagerAdapter.add() }

        remove_imageview.clicks()
                .to(RxUtil.autoDispose(this))
                .subscribe { pagerAdapter.remove() }
    }

    private class PagerFragmentAdapter(fm: FragmentManager, private var mSize: Int = 5) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val pos = LoopViewPager.getRevisedPosition(position, count)
            return PagerFragment.newInstance(pos + 1)
        }

        override fun getCount(): Int = mSize

        fun add() {
            mSize++
            notifyDataSetChanged()
        }

        fun remove() {
            mSize--
            mSize = max(mSize, 1)
            notifyDataSetChanged()
        }
    }

    class PagerFragment : BasePagerFragment(R.layout.fragment_pager) {
        companion object {
            private const val POSITION = "POSITION"

            fun newInstance(position: Int): PagerFragment =
                    PagerFragment().apply {
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
//            val hsv = FloatArray(3)
//            Color.colorToHSV(color, hsv)
//            hsv[1] = hsv[1] + 0.2f
//            hsv[2] = hsv[2] - 0.2f
//            val dark = Color.HSVToColor(hsv)
        }
    }
}