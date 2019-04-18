package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;

import com.twigcodes.ui.pager.LoopViewPager;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.HomeBannerAdapter;
import com.usher.demo.awesome.smarthome.entities.ADInfo;

import java.util.ArrayList;
import java.util.List;

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
        List<ADInfo> bannerData = new ArrayList<>();
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/790a4eed54594b3e89b0b8138420b4d8.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/07b537a651ba45fca9d365b54c20b849.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/60151954aacf42f794bc7bac5eb976cc.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/7ae668c57c1e40169e45491404466a4b.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/290e9a8341054ef2aeda4f2a429b23ae.jpg", "http://www.baidu.com"));

        mBannerAdapter = new HomeBannerAdapter(requireFragmentManager(), bannerData);
        mBannerViewPager.setAdapter(mBannerAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setStatusBarTheme(Theme.LIGHT);
        }
    }
}