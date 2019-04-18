package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.usher.demo.R;
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

    public static HomeBannerFragment newInstance(int resId) {
        Bundle args = new Bundle();
        args.putInt(Constants.TAG_URL, resId);
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
            int resId = getArguments().getInt(Constants.TAG_URL);
            Picasso.get().load(resId).into(mBannerImageView);
        }
    }
}
