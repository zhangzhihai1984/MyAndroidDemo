package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.entities.ADInfo;
import com.usher.demo.utils.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeBannerFragment extends Fragment {
    @BindView(R.id.banner_imageview)
    ImageView mBannerImageView;

    private View mFragmentView;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mFragmentView) {
            mFragmentView = inflater.inflate(R.layout.fragment_smarthome_home_banner, container, false);
            ButterKnife.bind(this, mFragmentView);
            initView();
        } else {
            ViewGroup parent = (ViewGroup) mFragmentView.getParent();
            if (null != parent)
                parent.removeView(mFragmentView);
        }

        return mFragmentView;
    }

    private void initView() {
        if (null != getArguments()) {
            ADInfo adInfo = new Gson().fromJson(getArguments().getString(Constants.TAG_ADINFO), ADInfo.class);
            Picasso.get().load(adInfo.getUrl()).into(mBannerImageView);
        }
    }
}
