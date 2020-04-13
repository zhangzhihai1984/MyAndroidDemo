package com.usher.demo.other

import android.os.Bundle
import android.widget.ScrollView
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.LogUtil
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_log.*
import java.util.concurrent.TimeUnit

class LogActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        initView()
    }

    private fun initView() {
        LogUtil.readLog()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    log_textview.append(it)
                    log_textview.append("\n")
                    log_scrollview.fullScroll(ScrollView.FOCUS_DOWN)
                }

        Observable.interval(2 * 1000, TimeUnit.MILLISECONDS)
                .take(10)
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { LogUtil.log("log write $it") }
    }
}