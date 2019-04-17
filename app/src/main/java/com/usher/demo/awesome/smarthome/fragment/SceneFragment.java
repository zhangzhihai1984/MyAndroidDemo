package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;
import com.usher.demo.awesome.drag.ChannelAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SceneFragment extends BasePagerFragment {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    public SceneFragment() {
        super();
    }

    public static SceneFragment newInstance() {
        Bundle args = new Bundle();
        SceneFragment fragment = new SceneFragment();
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(new ChannelAdapter(requireContext(), new ArrayList<>(), new ArrayList<>()));
    }
}