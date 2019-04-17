package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;

public class HomeFragment extends BaseNavigationFragment {

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
        View fragmentView = inflater.inflate(R.layout.fragment_smarthome_home, container, false);
        ButterKnife.bind(this, fragmentView);
        setStatusBarTheme(Theme.LIGHT);
        initView();

        return fragmentView;
    }

    private void initView() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setStatusBarTheme(Theme.LIGHT);
        }
    }
}