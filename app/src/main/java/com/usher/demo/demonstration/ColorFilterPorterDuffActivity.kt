package com.usher.demo.demonstration

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
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
import com.twigcodes.ui.util.ImageUtil
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.activity_colorfilter_porterduff.*
import kotlinx.android.synthetic.main.fragment_colorfilter_porterduff.*

class ColorFilterPorterDuffActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colorfilter_porterduff)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.demo_hardworking, R.drawable.demo_trump, R.drawable.demo_mall)
        val adapter = ColorFilterFragmentAdapter(supportFragmentManager, resIds)

        viewpager.adapter = adapter
        viewpager.pageSelections()
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    original_imageview.setImageResource(resIds[position])
                }

        indicatorview.setViewPager(viewpager)

        color_picker_view.colorPicks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { color ->
                    adapter.updateColor(color)
                }
    }

    private class ColorFilterFragmentAdapter(fm: FragmentManager, private val mResIds: List<Int>, private var color: Int = Color.RED) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = ColorFilterFragment.newInstance(mResIds[position], color)

        override fun getCount(): Int = mResIds.size

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

        fun updateColor(color: Int) {
            this.color = color
            notifyDataSetChanged()
        }
    }

    class ColorFilterFragment : BasePagerFragment(R.layout.fragment_colorfilter_porterduff) {
        companion object {
            fun newInstance(resId: Int, color: Int) =
                    ColorFilterFragment().apply {
                        arguments = Bundle().apply {
                            putIntegerArrayList(Constants.TAG_DATA, arrayListOf(resId, color))
                        }
                    }
        }

        override fun init() {
            arguments?.getIntegerArrayList(Constants.TAG_DATA)?.run {
                val resId = this[0]
                val color = this[1]
                val bitmap = resources.getDrawable(resId, null).toBitmap()
                val modes = listOf(
                        PorterDuff.Mode.DARKEN,
                        PorterDuff.Mode.LIGHTEN,
                        PorterDuff.Mode.MULTIPLY,
                        PorterDuff.Mode.SCREEN,
                        PorterDuff.Mode.ADD,
                        PorterDuff.Mode.OVERLAY
                )

                val adapter = ColorFilterAdapter(modes, bitmap, color)

                recyclerview.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
                recyclerview.adapter = adapter
            }
        }
    }

    private class ColorFilterAdapter(data: List<PorterDuff.Mode>, val bitmap: Bitmap, var color: Int) : RxBaseQuickAdapter<PorterDuff.Mode, BaseViewHolder>(R.layout.item_colorfilter_porterduff, data) {
        override fun convert(helper: BaseViewHolder, mode: PorterDuff.Mode) {
            helper.getView<ImageView>(R.id.colorfilter_imageview).setImageBitmap(ImageUtil.getColorFilterBitmap(bitmap, color, mode))
            helper.setText(R.id.mode_textview, mode.name)
        }
    }
}