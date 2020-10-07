package com.usher.demo.demonstration

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_tablayout1.*
import kotlinx.android.synthetic.main.fragment_tab_layout.*

class TabLayout1Activity : BaseActivity(R.layout.activity_tablayout1, Theme.LIGHT_AUTO) {

    override fun initView() {
        val data = listOf(*resources.getStringArray(R.array.sticky_list1))

        tab_viewpager.adapter = PagerFragmentAdapter(supportFragmentManager, data)
    }

    private class PagerFragmentAdapter(fm: FragmentManager, private val data: List<String>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = TabFragment.newInstance(data[position])

        override fun getCount(): Int = data.size
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

            val color = Color.rgb((0..256).random(), (0..256).random(), (0..256).random())

//            pager_imageview.setImageDrawable(ColorDrawable(color))
        }
    }
}