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
        val resIds = listOf(R.drawable.demo_hardworking, R.drawable.duggee1)

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
            val transformationPairs = listOf(
                    object : Transformation {
                        override fun key(): String = "NoTransformation"

                        override fun transform(source: Bitmap): Bitmap = source
                    } to "NULL",
                    ImageUtil.getBlurTransformation(requireContext()) to "BLUR",
                    ImageUtil.getSquareTransformation() to "SQUARE",
                    ImageUtil.getCircleTransformation() to "CIRCLE",
                    ImageUtil.getRoundTransformation(200f) to "ROUND"
            )

            arguments?.run {
                recyclerview.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
                recyclerview.adapter = PicassoTransformationAdapter(transformationPairs, getInt(Constants.TAG_DATA))
            }
        }
    }

    private class PicassoTransformationAdapter(data: List<Pair<Transformation, String>>, private val resId: Int) : RxBaseQuickAdapter<Pair<Transformation, String>, BaseViewHolder>(R.layout.item_picasso_transformation, data) {
        override fun convert(helper: BaseViewHolder, transformationPair: Pair<Transformation, String>) {
            Picasso.get().load(resId).transform(transformationPair.first).into(helper.getView<ImageView>(R.id.picasso_imageview))

            helper.setText(R.id.textview, transformationPair.second)
        }
    }
}