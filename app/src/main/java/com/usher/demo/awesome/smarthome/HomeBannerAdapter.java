package com.usher.demo.awesome.smarthome;

import com.twigcodes.ui.pager.LoopViewPager;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.fragment.HomeBannerFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeBannerAdapter extends FragmentPagerAdapter {
    private static final int[] RES_IDS = {R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4, R.mipmap.banner5};
    private List<Fragment> mFragmentList = new ArrayList<>();

    public HomeBannerAdapter(@NonNull FragmentManager fm) {
        super(fm);

        initData();
    }

    private void initData() {
        for (int resId : RES_IDS) {
            mFragmentList.add(HomeBannerFragment.newInstance(resId));
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        int pos = LoopViewPager.getMatchedPosition(position, getCount());
        return HomeBannerFragment.newInstance(RES_IDS[pos]);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
