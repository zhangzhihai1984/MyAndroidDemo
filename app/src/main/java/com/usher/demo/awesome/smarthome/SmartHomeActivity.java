package com.usher.demo.awesome.smarthome;

import android.os.Bundle;

import com.usher.demo.R;
import com.usher.demo.awesome.smarthome.fragment.CenterFragment;
import com.usher.demo.awesome.smarthome.fragment.CommunityFragment;
import com.usher.demo.awesome.smarthome.fragment.HomeFragment;
import com.usher.demo.awesome.smarthome.fragment.PropertyFragment;
import com.usher.demo.awesome.smarthome.fragment.SmartFragment;
import com.usher.demo.base.BaseActivity;
import com.usher.demo.utils.Constants;
import com.usher.demo.utils.RxUtil;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class SmartHomeActivity extends BaseActivity {
    @BindView(R.id.bottom_view)
    BottomTabView mBottomTabView;

    private final Map<String, Fragment> mFragmentMap = new HashMap<>();

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smarthome);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        mFragmentManager = getSupportFragmentManager();

        mFragmentMap.put(Constants.TAB_TAG_HOME, HomeFragment.newInstance());
        mFragmentMap.put(Constants.TAB_TAG_SMART, SmartFragment.newInstance());
        mFragmentMap.put(Constants.TAB_TAG_COMMUNITY, CommunityFragment.newInstance());
        mFragmentMap.put(Constants.TAB_TAG_PROPERTY, PropertyFragment.newInstance());
        mFragmentMap.put(Constants.TAB_TAG_CENTER, CenterFragment.newInstance());
    }

    private void initView() {
        mBottomTabView.onNavigated()
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
                .subscribe(this::navigateTab);
    }

    private void navigateTab(String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        Observable<Fragment> hide$ = Observable.fromIterable(mFragmentMap.keySet())
                .filter(key -> !key.equalsIgnoreCase(tag))
                .filter(key -> null != mFragmentManager.findFragmentByTag(key))
                .map(mFragmentMap::get)
                .doOnNext(transaction::hide);

        Observable<Fragment> show$ = Observable.fromIterable(mFragmentMap.keySet())
                .filter(key -> key.equalsIgnoreCase(tag))
                .map(mFragmentMap::get)
                .doOnNext(fragment -> {
                    if (null == mFragmentManager.findFragmentByTag(tag))
                        transaction.add(R.id.content_view, fragment, tag);
                    else
                        transaction.show(fragment);
                });

        Observable.concat(hide$, show$)
                .doOnComplete(transaction::commitAllowingStateLoss)
                .as(RxUtil.autoDispose(this))
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBottomTabView.clear();
    }
}