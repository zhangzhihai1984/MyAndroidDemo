package com.usher.demo.view

import android.graphics.PorterDuff
import android.os.Bundle
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.ImageUtil
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.withLatestFrom
import kotlinx.android.synthetic.main.activity_graffiti.*

class GraffitiActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graffiti)
        initView()
    }

    private fun initView() {
        val strokeImageViews = listOf(stroke_imageview1, stroke_imageview2, stroke_imageview3)
        val strokeClicks = Observable.merge(strokeImageViews.mapIndexed { i, imageView -> imageView.clicks().map { i }.share() })
                .startWith(Observable.just(1).compose(RxUtil.getSchedulerComposer()))

        val colorPicks = color_picker_view.colorSeeks()
                .startWith(Observable.just(getColor(R.color.text_primary)).compose(RxUtil.getSchedulerComposer()))

        colorPicks.withLatestFrom(strokeClicks) { color, i ->
            graffiti_view.strokeColor = color
            strokeImageViews[i].setColorFilter(color, PorterDuff.Mode.SRC)
        }
                .to(RxUtil.autoDispose(this))
                .subscribe {}

        strokeClicks.withLatestFrom(colorPicks) { current, color ->
            graffiti_view.strokeWidth = (current + 1) * 15f
            strokeImageViews.forEachIndexed { i, imageView ->
                imageView.setColorFilter(color, if (i == current) PorterDuff.Mode.SRC else PorterDuff.Mode.DST)
            }
        }
                .to(RxUtil.autoDispose(this))
                .subscribe {}

        undo_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { graffiti_view.undo() }

        clear_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { graffiti_view.clear() }

        done_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { snapshot_imageview.setImageBitmap(ImageUtil.getViewBitmap(graffiti_view)) }
    }
}