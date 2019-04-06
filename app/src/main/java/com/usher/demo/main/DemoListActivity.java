package com.usher.demo.main;

import android.os.Bundle;
import android.text.TextUtils;

import com.usher.demo.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DemoListActivity extends AppCompatActivity {
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
