package com.usher.demo.image

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
import com.squareup.picasso.Picasso
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.Constants
import com.usher.demo.widget.CommonDialog
import kotlinx.android.synthetic.main.activity_image_scale_type.*
import kotlinx.android.synthetic.main.fragment_image_scale_type.*

class ImageScaleTypeActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scale_type)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.demo_tree, R.drawable.demo_mall, R.drawable.demo_child)

        viewpager.adapter = ScaleTypeFragmentAdapter(supportFragmentManager, resIds)
        indicatorview.setViewPager(viewpager)
    }

    class ScaleTypeFragmentAdapter(fm: FragmentManager, private val mResIds: List<Int>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = ScaleTypeFragment.newInstance(mResIds[position])

        override fun getCount(): Int = mResIds.size
    }

    companion object {
        fun getScaleTypeDesc(type: ImageView.ScaleType) =
                when (type) {
                    ImageView.ScaleType.CENTER -> "No scaling of the image, just centering it and keeping its aspect ratio. This means that if it is larger than the display, it will be cropped, if smaller it will padded with background color"
                    ImageView.ScaleType.CENTER_CROP -> "Centers the image in the display and keeps its aspect ratio. Image is scaled up or down to fit its shorter side to the display. The longer side of the image is cropped."
                    ImageView.ScaleType.CENTER_INSIDE -> "Centers the image inside the display, and keeps its aspect ratio. It fits its longer side and pads the shorter side with an equal amount of background colored pixels. If the image's long side is smaller than the display this does the same as CENTER"
                    ImageView.ScaleType.FIT_CENTER -> "Behaves like CENTER_INSIDE, expect for the case when the image's longer side is smaller than the display, when it will scale up the image in order to fit its longer side to the display"
                    ImageView.ScaleType.FIT_START -> "Behaves like FIT_CENTER, but does not center the image. Instead its top left corner is aligned with the display's top left corner"
                    ImageView.ScaleType.FIT_END -> "Behaves like FIT_CENTER, but does not center the image. Instead its bottom right corner is aligned with the display's bottom right corner"
                    ImageView.ScaleType.FIT_XY -> "The only one that unlocks aspect ratio and will fit the image to the size of the display, which may cause some distortion, so be careful with this one"
                    ImageView.ScaleType.MATRIX -> "If none of the other 7 works for you, you can always use this one and provide your own scaling by assigning the output of a Matrix class transformation (Rotate, Scale, Skew, etc.) using a .setImageMatrix() method call."
                }
    }

    class ScaleTypeFragment(layoutRes: Int) : BasePagerFragment(layoutRes) {
        companion object {
            fun newInstance(resId: Int) =
                    ScaleTypeFragment(R.layout.fragment_image_scale_type).apply {
                        arguments = Bundle().apply {
                            putInt(Constants.TAG_DATA, resId)
                        }
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
                val scaleTypeAdapter = ScaleTypeAdapter(scaleTypes, getInt(Constants.TAG_DATA))

                recyclerview.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
                recyclerview.adapter = scaleTypeAdapter

                scaleTypeAdapter.itemClicks()
                        .switchMap {
                            CommonDialog(context as Context)
                                    .withTitle(scaleTypes[it].name)
                                    .withContent(getScaleTypeDesc(scaleTypes[it]))
                                    .withDialogType(CommonDialog.ButtonType.SINGLE)
                                    .clicks()
                        }
                        .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                        .subscribe { }
            }
        }

        class ScaleTypeAdapter(data: List<ImageView.ScaleType>, private val resId: Int) : RxBaseQuickAdapter<ImageView.ScaleType, BaseViewHolder>(R.layout.item_image_scale_type, data) {
            override fun convert(helper: BaseViewHolder, type: ImageView.ScaleType) {
                helper.getView<ImageView>(R.id.scale_type_imageview).run {
                    Picasso.get().load(resId).into(this)
                    scaleType = type
                }

                helper.setText(R.id.textview, type.name)
            }
        }
    }
}