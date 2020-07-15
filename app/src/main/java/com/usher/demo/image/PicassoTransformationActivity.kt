package com.usher.demo.image

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.ImageUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.activity_picasso_transformation.*
import kotlinx.android.synthetic.main.fragment_picasso_transformation.*

class PicassoTransformationActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picasso_transformation)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.duggee1, R.drawable.demo_mall)

        viewpager.adapter = PicassoTransformationFragmentAdapter(supportFragmentManager, resIds)
        indicatorview.setViewPager(viewpager)
    }

    private class PicassoTransformationFragmentAdapter(fm: FragmentManager, private val mResIds: List<Int>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = PicassoTransformationFragment.newInstance(mResIds[position])

        override fun getCount(): Int = mResIds.size
    }

    class PicassoTransformationFragment(layoutRes: Int) : BasePagerFragment(layoutRes) {
        companion object {
            fun newInstance(resId: Int) =
                    PicassoTransformationFragment(R.layout.fragment_picasso_transformation).apply {
                        arguments = Bundle().apply {
                            putInt(Constants.TAG_DATA, resId)
                        }
                    }
        }

        override fun init() {
            val transformations = listOf(
                    null,
                    ImageUtil.getBlurTransformation(requireContext()),
                    ImageUtil.getSquareTransformation(),
                    ImageUtil.getCircleTransformation(),
                    ImageUtil.getRoundTransformation(200f)
            )

            arguments?.run {
                recyclerview.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
                recyclerview.adapter = PicassoTransformationAdapter(transformations, getInt(Constants.TAG_DATA))
            }
        }
    }

    private class PicassoTransformationAdapter(data: List<Transformation?>, private val resId: Int) : RxBaseQuickAdapter<Transformation?, BaseViewHolder>(R.layout.item_picasso_transformation, data) {
        override fun convert(helper: BaseViewHolder, transformation: Transformation?) {
            Picasso.get().load(resId)
                    .transform(transformation ?: object : Transformation {
                        override fun key(): String = "NoBlurTransformation"

                        override fun transform(source: Bitmap): Bitmap = source
                    }).into(helper.getView<ImageView>(R.id.picasso_imageview))
        }
    }
}