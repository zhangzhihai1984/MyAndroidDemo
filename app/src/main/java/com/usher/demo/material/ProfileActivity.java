package com.usher.demo.material;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.usher.demo.R;

public class ProfileActivity extends AppCompatActivity {
    private int mStatusBarHeight;
    private int mThresholdOffset;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_profile);

//        setSupportActionBar(findViewById(R.id.toolbar));

        /*Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        window.setStatusBarColor(Color.TRANSPARENT);
        window.setStatusBarColor(Color.parseColor("#66000000"));*/

        AppBarLayout mAppBarLayout = findViewById(R.id.appbar_layout);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        ImageView mScanImageView = findViewById(R.id.scan_imageview);
        ImageView mSearchImageView = findViewById(R.id.search_imageview);

        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }

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

}
