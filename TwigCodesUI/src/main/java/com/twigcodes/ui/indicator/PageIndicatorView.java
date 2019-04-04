package com.twigcodes.ui.indicator;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.twigcodes.ui.R;

public class PageIndicatorView extends LinearLayout {
    private final static int DEFAULT_INDICATOR_WIDTH = 5;
    private ViewPager mViewPager;
    private int mIndicatorMargin;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mInAnimatorResId;
    private int mOutAnimatorResId;
    private int mIndicatorBackgroundResId = R.drawable.page_indicator_default_drawable;
    private int mIndicatorUnselectedBackgroundResId = R.drawable.page_indicator_default_drawable;
    private Animator mInAnimator;
    private Animator mOutAnimator;
    private Animator mImmediateInAnimator;
    private Animator mImmediateOutAnimator;

    private int mLastPosition = -1;

    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView);

        mIndicatorWidth = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_width, -1);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_height, -1);
        mIndicatorMargin = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_margin, -1);

        mInAnimatorResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_animator_in, R.animator.page_indicator_default_animator);
        mOutAnimatorResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_animator_out, 0);
        mIndicatorBackgroundResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_drawable, R.drawable.page_indicator_default_drawable);
        mIndicatorUnselectedBackgroundResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_drawable_unselected, mIndicatorBackgroundResId);

        int orientation = a.getInt(R.styleable.PageIndicatorView_indicator_orientation, -1);
        setOrientation(orientation == VERTICAL ? VERTICAL : HORIZONTAL);

        int gravity = a.getInt(R.styleable.PageIndicatorView_indicator_gravity, -1);
        setGravity(gravity >= 0 ? gravity : Gravity.CENTER);

        a.recycle();

        checkIndicatorConfig(context);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin, R.animator.page_indicator_default_animator, 0, R.drawable.page_indicator_default_drawable, 0);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
                                   @AnimatorRes int inAnimatorResId, @AnimatorRes int outAnimatorrResId,
                                   @DrawableRes int indicatorBackgroundId,
                                   @DrawableRes int indicatorUnselectedBackgroundId) {

        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;

        mInAnimatorResId = inAnimatorResId;
        mOutAnimatorResId = outAnimatorrResId;
        mIndicatorBackgroundResId = indicatorBackgroundId;
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
    }

    private void checkIndicatorConfig(Context context) {
        mIndicatorWidth = (mIndicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight = (mIndicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorHeight;
        mIndicatorMargin = (mIndicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorMargin;

        mInAnimatorResId = (mInAnimatorResId == 0) ? R.animator.page_indicator_default_animator : mInAnimatorResId;

        mInAnimator = createInAnimator(context);
        mImmediateInAnimator = createInAnimator(context).setDuration(0);

        mOutAnimator = createOutAnimator(context);
        mImmediateOutAnimator = createOutAnimator(context).setDuration(0);

        mIndicatorBackgroundResId = (mIndicatorBackgroundResId == 0) ? R.drawable.page_indicator_default_drawable : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId = (mIndicatorUnselectedBackgroundResId == 0) ? mIndicatorBackgroundResId : mIndicatorUnselectedBackgroundResId;
    }

    private Animator createInAnimator(Context context) {
        return AnimatorInflater.loadAnimator(context, mInAnimatorResId);
    }

    private Animator createOutAnimator(Context context) {
        Animator outAnimator;

        if (mOutAnimatorResId == 0) {
            outAnimator = AnimatorInflater.loadAnimator(context, mInAnimatorResId);
            outAnimator.setInterpolator(new ReverseInterpolator());
        } else {
            outAnimator = AnimatorInflater.loadAnimator(context, mOutAnimatorResId);
        }

        return outAnimator;
    }

    public void setViewPager(@NonNull ViewPager viewPager) {
        mViewPager = viewPager;

        if (mViewPager.getAdapter() == null) {
            throw new IllegalStateException("Please set adapter for the ViewPager");
        }

        mLastPosition = mViewPager.getCurrentItem();
        mViewPager.getAdapter().registerDataSetObserver(mDataSetObserver);

        createIndicators();

        mViewPager.removeOnPageChangeListener(mPageChangeListener);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @SuppressWarnings("ConstantConditions")
    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mLastPosition = mViewPager.getCurrentItem();

            /*int newCount = mViewPager.getAdapter().getCount();
            int currentCount = getChildCount();

            if (newCount == currentCount) {
                return;
            }*/

            createIndicators();
        }
    };

    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (mInAnimator.isRunning()) {
                mInAnimator.end();
            }

            if (mOutAnimator.isRunning()) {
                mOutAnimator.end();
            }

            View outIndicator = getChildAt(mLastPosition);
            if (outIndicator != null) {
                outIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
                mOutAnimator.setTarget(outIndicator);
                mOutAnimator.start();
            }

            View inIndicator = getChildAt(position);
            if (inIndicator != null) {
                inIndicator.setBackgroundResource(mIndicatorBackgroundResId);
                mInAnimator.setTarget(inIndicator);
                mInAnimator.start();
            }

            mLastPosition = position;
        }
    };

    @SuppressWarnings("ConstantConditions")
    private void createIndicators() {
        removeAllViews();

        int count = mViewPager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }

        int orientation = getOrientation();

        for (int i = 0; i < count; i++) {
            if (mLastPosition == i) {
                addIndicator(orientation, mIndicatorBackgroundResId, mImmediateInAnimator);
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId, mImmediateOutAnimator);
            }
        }
    }

    private void addIndicator(int orientation, @DrawableRes int backgroundResId, Animator animator) {
        if (animator.isRunning()) {
            animator.end();
        }

        View indicator = new View(getContext());
        indicator.setBackgroundResource(backgroundResId);

        LayoutParams lp = new LayoutParams(mIndicatorWidth, mIndicatorHeight);
        if (orientation == HORIZONTAL) {
            lp.leftMargin = mIndicatorMargin;
            lp.rightMargin = mIndicatorMargin;
        } else {
            lp.topMargin = mIndicatorMargin;
            lp.bottomMargin = mIndicatorMargin;
        }

        addView(indicator, lp);

        animator.setTarget(indicator);
        animator.start();
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    private int dip2px(float dpValue) {
        final float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}