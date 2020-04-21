package com.usher.demo.image

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jakewharton.rxbinding3.viewpager.pageSelections
import com.squareup.picasso.Picasso
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.PicassoUtil
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.activity_image_blur.*
import kotlinx.android.synthetic.main.fragment_image_blur.*

class ImageBlurActivity : BaseActivity(Theme.LIGHT_AUTO) {
    companion object {
        private val RES_IDS = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_blur)
        initView()
    }

    private fun initView() {
        viewpager.adapter = BlurFragmentAdapter(supportFragmentManager, RES_IDS)
        viewpager.pageSelections()
                .`as`(RxUtil.autoDispose(this))
                .subscribe { position ->
                    Picasso.get().load(RES_IDS[position]).transform(PicassoUtil.getBlurTransformation(this)).into(blur_imageview)
                }

        indicatorview.setViewPager(viewpager)
    }

    class BlurFragmentAdapter(fm: FragmentManager, private var mData: List<Int>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = BlurFragment.newInstance(mData[position])

        override fun getCount(): Int = mData.size
    }

    class BlurFragment(layoutRes: Int) : BasePagerFragment(layoutRes) {
        companion object {
            fun newInstance(resId: Int) =
                    BlurFragment(R.layout.fragment_image_blur).apply {
                        arguments = Bundle().apply {
                            putInt(Constants.TAG_DATA, resId)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                Picasso.get().load(getInt(Constants.TAG_DATA)).into(pager_imageview)
            }
        }
    }
}