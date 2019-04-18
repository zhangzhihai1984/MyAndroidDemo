package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.entities.ADInfo;
import com.usher.demo.awesome.smarthome.fragment.base.BasePagerFragment;
import com.usher.demo.utils.Constants;

import butterknife.BindView;

public class HomeBannerFragment extends BasePagerFragment {
    @BindView(R.id.banner_imageview)
    ImageView mBannerImageView;

    public HomeBannerFragment() {

    }

    public static HomeBannerFragment newInstance(ADInfo adInfo) {
        Bundle args = new Bundle();
        args.putString(Constants.TAG_ADINFO, new Gson().toJson(adInfo));
        HomeBannerFragment fragment = new HomeBannerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_smarthome_home_banner;
    }

    @Override
    public void init() {
        if (null != getArguments()) {
            ADInfo adInfo = new Gson().fromJson(getArguments().getString(Constants.TAG_ADINFO), ADInfo.class);
            Picasso.get().load(adInfo.getUrl()).into(mBannerImageView);
        }
    }
}