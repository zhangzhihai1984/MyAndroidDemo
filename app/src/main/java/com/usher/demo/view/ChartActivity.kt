package com.usher.demo.view

import com.twigcodes.ui.ChartView
import com.twigcodes.ui.util.RxUtil.autoDispose
import com.twigcodes.ui.util.RxUtil.getSchedulerComposer
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_chart.*
import java.util.concurrent.TimeUnit

class ChartActivity : BaseActivity(R.layout.activity_chart) {
    private val mTempData = listOf(25.5f, 23.2f, 22.0f, 20.0f, 25.0f, 25.0f, 25.0f, 20.0f, 18.0f, 30.0f, 28.0f, 27.0f, 26.0f)
    private val mHumidityData = listOf(50.0f, 60.0f, 60.0f, 60.0f, 55.0f, 45.0f, 45.0f, 40.0f, 30.0f, 45.0f, 45.0f, 50.0f, 55.0f, 60.0f, 60.0f, 60.0f, 55.0f, 45.0f, 45.0f, 40.0f, 30.0f, 45.0f, 45.0f, 30.0f, 30.0f, 45.0f, 45.0f, 45.0f, 45.0f, 45.0f)

    override fun initView() {
        chart_view.setConfig(ChartView.XType.MONTH, ChartView.YType.HUMIDITY, true)
        chart_view.data = mHumidityData

        Observable.timer(50, TimeUnit.MILLISECONDS)
                .compose(getSchedulerComposer())
                .to(autoDispose(this))
                .subscribe { chart_view.data = mHumidityData }
    }
}