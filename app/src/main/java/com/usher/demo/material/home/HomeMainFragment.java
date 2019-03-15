package com.usher.demo.material.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usher.demo.R;

public class HomeMainFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public HomeMainFragment() {
    }

    static HomeMainFragment newInstance(int sectionNumber) {
        HomeMainFragment fragment = new HomeMainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);
        int number = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : -1;
        ((TextView) rootView.findViewById(R.id.content_textview)).setText(getString(R.string.section_format, number));

        return rootView;
    }
}
