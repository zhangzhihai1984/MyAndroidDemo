package com.usher.demo.decoration;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.usher.demo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecorationActivity extends AppCompatActivity {
    private final List<ItemInfo> mList = new ArrayList<>();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoration);

        mContext = this;

        initData();
        initView();
    }

    private void initData() {

        List<List<String>> lists = new ArrayList<>();
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list1)));
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list2)));
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list3)));
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list4)));
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list5)));
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list6)));
        lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list7)));

        for (int i = 0; i < lists.size(); i++) {
            for (String content : lists.get(i)) {
                mList.add(new ItemInfo(String.valueOf(i), "GROUP " + i, content));
            }
        }
    }

    private void initView() {
        DecorationAdapter mAdapter = new DecorationAdapter(mContext, mList);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new StickyItemDecoration(mContext, mList));
        mRecyclerView.setAdapter(mAdapter);
    }
}
