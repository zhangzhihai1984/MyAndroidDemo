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
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.ImageUtil
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
        val resIds = listOf(R.drawable.demo_hardworking, R.drawable.demo_trump)

        viewpager.adapter = ColorFilterFragmentAdapter(supportFragmentManager, resIds)
        indicatorview.setViewPager(viewpager)
    }

    private class ColorFilterFragmentAdapter(fm: FragmentManager, private val mResIds: List<Int>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = ColorFilterFragment.newInstance(mResIds[position])

        override fun getCount(): Int = mResIds.size
    }

    class ColorFilterFragment : BasePagerFragment(R.layout.fragment_colorfilter_porterduff) {
        companion object {
            fun newInstance(resId: Int) =
                    ColorFilterFragment().apply {
                        arguments = Bundle().apply {
                            putInt(Constants.TAG_DATA, resId)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                val resId = getInt(Constants.TAG_DATA)
                val bitmap = resources.getDrawable(resId, null).toBitmap()
                val modes = listOf(
                        PorterDuff.Mode.DARKEN,
                        PorterDuff.Mode.LIGHTEN,
                        PorterDuff.Mode.MULTIPLY,
                        PorterDuff.Mode.SCREEN,
                        PorterDuff.Mode.ADD,
                        PorterDuff.Mode.OVERLAY
                )

                recyclerview.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
                recyclerview.adapter = ColorFilterAdapter(modes, bitmap)
            }
        }
    }

    private class ColorFilterAdapter(data: List<PorterDuff.Mode>, val bitmap: Bitmap) : RxBaseQuickAdapter<PorterDuff.Mode, BaseViewHolder>(R.layout.item_colorfilter_porterduff, data) {
        override fun convert(helper: BaseViewHolder, mode: PorterDuff.Mode) {
            helper.getView<ImageView>(R.id.colorfilter_imageview).setImageBitmap(ImageUtil.getColorFilterBitmap(bitmap, Color.RED, mode))
            helper.setText(R.id.mode_textview, mode.name)
        }
    }
}