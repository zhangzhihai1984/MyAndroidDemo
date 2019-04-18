package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;

import com.usher.demo.R;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class HomeFragment extends BaseNavigationFragment {
    @BindView(R.id.banner_viewpager)
    ViewPager mBannerViewPager;

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
        setStatusBarTheme(Theme.LIGHT);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_smarthome_home;
    }

    @Override
    public void init() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setStatusBarTheme(Theme.LIGHT);
        }
    }
}