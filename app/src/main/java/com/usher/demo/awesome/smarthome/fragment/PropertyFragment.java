package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PropertyFragment extends Fragment {
    private View mFragmentView;

    public PropertyFragment() {

    }

    public static PropertyFragment newInstance() {
        Bundle args = new Bundle();

        PropertyFragment fragment = new PropertyFragment();
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
        mFragmentView = inflater.inflate(R.layout.fragment_smarthome_property, container, false);
        initView();

        return mFragmentView;
    }

    private void initView() {

    }
}
