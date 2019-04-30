package com.usher.demo.awesome.smarthome;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.twigcodes.ui.pager.LoopViewPager;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.entities.ADInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeBannerView extends RelativeLayout {
    private final List<ADInfo> mDataList = new ArrayList<>();
    private final Context mContext;

    @BindView(R.id.banner_viewpager)
    LoopViewPager mBannerViewPager;

    @BindView(R.id.banner_indicator)
    SmartTabLayout mBannerIndicatorView;

    private HomeBannerAdapter mBannerAdapter;

    public HomeBannerView(Context context) {
        this(context, null);
    }

    public HomeBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HomeBannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.smarthome_home_banner, this);
        ButterKnife.bind(this);
        mContext = context;

        initView();
    }

    private void initView() {
        mBannerAdapter = new HomeBannerAdapter(((FragmentActivity) mContext).getSupportFragmentManager(), mDataList);
        mBannerViewPager.setAdapter(mBannerAdapter);
        mBannerIndicatorView.setViewPager(mBannerViewPager);
    }

    public void setData(List<ADInfo> list) {
        mDataList.clear();
        mDataList.addAll(list);
        mBannerAdapter.setData(mDataList);
        mBannerAdapter.notifyDataSetChanged();
        mBannerIndicatorView.setViewPager(mBannerViewPager);
        mBannerViewPager.setCurrentItem(0);
    }
}
