package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.SmartFragmentAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartFragment extends Fragment {
    @BindView(R.id.smarttablayout)
    SmartTabLayout mSmartTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private View mFragmentView;

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
        mFragmentView = inflater.inflate(R.layout.fragment_smarthome_smart, container, false);
        ButterKnife.bind(this, mFragmentView);
        initView();

        return mFragmentView;
    }

    private void initView() {
        mViewPager.setAdapter(new SmartFragmentAdapter(requireFragmentManager()));
        mSmartTabLayout.setViewPager(mViewPager);
    }
}
