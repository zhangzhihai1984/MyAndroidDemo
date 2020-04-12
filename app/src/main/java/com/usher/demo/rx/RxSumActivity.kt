package com.usher.demo.rx

import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_rx_sum.*

class RxSumActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_sum)
        initView()
    }

    private fun initView() {
        val param1 = param1_textview.clicks()
                .map { 1 }
                .scan { acc, curr -> acc + curr }
                .doOnNext { param1_textview.text = "$it" }

        val param2 = param2_textview.clicks()
                .map { 1 }
                .scan { t1, t2 -> t1 + t2 }
                .doOnNext { param2_textview.text = "$it" }

//        Observable.combineLatest<Int, Int, Int>(
//                param1,
//                param2,
//                BiFunction { t1, t2 -> t1 + t2 }
//        )
//                .`as`(RxUtil.autoDispose(this))
//                .subscribe { }


        Observables.combineLatest(param1, param2) { t1, t2 -> t1 + t2 }
                .`as`(RxUtil.autoDispose(this))
                .subscribe { sum_textview.text = "$it" }
    }
}