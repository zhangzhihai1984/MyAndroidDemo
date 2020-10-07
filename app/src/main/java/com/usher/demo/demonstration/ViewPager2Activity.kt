package com.usher.demo.demonstration

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_viewpager2.*
import kotlinx.android.synthetic.main.fragment_pager.*

class ViewPager2Activity : BaseActivity(R.layout.activity_viewpager2, Theme.LIGHT_AUTO) {

    override fun initView() {
        val adapterH = PagerFragmentAdapter(this)
        val adapterV = PagerFragmentAdapter(this)

        viewpager2_horizontal.adapter = adapterH
        viewpager2_vertical.adapter = adapterV

        indicatorview_horizontal.setViewPager(viewpager2_horizontal)
        indicatorview_vertical.setViewPager(viewpager2_vertical)

        (viewpager2_horizontal.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        (viewpager2_vertical.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private class PagerFragmentAdapter(activity: FragmentActivity, private var mSize: Int = 5) : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment = Pager2Fragment.newInstance(position)

        override fun getItemCount(): Int = mSize
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