package com.usher.demo.awesome.smarthome.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class BasePagerFragment extends Fragment {
    private View mFragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mFragmentView) {
            mFragmentView = inflater.inflate(getLayoutRes(), container, false);
            ButterKnife.bind(this, mFragmentView);
            init();
        } else {
            ViewGroup parent = (ViewGroup) mFragmentView.getParent();
            if (null != parent)
                parent.removeView(mFragmentView);
        }

        return mFragmentView;
    }

    public abstract int getLayoutRes();

    public abstract void init();
}