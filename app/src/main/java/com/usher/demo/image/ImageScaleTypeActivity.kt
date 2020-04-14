package com.usher.demo.image

import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.fragment.BasePagerFragment
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.activity_image_scale_type.*
import kotlinx.android.synthetic.main.fragment_image_scale_type.*

class ImageScaleTypeActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scale_type)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.demo_hardworking, R.drawable.demo_hardworking)

        viewpager.adapter = ScaleTypeFragmentAdapter(supportFragmentManager, resIds)
    }

    class ScaleTypeFragmentAdapter(fm: FragmentManager, private val mResIds: List<Int>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = ScaleTypeFragment.newInstance(mResIds[position])

        override fun getCount(): Int = mResIds.size
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
                    ImageView.ScaleType.CENTER_INSIDE,
                    ImageView.ScaleType.CENTER_INSIDE,
                    ImageView.ScaleType.CENTER_INSIDE,
                    ImageView.ScaleType.CENTER_INSIDE
            )

            arguments?.run {
                recyclerview.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
                recyclerview.adapter = ScaleTypeAdapter(scaleTypes, getInt(Constants.TAG_DATA))
            }
        }

        class ScaleTypeAdapter(data: List<ImageView.ScaleType>, private val resId: Int) : RxBaseQuickAdapter<ImageView.ScaleType, BaseViewHolder>(R.layout.item_image_scale_type, data) {
            override fun convert(helper: BaseViewHolder, scaleType: ImageView.ScaleType) {
                Picasso.get().load(resId).into(helper.getView<ImageView>(R.id.scale_type_imageview))
            }
        }
    }
}