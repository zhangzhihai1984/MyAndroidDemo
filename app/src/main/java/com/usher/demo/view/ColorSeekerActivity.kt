package com.usher.demo.view

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_color_seeker.*

class ColorSeekerActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_seeker)
        initView()
    }

    private fun initView() {
        color_seeker_view.colorSeeks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { color ->
                    palette_imageview.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color_seeker_view.updateColor(ContextCompat.getColor(this, R.color.colorPrimary))
        }
    }
}