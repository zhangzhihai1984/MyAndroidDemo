package com.usher.demo.rx

import android.os.Bundle
import android.view.KeyEvent
import com.jakewharton.rxbinding3.view.clicks
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_rx_exit.*

class RxExitActivity : BaseActivity() {
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
        exit_button.clicks()
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