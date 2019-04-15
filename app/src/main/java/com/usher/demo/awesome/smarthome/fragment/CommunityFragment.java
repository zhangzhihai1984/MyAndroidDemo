package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CommunityFragment extends Fragment {
    private View mFragmentView;

    public CommunityFragment() {

    }

    public static CommunityFragment newInstance() {
        Bundle args = new Bundle();

        CommunityFragment fragment = new CommunityFragment();
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
        mFragmentView = inflater.inflate(R.layout.fragment_smarthome_community, container, false);
        initView();

        return mFragmentView;
    }

    private void initView() {

    }
}
