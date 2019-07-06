package com.usher.demo.awesome.drag;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usher.demo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DragActiity extends AppCompatActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private final List<ActionInfo> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        ActionAdapter mAdapter = new ActionAdapter(this, mData);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mData.add(new ActionInfo("智能插座"));
        mData.add(new ActionInfo("空调"));
        mData.add(new ActionInfo(10));
        mData.add(new ActionInfo("空气盒子"));

        mAdapter.notifyDataSetChanged();
    }
}
