package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;

import com.twigcodes.ui.pager.LoopViewPager;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.HomeBannerAdapter;

import androidx.annotation.Nullable;
import butterknife.BindView;

public class HomeFragment extends BaseNavigationFragment {
    @BindView(R.id.banner_viewpager)
    LoopViewPager mBannerViewPager;

    private HomeBannerAdapter mBannerAdapter;

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
        mBannerAdapter = new HomeBannerAdapter(requireFragmentManager());
        mBannerViewPager.setAdapter(mBannerAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setStatusBarTheme(Theme.LIGHT);
        }
    }
}