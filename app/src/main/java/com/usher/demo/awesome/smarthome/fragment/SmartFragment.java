package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.jakewharton.rxbinding3.material.RxAppBarLayout;
import com.jakewharton.rxbinding3.viewpager.RxViewPager;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.SmartFragmentAdapter;
import com.usher.demo.utils.RxUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartFragment extends BaseNavigationFragment {
    @BindView(R.id.appbarlayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.background_imageview)
    ImageView mBackgroundImageView;

    @BindView(R.id.smarttablayout)
    SmartTabLayout mSmartTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    public SmartFragment() {

    }

    public static SmartFragment newInstance() {
        Bundle args = new Bundle();

        SmartFragment fragment = new SmartFragment();
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
        View fragmentView = inflater.inflate(R.layout.fragment_smarthome_smart, container, false);
        ButterKnife.bind(this, fragmentView);
        setStatusBarTheme(Theme.DARK);
        initView();

        return fragmentView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setStatusBarTheme(getTheme());
        }
    }

    private void initView() {
        int statusBarHeight = 72;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        int appbarHeight = getResources().getDimensionPixelSize(R.dimen.smart_appbar_height);
        int toolbarHeight = getResources().getDimensionPixelSize(R.dimen.smart_toolbar_height);
        float threshold = appbarHeight - toolbarHeight * 2 - statusBarHeight;

        RxAppBarLayout.offsetChanges(mAppBarLayout)
                .as(RxUtil.autoDispose(requireActivity()))
                .subscribe(offset -> {
                    mBackgroundImageView.setImageAlpha((int) ((threshold - Math.min(threshold, Math.abs(offset))) / threshold * 255));
                    setStatusBarTheme(Math.abs(offset) > threshold ? Theme.LIGHT : Theme.DARK);
                });

        mViewPager.setAdapter(new SmartFragmentAdapter(requireFragmentManager()));
        mSmartTabLayout.setViewPager(mViewPager);

        RxViewPager.pageSelections(mViewPager)
                .as(RxUtil.autoDispose(requireActivity()))
                .subscribe(position -> {
                    for (int i = 0; i < 3; i++) {
                        ((TextView) mSmartTabLayout.getTabAt(i)).setTextSize(i == position ? 20 : 14);
                    }
                });
    }
}