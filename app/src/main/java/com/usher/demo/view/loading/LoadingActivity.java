package com.usher.demo.view.loading;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.usher.demo.R;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private ImageView mFanImageView;
    private boolean mIsAddFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initView();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progressbar);
        LayerDrawable drawable = (LayerDrawable) mProgressBar.getProgressDrawable();
//        findViewById(R.id.view).setBackground(drawable.getDrawable(0));

        mFanImageView = findViewById(R.id.fan_imageview);

        new ProgressHander(this).sendEmptyMessage(0);
    }

    private static class ProgressHander extends Handler {
        final WeakReference<LoadingActivity> mReference;

        private ProgressHander(LoadingActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int progress = msg.what;

            LoadingActivity activity = mReference.get();

            if (null == activity)
                return;

//            activity.mProgressBar.setProgress(progress);
            activity.mFanImageView.setRotation((float) progress / 20 * -360);

            if (activity.mIsAddFlag) {
                if (progress >= 100) {
                    activity.mIsAddFlag = false;
                    progress -= 1;
                } else {
                    progress += progress > 50 ? 2 : 1;
                }
            } else {
                if (progress <= 0) {
                    activity.mIsAddFlag = true;
                    progress += 1;
                } else {
                    progress -= progress > 50 ? 2 : 1;
                }
            }

            sendEmptyMessageDelayed(progress, 50);
        }
    }
}
