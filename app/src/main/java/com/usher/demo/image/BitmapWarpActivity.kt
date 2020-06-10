package com.usher.demo.image

import android.graphics.Color
import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bitmap_warp.*

class BitmapWarpActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_warp)
        initView()
    }

    private fun initView() {
        color_picker_view.colorPicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { color -> bitmap_warp_view.colorVertex(color) }

        shuffle_imageview.clicks()
                .compose(RxUtil.singleClick())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    val colors = (1..bitmap_warp_view.vertexCount).map { Color.argb(255, (0..255).random(), (0..255).random(), (0..255).random()) }.toIntArray()
                    bitmap_warp_view.colorVertex(colors)
                }

        clear_imageview.clicks()
                .compose(RxUtil.singleClick())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { bitmap_warp_view.colorVertex(null) }

        debug_imageview.clicks()
                .compose(RxUtil.singleClick())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { bitmap_warp_view.debug = bitmap_warp_view.debug.not() }
    }
}