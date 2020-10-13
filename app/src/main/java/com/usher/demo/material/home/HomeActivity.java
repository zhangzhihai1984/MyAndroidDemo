package com.usher.demo.material.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
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

        Glide.with(this)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1602570022896&di=b88d6662683e464ba22f1fbe0e02c58a&imgtype=0&src=http%3A%2F%2Fa2.att.hudong.com%2F36%2F48%2F19300001357258133412489354717.jpg")
                .into(mBlurImageView);
//        Picasso.get().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1602570022896&di=b88d6662683e464ba22f1fbe0e02c58a&imgtype=0&src=http%3A%2F%2Fa2.att.hudong.com%2F36%2F48%2F19300001357258133412489354717.jpg").into(mBlurImageView);
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
