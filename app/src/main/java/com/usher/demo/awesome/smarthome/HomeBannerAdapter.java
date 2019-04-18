package com.usher.demo.awesome.smarthome;

import com.twigcodes.ui.pager.LoopViewPager;
import com.usher.demo.awesome.smarthome.entities.ADInfo;
import com.usher.demo.awesome.smarthome.fragment.HomeBannerFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeBannerAdapter extends FragmentPagerAdapter {
    private final List<ADInfo> mList;

    public HomeBannerAdapter(@NonNull FragmentManager fm, List<ADInfo> list) {
        super(fm);

        mList = list;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        int pos = LoopViewPager.getMatchedPosition(position, getCount());
        return HomeBannerFragment.newInstance(mList.get(pos));
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
