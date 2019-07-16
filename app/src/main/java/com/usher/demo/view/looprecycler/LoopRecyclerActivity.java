package com.usher.demo.view.looprecycler;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.twigcodes.ui.layoutmanager.LoopLayoutManager;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoopRecyclerActivity extends BaseActivity {
    @BindView(R.id.vertical_recyclerview)
    RecyclerView mVerticalRecyclerView;

    @BindView(R.id.horizontal_recyclerview)
    RecyclerView mHorizontalRecyclerView;

    private final List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loop_recycler);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mData.add(String.valueOf(i));
        }
    }

    private void initView() {
        LoopLayoutManager manager = new LoopLayoutManager();

        mVerticalRecyclerView.setLayoutManager(manager);
        mVerticalRecyclerView.setAdapter(new VerticalAdapter(mData));
        mVerticalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = manager.adjustPosition();
                    Log.i("zzh", "idle " + position);
                }
            }
        });

        LoopLayoutManager manager2 = new LoopLayoutManager(LoopLayoutManager.Orientaion.HORIZONTAL);
        mHorizontalRecyclerView.setLayoutManager(manager2);
        mHorizontalRecyclerView.setAdapter(new HorizontalAdapter(mData));
        mHorizontalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = manager2.adjustPosition();
                    Log.i("zzh", "idle " + position);
                }
            }
        });

    }

    class VerticalAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        VerticalAdapter(@Nullable List<String> data) {
            super(R.layout.item_loop_recycler_vertical, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.name_textview, item);
        }
    }

    class HorizontalAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        HorizontalAdapter(@Nullable List<String> data) {
            super(R.layout.item_loop_recycler_horizontal, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.name_textview, item);
        }
    }
}
