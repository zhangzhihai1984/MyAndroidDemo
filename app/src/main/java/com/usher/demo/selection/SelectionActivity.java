package com.usher.demo.selection;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.usher.demo.R;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {
    private final int SELECTION_SIZE = 8;
    private final List<SelectionInfo> mSelectionInfoList = new ArrayList<>();
    private final List<SelectionInfo> mPresentationList = new ArrayList<>();
    private Context mContext;

    private SelectionAdapter mAdapter;
    private PresentationAdapter mAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        mContext = this;

        initData();
        initView();
    }

    private void initData() {
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.SELECTED));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.SELECTED));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.DISABLED));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.DEFAULT));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.DEFAULT));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.DEFAULT));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.DISABLED));
        mSelectionInfoList.add(new SelectionInfo(SelectionInfo.Status.DISABLED));

        mAdapter = new SelectionAdapter(mContext, mSelectionInfoList);
        mAdapter.setOnSelectedListener(this::updatePresentationList);

        mAdapter2 = new PresentationAdapter(mContext, mPresentationList);
    }

    private void initView() {
        final RecyclerView mRecyclerView = findViewById(R.id.recyclerview);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, SELECTION_SIZE, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
                params.height = mRecyclerView.getWidth() / SELECTION_SIZE - getResources().getDimensionPixelSize(R.dimen.selection_item_margin) * 2;

                mRecyclerView.setLayoutParams(params);
                mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final RecyclerView mRecyclerView2 = findViewById(R.id.recyclerview2);

        GridLayoutManager mGridLayoutManager2 = new GridLayoutManager(mContext, SELECTION_SIZE, LinearLayoutManager.VERTICAL, false);
        mGridLayoutManager2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mPresentationList.get(position).getSpanSize();
            }
        });

        mRecyclerView2.setLayoutManager(mGridLayoutManager2);
        mRecyclerView2.setAdapter(mAdapter2);
        mRecyclerView2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = mRecyclerView2.getLayoutParams();
                params.height = mRecyclerView2.getWidth() / SELECTION_SIZE - getResources().getDimensionPixelSize(R.dimen.selection_item_margin) * 2;

                mRecyclerView2.setLayoutParams(params);
                mRecyclerView2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        updatePresentationList();
    }

    private void updatePresentationList() {
        mPresentationList.clear();

        int spanSize;
        int j;
        for (int i = 0; i < mSelectionInfoList.size(); i += spanSize) {
            spanSize = 1;
            SelectionInfo.Status status = mSelectionInfoList.get(i).getStatus();

            for (j = i + 1; j < mSelectionInfoList.size(); j++) {
                if (mSelectionInfoList.get(j).getStatus() == status) {
                    spanSize += 1;
                } else {
                    mPresentationList.add(new SelectionInfo(status, spanSize));
                    break;
                }
            }

            if (j == mSelectionInfoList.size()) {
                mPresentationList.add(new SelectionInfo(status, spanSize));
            }
        }

        mAdapter2.notifyDataSetChanged();
    }
}
