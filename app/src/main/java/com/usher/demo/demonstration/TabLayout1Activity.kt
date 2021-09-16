package com.usher.demo.demonstration

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jakewharton.rxbinding4.viewpager.pageSelections
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_tablayout1.*
import kotlinx.android.synthetic.main.fragment_tab_layout.*
import kotlinx.android.synthetic.main.smart_tab_item.view.*

/**
 * tabRippleColor:
 * 如果我们不需要ripple效果的话, 直接将其设置为transparent.
 *
 * tabIndicator:
 * 可以简单的认为shape中的color是无效的, 也就是说tabIndicator只决定形状, 颜色还是需要设置tabIndicatorColor.
 * (通过测试发现, tabIndicator中color的alpha还是起作用的)
 *
 * tabMinWidth:
 * Tab有默认的最小的宽度, 可以通过这个属性来修改, 当然可以直接将其设置为0.
 *
 * Tab的margin和padding:
 * 通过查看源码可以发现, TabLayout其实是一个HorizontalScrollView, 内部是一个SlidingTabIndicator, 这个View是
 * 一个LinearLayout, 然后每个TabView加到这个LinearLayout中.
 * 这样的话, 我们就可以通过getChildAt(0)先获取到TabLayout的SlidingTabIndicator, 然后遍历其children, 根据需要
 * 修改margin和padding.
 */
class TabLayout1Activity : BaseActivity(R.layout.activity_tablayout1) {

    override fun initView() {
        val data = listOf(*resources.getStringArray(R.array.selected_channels))

        tab_viewpager.adapter = PagerFragmentAdapter(supportFragmentManager, data)

        /**
         * 连接ViewPager和TabLayout
         */
        tablayout.setupWithViewPager(tab_viewpager)

        /**
         * 遍历TabLayout中的Tab, 设置CustomView
         */
        data.indices.forEach {
            tablayout.getTabAt(it)?.customView = getCustomView(data[it])
        }

        /**
         * 遍历TabLayout中的TabView, 设置margin和padding
         */
        (tablayout.getChildAt(0) as ViewGroup).children.forEach { view ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> { rightMargin = resources.getDimensionPixelSize(R.dimen.tab_margin_end) }
            view.setPadding(0)
        }

        /**
         * 监听滑动, 修改CustomView中的textSize
         */
        tab_viewpager.pageSelections()
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    data.indices.forEach {
                        tablayout.getTabAt(it)?.customView?.textview?.textSize = if (it == position) 19f else 14f
                    }
                }
    }

    @SuppressLint("InflateParams")
    private fun getCustomView(content: String): View =
            LayoutInflater.from(this).inflate(R.layout.smart_tab_item, null, false).apply {
                textview.text = content
            }

    private class PagerFragmentAdapter(fm: FragmentManager, private val data: List<String>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = TabFragment.newInstance(data[position])

        override fun getCount(): Int = data.size

        override fun getPageTitle(position: Int): CharSequence? = data[position]
    }

    class TabFragment : BasePagerFragment(R.layout.fragment_tab_layout) {
        companion object {
            private const val CONTENT = "CONTENT"

            fun newInstance(content: String): TabFragment =
                    TabFragment().apply {
                        arguments = Bundle().apply {
                            putString(CONTENT, content)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                pager_textview.text = getString(CONTENT)
            }
        }
    }
}