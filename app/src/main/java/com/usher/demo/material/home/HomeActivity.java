package com.usher.demo.material.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.usher.demo.R;

public class HomeActivity extends AppCompatActivity {
    private int mStatusBarHeight;
    private int mThresholdOffset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initData();
        initView();
    }

    private void initData() {
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
    }

    private void initView() {
        initBackground();
        initAppBarLayout();
        initTabLayout();
    }

    private void initBackground() {
        ImageView mBlurImageView = findViewById(R.id.blur_imageview);
        Picasso.get().load(R.mipmap.background1).transform(new BlurTransformation(this)).into(mBlurImageView);
    }

    private class BlurTransformation implements Transformation {
        private Context mContext;

        public BlurTransformation(Context context) {
            mContext = context;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int width = Math.round(source.getWidth() / 8);
            int height = Math.round(source.getHeight() / 8);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(source, width, height, false);

            RenderScript renderScript = RenderScript.create(mContext);

            final Allocation input = Allocation.createFromBitmap(renderScript, inputBitmap);
            final Allocation output = Allocation.createTyped(renderScript, input.getType());

            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            scriptIntrinsicBlur.setRadius(8);
            scriptIntrinsicBlur.setInput(input);
            scriptIntrinsicBlur.forEach(output);
            output.copyTo(inputBitmap);

            renderScript.destroy();

            source.recycle();

            return inputBitmap;
        }

        @Override
        public String key() {
            return "BlurTransformation";
        }
    }

    private void initAppBarLayout() {
        AppBarLayout mAppBarLayout = findViewById(R.id.appbar_layout);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        ImageView mScanImageView = findViewById(R.id.scan_imageview);
        ImageView mSearchImageView = findViewById(R.id.search_imageview);

        mAppBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i("zzh", "AppBarLayout height: " + mAppBarLayout.getLayoutParams().height);
                Log.i("zzh", "Toolbar height: " + mToolbar.getLayoutParams().height);
                Log.i("zzh", "StatusBar height: " + mStatusBarHeight);

                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mThresholdOffset = mAppBarLayout.getLayoutParams().height - 2 * mToolbar.getLayoutParams().height - mStatusBarHeight;

                Log.i("zzh", "threshold offset: " + mThresholdOffset);

                mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                    Log.i("zzh", "offset: " + verticalOffset);
                    if (Math.abs(verticalOffset) < mThresholdOffset) {
                        mScanImageView.setImageResource(R.mipmap.scan_light);
                        mSearchImageView.setImageResource(R.mipmap.search_light);
                    } else {
                        mScanImageView.setImageResource(R.mipmap.scan_dark);
                        mSearchImageView.setImageResource(R.mipmap.search_dark);
                    }
                });
            }
        });
    }

    private void initTabLayout() {
        HomeMainAdapter mPagerAdapter = new HomeMainAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
//        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = 50;
        }
    }
}
