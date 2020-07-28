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
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.activity_pager.*
import kotlinx.android.synthetic.main.fragment_pager.*
import kotlin.math.max

class PagerActivity : BaseActivity(Theme.LIGHT_AUTO) {
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
                .to(RxUtil.autoDispose(this))
                .subscribe { mPagerAdapter.add() }

        remove_imageview.clicks()
                .to(RxUtil.autoDispose(this))
                .subscribe { mPagerAdapter.remove() }
    }

    private class PagerFragmentAdapter(fm: FragmentManager, private var mSize: Int = 5) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val pos = LoopViewPager.getMatchedPosition(position, count)
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
            private const val RESID = "RESID"

            fun newInstance(num: Int): PagerFragment =
                    PagerFragment().apply {
                        arguments = Bundle().apply {
                            putInt(RESID, num)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                textview.text = "${getInt(RESID)}"
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