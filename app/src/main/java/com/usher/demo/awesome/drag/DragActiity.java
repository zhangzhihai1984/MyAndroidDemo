package com.usher.demo.awesome.drag;

import android.os.Bundle;

import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

import butterknife.ButterKnife;

public class DragActiity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

    }
}
