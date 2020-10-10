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
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_loop_pager2.*
import kotlinx.android.synthetic.main.fragment_pager.*
import java.util.concurrent.TimeUnit

class LoopPager2Activity : BaseActivity(R.layout.activity_loop_pager2, Theme.LIGHT_AUTO) {

    override fun initView() {
        val data = List(5) { it }
        val adapter = LoopPagerFragmentAdapter(this)

        viewpager2.adapter = adapter

        val pages = viewpager2.pageSelections()
                .doOnNext { position ->
                    val revisedPos = (position - 1 + 5) % 5

                    if (revisedPos != indicatorview.getCurrentItem()) {
                        indicatorview.setCurrentItem(revisedPos)
                        indicatorview2.setCurrentItem(revisedPos)
                    }
                }

        viewpager2.pageScrollStateChanges()
                .filter { it == ViewPager2.SCROLL_STATE_IDLE }
                .withLatestFrom(pages) { _, position ->
                    when (position) {
                        0 -> viewpager2.setCurrentItem(5, false)
                        5 + 1 -> viewpager2.setCurrentItem(1, false)
                    }
                }
                .to(RxUtil.autoDispose(this))
                .subscribe { }

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    adapter.data = data
                    adapter.notifyDataSetChanged()
                    viewpager2.currentItem = 1
                    indicatorview.createIndicators(data.size)
                    indicatorview2.createIndicators(data.size)
                }
    }

//    private class PagerFragmentAdapter(activity: FragmentActivity, private var mSize: Int = 5) : FragmentStateAdapter(activity) {
//        override fun createFragment(position: Int): Fragment = Pager2Fragment.newInstance((position - 1 + mSize) % mSize)
//
//        override fun getItemCount(): Int = mSize
//    }

    private class LoopPagerFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        var data = listOf<Int>()
        override fun getItemCount(): Int =
                when {
                    data.size <= 1 -> data.size
                    else -> data.size + 2
                }

        override fun createFragment(position: Int): Fragment = Pager2Fragment.newInstance((position - 1 + data.size) % data.size)

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