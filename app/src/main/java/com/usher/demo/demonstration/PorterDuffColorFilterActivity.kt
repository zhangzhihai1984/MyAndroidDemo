package com.usher.demo.demonstration

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.viewpager.pageSelections
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_colorfilter_porterduff.*
import kotlinx.android.synthetic.main.fragment_colorfilter_porterduff.*
import kotlinx.android.synthetic.main.item_colorfilter_porterduff.view.*

class PorterDuffColorFilterActivity : BaseActivity(R.layout.activity_colorfilter_porterduff) {

    override fun initView() {
        val resIds = listOf(R.drawable.demo_plaster, R.drawable.picasso_reading_at_a_table, R.drawable.demo_bottle, R.drawable.demo_bottle2)
        val adapter = ColorFilterFragmentAdapter(supportFragmentManager, resIds)

        viewpager.adapter = adapter
        viewpager.pageSelections()
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    original_imageview.setImageResource(resIds[position])
                }

        indicatorview.setViewPager(viewpager)

        color_seeker_view.colorSeeks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { color ->
                    adapter.updateColor(color)
                }

        color_seeker_view.updateColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private class ColorFilterFragmentAdapter(fm: FragmentManager, private val resIds: List<Int>, private var color: Int = Color.TRANSPARENT) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = ColorFilterFragment.newInstance(resIds[position], color)

        override fun getCount(): Int = resIds.size

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

        fun updateColor(color: Int) {
            this.color = color
            notifyDataSetChanged()
        }
    }

    class ColorFilterFragment : BasePagerFragment(R.layout.fragment_colorfilter_porterduff) {
        companion object {
            private const val RESID = "RESID"
            private const val COLOR = "COLOR"

            fun newInstance(resId: Int, color: Int) =
                    ColorFilterFragment().apply {
                        arguments = Bundle().apply {
                            putInt(RESID, resId)
                            putInt(COLOR, color)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                val resId = getInt(RESID)
                val color = getInt(COLOR)
                val modes = listOf(
                        PorterDuff.Mode.SRC_OVER,
                        PorterDuff.Mode.SRC_IN,
                        PorterDuff.Mode.SRC_OUT,
                        PorterDuff.Mode.SRC_ATOP,
                        PorterDuff.Mode.DARKEN,
                        PorterDuff.Mode.LIGHTEN,
                        PorterDuff.Mode.MULTIPLY,
                        PorterDuff.Mode.SCREEN,
                        PorterDuff.Mode.ADD,
                        PorterDuff.Mode.OVERLAY
                )

                recyclerview.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
                recyclerview.adapter = ColorFilterAdapter(modes, resId, color)
            }
        }

        private class ColorFilterAdapter(data: List<PorterDuff.Mode>, val resId: Int, var color: Int) : RxBaseQuickAdapter<PorterDuff.Mode, BaseViewHolder>(R.layout.item_colorfilter_porterduff, data) {
            override fun convert(holder: BaseViewHolder, mode: PorterDuff.Mode) {
                holder.itemView.run {
                    colorfilter_imageview.setImageResource(resId)
                    colorfilter_imageview.setColorFilter(color, mode)

                    mode_textview.text = mode.name
                }
            }
        }
    }
}