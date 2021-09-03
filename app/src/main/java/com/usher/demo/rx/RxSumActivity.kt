package com.usher.demo.rx

import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_rx_sum.*

class RxSumActivity : BaseActivity(R.layout.activity_rx_sum, Theme.LIGHT_AUTO) {

    override fun initView() {
        val param1 = param1_textview.clicks()
                .map { 1 }
                .scan { acc, value -> acc + value }
                .doOnNext { param1_textview.text = it.toString() }

        val param2 = param2_textview.clicks()
                .map { 1 }
                .scan { acc, value -> acc + value }
                .doOnNext { param2_textview.text = it.toString() }

        Observable.combineLatest(param1, param2) { t1, t2 -> t1 + t2 }
                .to(RxUtil.autoDispose(this))
                .subscribe { sum_textview.text = it.toString() }
    }
}