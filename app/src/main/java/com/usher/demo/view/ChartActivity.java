package com.usher.demo.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.twigcodes.ui.ChartView;
import com.twigcodes.ui.util.RxUtil;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;

public class ChartActivity extends BaseActivity {
    @BindView(R.id.chart_view)
    ChartView mChartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        List<Float> mTempData = new ArrayList<>();
        mTempData.add(25.5f);
        mTempData.add(23.2f);
        mTempData.add(22.0f);
        mTempData.add(20.0f);
        mTempData.add(25.0f);
        mTempData.add(25.0f);
        mTempData.add(25.0f);
        mTempData.add(20.0f);
        mTempData.add(18.0f);
        mTempData.add(30.0f);
        mTempData.add(28.0f);
        mTempData.add(27.0f);
        mTempData.add(26.0f);

        List<Float> mHumidityData = new ArrayList<>();
        mHumidityData.add(50.0f);
        mHumidityData.add(55.0f);
        mHumidityData.add(60.0f);
        mHumidityData.add(60.0f);
        mHumidityData.add(60.0f);
        mHumidityData.add(55.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(40.0f);
        mHumidityData.add(30.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(50.0f);
        mHumidityData.add(55.0f);
        mHumidityData.add(60.0f);
        mHumidityData.add(60.0f);
        mHumidityData.add(60.0f);
        mHumidityData.add(55.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(40.0f);
        mHumidityData.add(30.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);

        mHumidityData.add(30.0f);
        mHumidityData.add(30.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
        mHumidityData.add(45.0f);
//
//        mHumidityData.add(30.0f);
//        mHumidityData.add(30.0f);
//        mHumidityData.add(45.0f);
//        mHumidityData.add(45.0f);
//        mHumidityData.add(45.0f);
//        mHumidityData.add(45.0f);


        mChartView.setConfig(ChartView.XType.MONTH, ChartView.YType.HUMIDITY, true);
//        mChartView.setData(mTempData);

        Observable.timer(50, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe(v -> mChartView.setData(mHumidityData));

//        Observable.timer(3000, TimeUnit.MILLISECONDS)
//                .compose(RxUtil.getSchedulerComposer())
//                .as(RxUtil.autoDispose(this))
//                .subscribe(v -> mChartView.setConfig(ChartView.YType.TEMPERATURE, true));
//
//        Observable.timer(6000, TimeUnit.MILLISECONDS)
//                .compose(RxUtil.getSchedulerComposer())
//                .as(RxUtil.autoDispose(this))
//                .subscribe(v -> mChartView.setData(mTempData));
    }
}
