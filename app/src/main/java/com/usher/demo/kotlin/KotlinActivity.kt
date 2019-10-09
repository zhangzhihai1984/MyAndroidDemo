package com.usher.demo.kotlin

import android.os.Bundle
import android.util.Log
import com.uber.autodispose.ObservableSubscribeProxy
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class KotlinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        init()
    }

    private fun init() {
        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
//                .`as`<ObservableSubscribeProxy<Long>>(RxUtil.autoDispose(this))
                .`as`(RxUtil.autoDispose(this))
                .subscribe { v -> Log.i("zzh", "haha")
                }
    }
}