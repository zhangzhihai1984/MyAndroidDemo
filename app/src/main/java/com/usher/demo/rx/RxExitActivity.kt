package com.usher.demo.rx

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.Animation
import com.jakewharton.rxbinding3.view.clicks
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_rx_exit.*

class RxExitActivity : BaseActivity(Theme.LIGHT) {
    companion object {
        private const val EXIT_DURATION = 500
    }

    private val mBackSubject = PublishSubject.create<Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_exit)
        initView()
    }

    private fun initView() {
        ValueAnimator.ofFloat(1f, 1.1f, 1f).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            addUpdateListener {
                exit_imageview.scaleX = it.animatedValue as Float
                exit_imageview.scaleY = it.animatedValue as Float
            }
        }.start()

        exit_imageview.clicks()
                .map { mBackSubject.onNext(Unit) }
                .`as`(RxUtil.autoDispose(this))
                .subscribe { }

        val interval = mBackSubject.timeInterval().map { it.time() }
        val exit = interval.skip(1).filter { it < EXIT_DURATION }

        interval.takeUntil(exit)
                .`as`(RxUtil.autoDispose(this))
                .subscribe { showToast("Click Once More to EXIT") }

        exit.`as`(RxUtil.autoDispose(this)).subscribe { finish() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mBackSubject.onNext(Unit)
            return false
        }

        return super.onKeyDown(keyCode, event)
    }
}