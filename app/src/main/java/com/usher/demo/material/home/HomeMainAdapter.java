package com.usher.demo.material.home;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeMainAdapter extends FragmentPagerAdapter {
    private static final String[] TAB_NAMES = {"推荐", "音乐", "节目", "儿童", "我的"};

    public HomeMainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HomeMainFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return TAB_NAMES.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_NAMES[position];
    }
}
