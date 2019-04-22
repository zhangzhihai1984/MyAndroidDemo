package com.usher.demo.material;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.rxbinding3.viewpager.RxViewPager;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class TabLayoutActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("场景管理"));
        tabLayout.addTab(tabLayout.newTab().setText("设备管理"));
        tabLayout.addTab(tabLayout.newTab().setText("其他设备"));
//        tabLayout.addTab(tabLayout.newTab().setText("儿童"));
//        tabLayout.addTab(tabLayout.newTab().setText("我的"));

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = 50;
        }

        SmartTabLayout smartTabLayout = findViewById(R.id.smarttablayout);
        smartTabLayout.setViewPager(mViewPager);

        SmartTabLayout smartTabLayout2 = findViewById(R.id.smarttablayout2);
        smartTabLayout2.setViewPager(mViewPager);

        SmartTabLayout smartTabLayout3 = findViewById(R.id.smarttablayout3);
        smartTabLayout3.setViewPager(mViewPager);

        RxViewPager.pageSelections(mViewPager)
                .as(RxUtil.autoDispose(this))
                .subscribe(position -> {
                    for (int i = 0; i < 3; i++) {
                        TextView view = (TextView) smartTabLayout.getTabAt(i);
                        view.setTextSize(i == position ? 18 : 14);
                    }
                });

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private View mFragmentView;

        private final PublishSubject<Boolean> mVisible$ = PublishSubject.create();
        private final PublishSubject<Boolean> mCreated$ = PublishSubject.create();

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @SuppressLint("CheckResult")
        public PlaceholderFragment() {
            Observable.combineLatest(
                    mVisible$.filter(visible -> visible),
                    mCreated$,
                    (v1, v2) -> v1
            )
                    .take(1)
                    .subscribe(
                            v -> doRequest(),
                            err -> Log.i("zzh", err.getMessage())
                    );
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.i("zzh", getArguments().getInt(ARG_SECTION_NUMBER) + " onCreateView");
            if (null == mFragmentView) {
                mFragmentView = inflater.inflate(R.layout.fragment_tab_layout, container, false);
                TextView textView = mFragmentView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

                mCreated$.onNext(true);
            } else {
                ViewGroup parent = (ViewGroup) mFragmentView.getParent();
                if (null != parent)
                    parent.removeView(mFragmentView);
            }

            return mFragmentView;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            mVisible$.onNext(isVisibleToUser);

            Log.i("zzh", getArguments().getInt(ARG_SECTION_NUMBER) + " setUserVisibleHint " + isVisibleToUser);
        }

        private void doRequest() {
            Log.i("zzh", getArguments().getInt(ARG_SECTION_NUMBER) + " doRequest");
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);

//            Observable.range(0, 5)
//                    .as(RxUtil.autoDispose((LifecycleOwner) context))
//                    .subscribe(
//                            v -> mFragmentList.add(PlaceholderFragment.newInstance(v + 1)),
//                            err -> Log.i("zzh", err.getMessage())
//                    );

            for (int i = 0; i < 3; i++)
                mFragmentList.add(PlaceholderFragment.newInstance(i + 1));

            mTitleList.add("场景管理");
            mTitleList.add("设备管理");
            mTitleList.add("其他设备");
//            mTitleList.add("儿童");
//            mTitleList.add("我的");
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }
}
