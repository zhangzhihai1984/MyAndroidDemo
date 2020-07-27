package com.usher.demo.demonstration

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.PorterDuffXfermodeDiagramView
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_porterduff_xfermode.*
import kotlinx.android.synthetic.main.fragment_porterduff_xfermode.*

class PorterDuffXfermodeActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_porterduff_xfermode)
        initView()
    }

    private fun initView() {
        val colorPairs = listOf(
                Color.rgb(33, 150, 243) to Color.rgb(233, 30, 99),
                Color.parseColor("#66000000") to Color.rgb(233, 30, 99),
                Color.WHITE to Color.BLACK
        )

        viewpager.adapter = PorterDuffFragmentAdapter(supportFragmentManager, colorPairs)
        indicatorview.setViewPager(viewpager)
    }

    private class PorterDuffFragmentAdapter(fm: FragmentManager, private val mColorPairs: List<Pair<Int, Int>>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = PorterDuffFragment.newInstance(mColorPairs[position])

        override fun getCount(): Int = mColorPairs.size

    }

    class PorterDuffFragment : BasePagerFragment(R.layout.fragment_porterduff_xfermode) {
        companion object {
            private const val SRC_COLOR = "SRC_COLOR"
            private const val DST_COLOR = "DST_COLOR"

            fun newInstance(colorPair: Pair<Int, Int>) =
                    PorterDuffFragment().apply {
                        arguments = Bundle().apply {
                            putInt(SRC_COLOR, colorPair.first)
                            putInt(DST_COLOR, colorPair.second)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                val srcColor = getInt(SRC_COLOR)
                val dstColor = getInt(DST_COLOR)
                val modes = listOf(
                        PorterDuff.Mode.CLEAR,
                        PorterDuff.Mode.SRC,
                        PorterDuff.Mode.DST,
                        PorterDuff.Mode.SRC_OVER,
                        PorterDuff.Mode.DST_OVER,
                        PorterDuff.Mode.SRC_IN,
                        PorterDuff.Mode.DST_IN,
                        PorterDuff.Mode.SRC_OUT,
                        PorterDuff.Mode.DST_OUT,
                        PorterDuff.Mode.SRC_ATOP,
                        PorterDuff.Mode.DST_ATOP,
                        PorterDuff.Mode.XOR,
                        PorterDuff.Mode.DARKEN,
                        PorterDuff.Mode.LIGHTEN,
                        PorterDuff.Mode.MULTIPLY,
                        PorterDuff.Mode.SCREEN,
                        PorterDuff.Mode.ADD,
                        PorterDuff.Mode.OVERLAY
                )

                recyclerview.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
                recyclerview.adapter = PorterDuffAdapter(modes, srcColor, dstColor)
            }
        }

        private class PorterDuffAdapter(data: List<PorterDuff.Mode>, private val srcColor: Int, private val dstColor: Int) : RxBaseQuickAdapter<PorterDuff.Mode, BaseViewHolder>(R.layout.item_porterduff_xfermode, data) {
            override fun convert(helper: BaseViewHolder, mode: PorterDuff.Mode) {
                helper.getView<PorterDuffXfermodeDiagramView>(R.id.porterduff_view).update(mode, srcColor, dstColor)
                helper.setText(R.id.textview, mode.name)
            }
        }
    }
}