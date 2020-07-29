package com.usher.demo.demonstration

import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.jakewharton.rxbinding4.viewpager.pageSelections
import com.jakewharton.rxbinding4.widget.changes
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.ImageUtil
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bitmap_blur.*
import kotlinx.android.synthetic.main.fragment_bitmap_blur.*

class BitmapBlurActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_blur)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.picasso_dora_maar_au_chat, R.drawable.picasso_the_weeping_woman, R.drawable.picasso_portrait_of_dora_maar)
        val adapter = BlurFragmentAdapter(supportFragmentManager, resIds, 8f, 8f)

        viewpager.adapter = adapter
        viewpager.pageSelections()
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    original_imageview.setImageResource(resIds[position])
                }
//
        indicatorview.setViewPager(viewpager)

        scale_seekbar.changes()
                .to(RxUtil.autoDispose(this))
                .subscribe { scale ->
                    scale_textview.text = "$scale"
                    adapter.updateBlur(scale.toFloat(), radius_seekbar.progress.toFloat())
                }

        radius_seekbar.changes()
                .to(RxUtil.autoDispose(this))
                .subscribe { radius ->
                    radius_textview.text = "$radius"
                    adapter.updateBlur(scale_seekbar.progress.toFloat(), radius.toFloat())
                }
    }

    private class BlurFragmentAdapter(fm: FragmentManager, private val resIds: List<Int>, private var scale: Float, private var radius: Float) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = BlurFragment.newInstance(resIds[position], scale, radius)

        override fun getCount(): Int = resIds.size

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

        fun updateBlur(scale: Float, radius: Float) {
            this.scale = scale
            this.radius = radius
            notifyDataSetChanged()
        }
    }

    class BlurFragment : BasePagerFragment(R.layout.fragment_bitmap_blur) {
        companion object {
            private const val RESID = "RESID"
            private const val SCALE = "SCALE"
            private const val RADIUS = "RADIUS"

            fun newInstance(resId: Int, scale: Float, radius: Float) =
                    BlurFragment().apply {
                        arguments = Bundle().apply {
                            putInt(RESID, resId)
                            putFloat(SCALE, scale)
                            putFloat(RADIUS, radius)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                val resId = getInt(RESID)
                val scale = getFloat(SCALE)
                val radius = getFloat(RADIUS)
                val bitmap = resources.getDrawable(resId, null).toBitmap()
                blur_imageview.setImageBitmap(ImageUtil.getRenderScriptBlurScaledBitmap(requireContext(), bitmap, scale, radius))
            }
        }
    }
}