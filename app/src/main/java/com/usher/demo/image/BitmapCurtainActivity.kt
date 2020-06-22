package com.usher.demo.image

import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bitmap_curtain.*

class BitmapCurtainActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_curtain)
        initView()
    }

    private fun initView() {
        debug_imageview.clicks()
                .compose(RxUtil.singleClick())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    bitmap_curtain_layout1.debug = bitmap_curtain_layout1.debug.not()
                    bitmap_curtain_layout2.debug = bitmap_curtain_layout2.debug.not()
                }

        open_imageview.clicks()
                .compose(RxUtil.singleClick())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    bitmap_curtain_layout1.open()
                    bitmap_curtain_layout2.open()
                }

        close_imageview.clicks()
                .compose(RxUtil.singleClick())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    bitmap_curtain_layout1.close()
                    bitmap_curtain_layout2.close()
                }
    }
}