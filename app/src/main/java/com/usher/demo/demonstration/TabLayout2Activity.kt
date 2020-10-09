package com.usher.demo.demonstration

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.rxbinding4.viewpager2.pageSelections
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_tablayout2.*
import kotlinx.android.synthetic.main.fragment_tab_layout.*
import kotlinx.android.synthetic.main.smart_tab_item.view.*

class TabLayout2Activity : BaseActivity(R.layout.activity_tablayout2, Theme.LIGHT_AUTO) {

    override fun initView() {
        val data = listOf(*resources.getStringArray(R.array.selected_channels))

        tab_viewpager.adapter = PagerFragmentAdapter(this, data)

        /**
         * 连接ViewPager和TabLayout
         * 遍历TabLayout中的Tab, 设置CustomView
         */
        TabLayoutMediator(tablayout, tab_viewpager) { tab, position ->
            tab.setCustomView(R.layout.smart_tab_item)
            tab.customView?.textview?.text = data[position]
        }.attach()

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

        /**
         * 设置ViewPager2内部RecyclerView的overScrollMode
         */
        (tab_viewpager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private class PagerFragmentAdapter(activity: FragmentActivity, private val data: List<String>) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = data.size

        override fun createFragment(position: Int): Fragment = TabFragment.newInstance(data[position])
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