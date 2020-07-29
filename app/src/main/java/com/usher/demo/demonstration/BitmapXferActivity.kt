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
import kotlinx.android.synthetic.main.activity_bitmap_xfer.*
import kotlinx.android.synthetic.main.fragment_bitmap_xfer.*

class BitmapXferActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_xfer)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.picasso_girl_before_a_mirror, R.drawable.picasso_the_women_of_algiers, R.drawable.demo_plaster)

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
                val bitmap = resources.getDrawable(resId, null).toBitmap()
                val bitmapPairs = listOf(
                        bitmap to "ORIGINAL",
                        ImageUtil.getRenderScriptBlurScaledBitmap(requireContext(), bitmap) to "RENDER SCRIPT SCALED BLUR",
                        ImageUtil.getRenderScriptBlurBitmap(requireContext(), bitmap, 25f) to "RENDER SCRIPT BLUR",
                        ImageUtil.getScaledBlurBitmap(bitmap, 16f) to "SCALED BLUR",
                        ImageUtil.getSquareBitmap(bitmap) to "SQUARE",
                        ImageUtil.getCircleBitmap(bitmap) to "CIRCLE",
                        ImageUtil.getRoundBitmap(bitmap, 100f) to "ROUND",
                        ImageUtil.getPorterDuffColorFilterBitmap(bitmap, requireContext().getColor(R.color.colorPrimary)) to "COLOR FILTER",
                        ImageUtil.getPorterDuffColorFilterBitmap(bitmap, Color.parseColor("#66000000"), PorterDuff.Mode.SRC_ATOP) to "COLOR FILTER"
                )

                recyclerview.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
                recyclerview.adapter = BitmapXferAdapter(bitmapPairs)
            }
        }
    }

    private class BitmapXferAdapter(data: List<Pair<Bitmap, String>>) : RxBaseQuickAdapter<Pair<Bitmap, String>, BaseViewHolder>(R.layout.item_bitmap_xfer, data) {
        override fun convert(helper: BaseViewHolder, xferPair: Pair<Bitmap, String>) {
            helper.getView<ImageView>(R.id.xfer_imageview).setImageBitmap(xferPair.first)
            helper.setText(R.id.textview, xferPair.second)
        }
    }
}