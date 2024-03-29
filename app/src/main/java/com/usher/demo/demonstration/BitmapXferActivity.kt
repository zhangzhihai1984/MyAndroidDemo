package com.usher.demo.demonstration

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.activity_bitmap_xfer.*
import kotlinx.android.synthetic.main.fragment_bitmap_xfer.*
import kotlinx.android.synthetic.main.item_bitmap_xfer.view.*

class BitmapXferActivity : BaseActivity(R.layout.activity_bitmap_xfer) {

    override fun initView() {
        val resIds = listOf(R.drawable.picasso_reading_at_a_table, R.drawable.demo_plaster, R.drawable.picasso_girl_before_a_mirror)

        viewpager.adapter = BitmapXferFragmentAdapter(supportFragmentManager, resIds)
        indicatorview.setViewPager(viewpager)
    }

    private class BitmapXferFragmentAdapter(fm: FragmentManager, private val resIds: List<Int>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = BitmapXferFragment.newInstance(resIds[position])

        override fun getCount(): Int = resIds.size
    }

    class BitmapXferFragment : BasePagerFragment(R.layout.fragment_bitmap_xfer) {
        companion object {
            private const val RESID = "RESID"

            fun newInstance(resId: Int) =
                    BitmapXferFragment().apply {
                        arguments = Bundle().apply {
                            putInt(RESID, resId)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                val resId = getInt(RESID)
                ContextCompat.getDrawable(requireContext(), resId)?.toBitmap()?.let { bitmap ->
                    val bitmapPairs = listOf(
                            bitmap to "ORIGINAL",
                            ImageUtil.getRenderScriptBlurBitmap(requireContext(), bitmap, 25f) to "SCRIPT BLUR",
                            ImageUtil.getRenderScriptBlurScaledBitmap(requireContext(), bitmap) to "SCRIPT BLUR PRO",
                            ImageUtil.getScaledBlurBitmap(bitmap, 16f) to "SCALED BLUR",
                            ImageUtil.getSquareBitmap(bitmap) to "SQUARE",
                            ImageUtil.getCircleBitmap(bitmap) to "CIRCLE",
                            ImageUtil.getRoundBitmap(bitmap, 100f) to "ROUND",
                            ImageUtil.getPorterDuffColorFilterBitmap(bitmap, Color.parseColor("#4400ffff"), PorterDuff.Mode.MULTIPLY) to "PORTERDUFF FILTER",
                            ImageUtil.getPorterDuffColorFilterBitmap(bitmap, Color.CYAN, PorterDuff.Mode.OVERLAY) to "PORTERDUFF FILTER",
                            ImageUtil.getLightingColorFilterBitmap(bitmap, Color.WHITE, Color.CYAN) to "LIGHTING FILTER"
                    )

                    recyclerview.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
                    recyclerview.adapter = BitmapXferAdapter(bitmapPairs)
                }
            }
        }
    }

    private class BitmapXferAdapter(data: List<Pair<Bitmap, String>>) : RxBaseQuickAdapter<Pair<Bitmap, String>, BaseViewHolder>(R.layout.item_bitmap_xfer, data) {
        override fun convert(holder: BaseViewHolder, xferPair: Pair<Bitmap, String>) {
            holder.itemView.run {
                xfer_imageview.setImageBitmap(xferPair.first)
                textview.text = xferPair.second
            }
        }
    }
}