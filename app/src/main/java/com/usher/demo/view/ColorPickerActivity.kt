package com.usher.demo.view

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.core.animation.doOnEnd
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import kotlinx.android.synthetic.main.activity_color_picker.*

class ColorPickerActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)
        initView()
    }

    private fun initView() {
//        color_picker_view.updateColor(Color.BLUE)
//
        color_picker_view.colorPicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { color ->
                    ValueAnimator.ofFloat(0f, -20f, 0f, 20f, 0f).apply {
                        duration = 200
                        repeatCount = 2
                        addUpdateListener {
                            palette_cardview.rotation = animatedValue as Float
                        }
                        doOnEnd { palette_imageview.imageTintList = ColorStateList.valueOf(color) }
                    }.start()
                }
    }
}