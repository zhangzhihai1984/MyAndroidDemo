package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.fragment.base.BaseLazyPagerFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OtherFragment extends BaseLazyPagerFragment {

    public OtherFragment() {
        super();
    }

    public static OtherFragment newInstance() {
        Bundle args = new Bundle();
        OtherFragment fragment = new OtherFragment();
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_smarthome_other;
    }

    @Override
    public void init() {
    }
}
