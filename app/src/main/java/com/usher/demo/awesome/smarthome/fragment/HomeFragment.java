package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.HomeBannerView;
import com.usher.demo.awesome.smarthome.ShortcutAdapter;
import com.usher.demo.awesome.smarthome.entities.ADInfo;
import com.usher.demo.awesome.smarthome.fragment.base.BaseNavigationFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragment extends BaseNavigationFragment {
    @BindView(R.id.home_banner_view)
    HomeBannerView mHomeBannerView;

    @BindView(R.id.shortcut_recyclerview)
    RecyclerView mShortcutRecyclerView;

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
        mShortcutRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 5, RecyclerView.VERTICAL, false));
        mShortcutRecyclerView.setAdapter(new ShortcutAdapter(requireContext()));

        getMockData();
    }

    private void getMockData() {
        List<ADInfo> bannerData = new ArrayList<>();
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/790a4eed54594b3e89b0b8138420b4d8.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/07b537a651ba45fca9d365b54c20b849.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/60151954aacf42f794bc7bac5eb976cc.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/7ae668c57c1e40169e45491404466a4b.jpg", "http://www.baidu.com"));
        bannerData.add(new ADInfo("http://sccloud.oss.cn-north-1.jcloudcs.com/cms/project/-1/lunbo/290e9a8341054ef2aeda4f2a429b23ae.jpg", "http://www.baidu.com"));

        mHomeBannerView.setData(bannerData);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setStatusBarTheme(Theme.LIGHT);
        }
    }
}