package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.SmartSceneAdapter;
import com.usher.demo.awesome.smarthome.fragment.base.BaseLazyPagerFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SmartSceneFragment extends BaseLazyPagerFragment {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    public SmartSceneFragment() {
        super();
    }

    public static SmartSceneFragment newInstance() {
        Bundle args = new Bundle();
        SmartSceneFragment fragment = new SmartSceneFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_smarthome_scene;
    }

    @Override
    public void init() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(new SmartSceneAdapter(requireContext()));
    }
}