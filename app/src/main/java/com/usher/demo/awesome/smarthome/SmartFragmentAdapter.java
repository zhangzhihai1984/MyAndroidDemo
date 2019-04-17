package com.usher.demo.awesome.smarthome;

import com.usher.demo.awesome.smarthome.fragment.DeviceFragment;
import com.usher.demo.awesome.smarthome.fragment.OtherFragment;
import com.usher.demo.awesome.smarthome.fragment.SmartSceneFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SmartFragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTitleList = new ArrayList<>();

    public SmartFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);

        initData();
    }

    private void initData() {
        mFragmentList.add(SmartSceneFragment.newInstance());
        mFragmentList.add(DeviceFragment.newInstance());
        mFragmentList.add(OtherFragment.newInstance());

        mTitleList.add("场景管理");
        mTitleList.add("设备管理");
        mTitleList.add("其他设备");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
