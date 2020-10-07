package com.usher.demo.view

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.animation.doOnEnd
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_color_picker.*

class ColorPickerActivity : BaseActivity(R.layout.activity_color_picker, Theme.LIGHT_AUTO) {

    override fun initView() {
        color_picker_view.colorPicks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { color ->
                    ValueAnimator.ofFloat(0f, -20f, 0f, 20f, 0f).apply {
                        duration = 200
                        repeatCount = 2
                        addUpdateListener {
                            palette_cardview.rotation = animatedValue as Float
                        }
                        doOnEnd {
//                            palette_imageview.imageTintList = ColorStateList.valueOf(color)
                            palette_imageview.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                        }
                    }.start()
                }
    }
}