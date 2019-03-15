package com.usher.demo.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.usher.demo.R;

public class PagerFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_URL = "section_url";

    private View mFragmentView;

    private int mNumber;
    private String mUrl;

    private boolean mIsViewCreated = false;
    private boolean mIsFirstIn = true;

    public PagerFragment() {
        // Required empty public constructor
    }

    public static PagerFragment newInstance(int number, String url) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, number);
        args.putString(ARG_SECTION_URL, url);

        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mUrl = getArguments().getString(ARG_SECTION_URL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.i("zzh", "onCreateView " + mNumber + " " + (null == mFragmentView));

        if (null == mFragmentView) {
            mFragmentView = inflater.inflate(R.layout.fragment_pager, container, false);

            initView();
        } else {
            ViewGroup parent = (ViewGroup) mFragmentView.getParent();

            if (null != parent) {
                parent.removeView(mFragmentView);
            }
        }

        return mFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        Log.i("zzh", "setUserVisibleHint " + mNumber + " " + isVisibleToUser);
    }

    private void initView() {
        mIsViewCreated = true;

        ((TextView) mFragmentView.findViewById(R.id.textview)).setText(String.valueOf(mNumber));

        ImageView imageView = mFragmentView.findViewById(R.id.imageview);
//        imageView.postDelayed(() -> Picasso.get().load(mUrl).into(imageView), 1000);
        Picasso.get().load(mUrl).into(imageView);

        imageView.setOnClickListener(v -> Toast.makeText(getContext()," " + mNumber, Toast.LENGTH_SHORT).show());
    }
}


