package com.usher.demo.main;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

public class DemoListActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_list);

        initView();
    }

    private void initView() {
        String tag = getIntent().getStringExtra(DemoConfig.TAG_KEY);
        if (TextUtils.isEmpty(tag))
            tag = "main";

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new DemoListAdapter(this, DemoConfig.getDemoConfig(tag)));
    }
}