package com.usher.demo.view.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.twigcodes.ui.pager.LoopViewPager
import kotlin.math.max

class PagerFragmentAdapter(fm: FragmentManager, private var mSize: Int = 5) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val pos = LoopViewPager.getMatchedPosition(position, count)
        return PagerFragment.newInstance(pos + 1)
    }

    override fun getCount(): Int = mSize

    override fun getItemPosition(obj: Any): Int {
//        return POSITION_NONE;
        return super.getItemPosition(obj)
    }

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