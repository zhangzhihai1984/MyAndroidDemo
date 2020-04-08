package com.usher.demo.view.pager

import android.os.Bundle
import com.twigcodes.ui.fragment.BasePagerFragment
import com.usher.demo.R
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.fragment_pager.*

class PagerFragment(layoutRes: Int) : BasePagerFragment(layoutRes) {
    companion object {
        fun newInstance(num: Int): PagerFragment =
                PagerFragment(R.layout.fragment_pager).apply {
                    arguments = Bundle().apply {
                        putInt(Constants.TAG_DATA, num)
                    }
                }
    }

    override fun init() {
        arguments?.let {
            textview.text = "${it.getInt(Constants.TAG_DATA)}"
        }
    }
}