package com.usher.demo.demonstration

import android.os.Bundle
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bitmap_mesh_curtain.*

class BitmapMeshCurtainActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_mesh_curtain)
        initView()
    }

    private fun initView() {
        debug_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { bitmap_curtain_view.debug = bitmap_curtain_view.debug.not() }
    }
}