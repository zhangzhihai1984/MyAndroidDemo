package com.usher.demo.awesome.smarthome;

import android.os.Bundle;

import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;

public class SmartHomeActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smarthome);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

    }
}
