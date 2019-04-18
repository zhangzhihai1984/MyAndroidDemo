package com.usher.demo.awesome.smarthome;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.utils.Constants;
import com.usher.demo.utils.RxUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class BottomTabView extends LinearLayout implements INavigator {
    private final List<Integer> mTabDefaultIcons = new ArrayList<>();
    private final List<Integer> mTabSelectedIcons = new ArrayList<>();
    private final List<String> mTabTexts = new ArrayList<>();
    private final List<String> mTabTags = new ArrayList<>();
    private final List<View> mTabViews = new ArrayList<>();

    private String mCurrentTabTag = Constants.TAB_TAG_HOME;

    PublishSubject<String> navigateSubject = PublishSubject.create();

    public BottomTabView(Context context) {
        this(context, null);
    }

    public BottomTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BottomTabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initData();
        initView();
    }

    private void initData() {
        mTabTags.add(Constants.TAB_TAG_HOME);
        mTabTags.add(Constants.TAB_TAG_SMART);
        mTabTags.add(Constants.TAB_TAG_COMMUNITY);
        mTabTags.add(Constants.TAB_TAG_PROPERTY);
        mTabTags.add(Constants.TAB_TAG_CENTER);

        mTabTexts.add("首页");
        mTabTexts.add("智能家居");
        mTabTexts.add("智慧社区");
        mTabTexts.add("物业管理");
        mTabTexts.add("我的");

        mTabDefaultIcons.add(R.drawable.bottom_tab_icon_home_default);
        mTabDefaultIcons.add(R.drawable.bottom_tab_icon_smart_default);
        mTabDefaultIcons.add(R.drawable.bottom_tab_icon_community_default);
        mTabDefaultIcons.add(R.drawable.bottom_tab_icon_property_default);
        mTabDefaultIcons.add(R.drawable.bottom_tab_icon_center_default);

        mTabSelectedIcons.add(R.drawable.bottom_tab_icon_home_selected);
        mTabSelectedIcons.add(R.drawable.bottom_tab_icon_smart_selected);
        mTabSelectedIcons.add(R.drawable.bottom_tab_icon_community_selected);
        mTabSelectedIcons.add(R.drawable.bottom_tab_icon_property_selected);
        mTabSelectedIcons.add(R.drawable.bottom_tab_icon_center_selected);
    }

    private void initView() {
        setOrientation(HORIZONTAL);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);

        for (int i = 0; i < mTabTags.size(); i++) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_tab_item, this, false);
            itemView.setLayoutParams(params);
            itemView.setTag(mTabTags.get(i));

            RxView.clicks(itemView)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .map(v -> itemView.getTag().toString())
                    .filter(tag -> !mCurrentTabTag.equals(tag))
                    .as(RxUtil.autoDispose((LifecycleOwner) getContext()))
                    .subscribe(this::updateTabs);

            ((TextView) itemView.findViewById(R.id.tab_textview)).setText(mTabTexts.get(i));

            mTabViews.add(itemView);
            addView(itemView);
        }

        setTabIcon(mCurrentTabTag);
    }

    private void setTabIcon(String tabTag) {
        for (int i = 0; i < mTabTags.size(); i++) {
            View itemView = mTabViews.get(i);
            ImageView iconView = itemView.findViewById(R.id.tab_imageview);
            TextView nameView = itemView.findViewById(R.id.tab_textview);

            if (mTabTags.get(i).equalsIgnoreCase(tabTag)) {
                iconView.setImageResource(mTabSelectedIcons.get(i));
                nameView.setTextColor(Color.parseColor("#000000"));
            } else {
                iconView.setImageResource(mTabDefaultIcons.get(i));
                nameView.setTextColor(Color.parseColor("#9b9b9b"));
            }
        }
    }

    @Override
    public void clear() {
        mTabDefaultIcons.clear();
        mTabSelectedIcons.clear();
        mTabTags.clear();
        mTabViews.clear();
    }

    @Override
    public void updateTabs(String tag) {
        mCurrentTabTag = tag;

        setTabIcon(tag);

        navigateSubject.onNext(tag);
    }

    @Override
    public Observable<String> onNavigated() {
        return navigateSubject;
    }
}
