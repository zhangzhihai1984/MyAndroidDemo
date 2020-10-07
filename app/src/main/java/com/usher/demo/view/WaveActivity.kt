package com.usher.demo.view

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wave.*

class WaveActivity : BaseActivity(R.layout.activity_wave, Theme.LIGHT_AUTO) {

    override fun initView() {
//        Observable.interval(0, 200, TimeUnit.MILLISECONDS)
//                .take(100 + 1)
//                .map { it.toInt() }
//                .compose(RxUtil.getSchedulerComposer())
//                .to(RxUtil.autoDispose(this))
//                .subscribe {
//                    wave_view.progress = it
//                    progressbar.progress = it
//                }

        ValueAnimator.ofInt(0, 100).apply {
            duration = 10_000
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                wave_view.progress = animatedValue as Int
                progressbar.progress = animatedValue as Int
            }
        }.start()
    }
}