package com.usher.demo.demonstration

import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bitmap_mesh_ripple.*

class BitmapMeshRippleActivity : BaseActivity(R.layout.activity_bitmap_mesh_ripple, Theme.LIGHT_AUTO) {

    override fun initView() {
        debug_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { bitmap_ripple_view.debug = bitmap_ripple_view.debug.not() }
    }
}