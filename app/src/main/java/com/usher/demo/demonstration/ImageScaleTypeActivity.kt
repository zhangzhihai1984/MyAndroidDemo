package com.usher.demo.demonstration

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.widget.CommonDialog
import kotlinx.android.synthetic.main.activity_image_scale_type.*
import kotlinx.android.synthetic.main.fragment_image_scale_type.*
import kotlinx.android.synthetic.main.item_image_scale_type.view.*

class ImageScaleTypeActivity : BaseActivity(R.layout.activity_image_scale_type) {

    override fun initView() {
        val resIds = listOf(R.drawable.picasso_the_weeping_woman, R.drawable.picasso_reading_at_a_table, R.drawable.demo_bottle_s)

        viewpager.adapter = ScaleTypeFragmentAdapter(supportFragmentManager, resIds)
        indicatorview.setViewPager(viewpager)
    }

    private class ScaleTypeFragmentAdapter(fm: FragmentManager, private val resIds: List<Int>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = ScaleTypeFragment.newInstance(resIds[position])

        override fun getCount(): Int = resIds.size
    }

    class ScaleTypeFragment : BasePagerFragment(R.layout.fragment_image_scale_type) {
        companion object {
            private const val RESID = "RESID"

            fun newInstance(resId: Int) =
                    ScaleTypeFragment().apply {
                        arguments = Bundle().apply {
                            putInt(RESID, resId)
                        }
                    }

            private fun getScaleTypeDesc(type: ImageView.ScaleType) =
                    when (type) {
                        ImageView.ScaleType.CENTER -> "No scaling of the image, just centering it and keeping its aspect ratio. This means that if it is larger than the display, it will be cropped, if smaller it will padded with background color."
                        ImageView.ScaleType.CENTER_CROP -> "Centers the image in the display and keeps its aspect ratio. Image is scaled up or down to fit its shorter side to the display. The longer side of the image is cropped."
                        ImageView.ScaleType.CENTER_INSIDE -> "Centers the image inside the display, and keeps its aspect ratio. It fits its longer side and pads the shorter side with an equal amount of background colored pixels. If the image's long side is smaller than the display this does the same as CENTER."
                        ImageView.ScaleType.FIT_CENTER -> "Behaves like CENTER_INSIDE, expect for the case when the image's longer side is smaller than the display, when it will scale up the image in order to fit its longer side to the display."
                        ImageView.ScaleType.FIT_START -> "Behaves like FIT_CENTER, but does not center the image. Instead its top left corner is aligned with the display's top left corner."
                        ImageView.ScaleType.FIT_END -> "Behaves like FIT_CENTER, but does not center the image. Instead its bottom right corner is aligned with the display's bottom right corner."
                        ImageView.ScaleType.FIT_XY -> "The only one that unlocks aspect ratio and will fit the image to the size of the display, which may cause some distortion, so be careful with this one."
                        ImageView.ScaleType.MATRIX -> "If none of the other 7 works for you, you can always use this one and provide your own scaling by assigning the output of a Matrix class transformation (Rotate, Scale, Skew, etc.) using a .setImageMatrix() method call."
                    }
        }

        override fun init() {
            val scaleTypes = listOf(
                    ImageView.ScaleType.CENTER,
                    ImageView.ScaleType.CENTER_CROP,
                    ImageView.ScaleType.CENTER_INSIDE,
                    ImageView.ScaleType.FIT_CENTER,
                    ImageView.ScaleType.FIT_START,
                    ImageView.ScaleType.FIT_END,
                    ImageView.ScaleType.FIT_XY,
                    ImageView.ScaleType.MATRIX
            )

            arguments?.run {
                val resId = getInt(RESID)
                val scaleTypeAdapter = ScaleTypeAdapter(scaleTypes, resId)

                recyclerview.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
                recyclerview.adapter = scaleTypeAdapter

                scaleTypeAdapter.itemClicks()
                        .compose(RxUtil.singleClick())
                        .flatMap {
                            CommonDialog(context as Context)
                                    .withTitle(scaleTypes[it].name)
                                    .withContent(getScaleTypeDesc(scaleTypes[it]))
                                    .withDialogType(CommonDialog.ButtonType.SINGLE)
                                    .clicks()
                        }
                        .to(RxUtil.autoDispose(context as LifecycleOwner))
                        .subscribe { }
            }
        }

        private class ScaleTypeAdapter(data: List<ImageView.ScaleType>, private val resId: Int) : RxBaseQuickAdapter<ImageView.ScaleType, BaseViewHolder>(R.layout.item_image_scale_type, data) {
            override fun convert(holder: BaseViewHolder, type: ImageView.ScaleType) {
                holder.itemView.run {
                    scale_type_imageview.setImageResource(resId)
                    scale_type_imageview.scaleType = type

                    textview.text = type.name
                }
            }
        }
    }
}