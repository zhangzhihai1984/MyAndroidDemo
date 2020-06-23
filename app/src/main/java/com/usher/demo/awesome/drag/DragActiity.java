package com.usher.demo.awesome.drag;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.jakewharton.rxbinding4.material.RxAppBarLayout;
import com.twigcodes.ui.util.RxUtil;
import com.usher.demo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DragActiity extends AppCompatActivity {
    @BindView(R.id.appbarlayout)
    AppBarLayout mAppBarLayout;

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
        SceneTouchCallback callback = new SceneTouchCallback();

        ActionAdapter mAdapter = new ActionAdapter(this, mData, callback);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        RxAppBarLayout.offsetChanges(mAppBarLayout)
                .to(RxUtil.autoDispose(this))
                .subscribe(offset -> {
//                    Log.i("zzh", "offset: " + offset);
                    callback.setItemViewSwipeEnabled(offset <= -525 || offset >= 0);
                    callback.setItemViewDragEnabled(offset <= -525 || offset >= 0);
                });

        mData.add(new ActionInfo("智能插座"));
        mData.add(new ActionInfo("空调"));
        mData.add(new ActionInfo(10));
        mData.add(new ActionInfo("空气盒子"));
        mData.add(new ActionInfo("智能插座"));
        mData.add(new ActionInfo("空调"));
        mData.add(new ActionInfo(10));
        mData.add(new ActionInfo("空气盒子"));

        mAdapter.notifyDataSetChanged();

    }
}