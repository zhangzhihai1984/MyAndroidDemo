package com.usher.demo.image

import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.ImageUtil
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_graffiti.*

class GraffitiActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graffiti)
        initView()
    }

    private fun initView() {
        val drawables = listOf(
                resources.getDrawable(R.drawable.demo_tree, null),
                resources.getDrawable(R.drawable.demo_hardworking, null),
                resources.getDrawable(R.drawable.demo_arale, null),
                Color.BLACK.toDrawable(),
                getColor(R.color.colorPrimary).toDrawable(),
                null
        )

        val stroke1 = stroke_imageview1.clicks().map { 15f }
        val stroke2 = stroke_imageview2.clicks().map { 30f }
        val stroke3 = stroke_imageview3.clicks().map { 45f }

        Observable.merge(stroke1, stroke2, stroke3)
                .to(RxUtil.autoDispose(this))
                .subscribe { graffiti_view.strokeWidth = it }

        color_picker_view.colorPicks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { color -> graffiti_view.strokeColor = color }

        shuffle_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { graffiti_view.background = drawables[drawables.indices.random()] }

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