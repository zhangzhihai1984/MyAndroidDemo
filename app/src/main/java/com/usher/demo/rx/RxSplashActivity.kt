package com.usher.demo.rx

import android.animation.ValueAnimator
import android.view.KeyEvent
import android.view.animation.OvershootInterpolator
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_rx_splash.*
import java.util.concurrent.TimeUnit

class RxSplashActivity : BaseActivity(R.layout.activity_rx_splash, Theme.LIGHT_AUTO) {
    companion object {
        private const val COUNTDOWN_SECONDS = 10L
    }

    override fun initView() {
        ValueAnimator.ofFloat(1f, 1.1f, 1f).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                countdown_textview.scaleX = animatedValue as Float
                countdown_textview.scaleY = animatedValue as Float
            }
        }.start()

        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { ad_imageview.setImageResource(R.drawable.common_gradient_primary_radial_background) }

        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .take(COUNTDOWN_SECONDS + 1)
                .map { COUNTDOWN_SECONDS - it }
                .takeUntil { it <= 0 }
                .takeUntil(countdown_textview.clicks().take(1))
                .to(RxUtil.autoDispose(this))
                .subscribe({
                    countdown_textview.text = "$it"
                    progressbar.progress = it.toInt()
                }, {}, {
                    finish()
                })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            return true
        return super.onKeyDown(keyCode, event)
    }
}