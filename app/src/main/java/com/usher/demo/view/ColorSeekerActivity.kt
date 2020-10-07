package com.usher.demo.view

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.content.ContextCompat
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_color_seeker.*

class ColorSeekerActivity : BaseActivity(R.layout.activity_color_seeker, Theme.LIGHT_AUTO) {

    override fun initView() {
        color_seeker_view.colorSeeks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { color ->
                    palette_imageview.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                }

        color_seeker_view.updateColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }
}