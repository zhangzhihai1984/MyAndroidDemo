package com.usher.demo.rx

import android.animation.ValueAnimator
import android.view.KeyEvent
import android.view.animation.OvershootInterpolator
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_rx_exit.*

class RxExitActivity : BaseActivity(R.layout.activity_rx_exit) {
    companion object {
        private const val EXIT_DURATION = 500
    }

    private val mBackSubject = PublishSubject.create<Unit>()

    override fun initView() {
        ValueAnimator.ofFloat(1f, 1.1f, 1f).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                exit_imageview.scaleX = animatedValue as Float
                exit_imageview.scaleY = animatedValue as Float
            }
        }.start()

        exit_imageview.clicks()
                .map { mBackSubject.onNext(Unit) }
                .to(RxUtil.autoDispose(this))
                .subscribe { }

        val interval = mBackSubject.timeInterval().map { it.time() }
        val exit = interval.skip(1).filter { it < EXIT_DURATION }

        interval.takeUntil(exit)
                .to(RxUtil.autoDispose(this))
                .subscribe { showToast("Click Once More to EXIT") }

        exit.to(RxUtil.autoDispose(this)).subscribe { finish() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mBackSubject.onNext(Unit)
            return false
        }

        return super.onKeyDown(keyCode, event)
    }
}