package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private View mFragmentView;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
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
        mFragmentView = inflater.inflate(R.layout.fragment_smarthome_home, container, false);
        initView();

        return mFragmentView;
    }

    private void initView() {

    }

    public static class SmartFragment extends Fragment {
    }
}
