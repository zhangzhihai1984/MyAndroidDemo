package com.usher.demo.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.twigcodes.ui.ChartView;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    }
}
