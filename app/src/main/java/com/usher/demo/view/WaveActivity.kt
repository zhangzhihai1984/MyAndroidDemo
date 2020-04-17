package com.usher.demo.view

import android.os.Bundle
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_wave.*
import java.util.concurrent.TimeUnit

class WaveActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave)
        initView()
    }

    private fun initView() {
        Observable.interval(200, TimeUnit.MILLISECONDS)
                .take(100 + 1)
                .startWith(0)
                .map { it.toInt() }
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    wave_view.progress = it
                    progressbar.progress = it
                }
    }
}