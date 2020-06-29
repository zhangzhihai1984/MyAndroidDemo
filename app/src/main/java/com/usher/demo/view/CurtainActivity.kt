package com.usher.demo.view

import android.os.Bundle
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_curtain.*
import kotlinx.android.synthetic.main.curtain_content_layout.*
import kotlinx.android.synthetic.main.curtain_cover_layout.*

class CurtainActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curtain)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.demo_slamdunk, R.drawable.demo_mall, R.drawable.demo_tree, R.drawable.demo_hardworking, R.drawable.demo_arale)

        curtain_view.percentChanges()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { percent ->
                    mask_view.alpha = 1 - percent - 0.2f
                    percent_textview.text = "${(percent * 100).toInt()}"
                }

        shuffle_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    cover_imageview.setImageResource(resIds[(resIds.indices).random()])
                }

        debug_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    curtain_view.debug = curtain_view.debug.not()
                }

        open_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    curtain_view.open()
                }

        close_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    curtain_view.close()
                }
    }
}