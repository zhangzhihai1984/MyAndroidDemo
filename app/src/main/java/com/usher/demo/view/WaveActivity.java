package com.usher.demo.view;

import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.twigcodes.ui.wave.WaveView;
import com.usher.demo.R;

import java.lang.ref.WeakReference;

public class WaveActivity extends AppCompatActivity {
    private WaveView mWaveView;
    private boolean mIsAddFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);

        initView();
    }

    private void initView() {
        mWaveView = findViewById(R.id.wave_view);
        mWaveView.setProgress(100);


//        new ProgressHandler(this).sendEmptyMessage(0);
    }

    private static class ProgressHandler extends Handler {
        final WeakReference<WaveActivity> mReference;
        private ProgressHandler(WaveActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int progress = msg.what;

            WaveActivity activity = mReference.get();

            if (null == activity)
                return;
            activity.mWaveView.setProgress(progress);

            if (activity.mIsAddFlag) {
                if (progress >= 100) {
                    activity.mIsAddFlag = false;
                    progress -= 1;
                } else {
                    progress += 1;
                }
            } else {
                if (progress <= 0) {
                    activity.mIsAddFlag = true;
                    progress += 1;
                } else {
                    progress -= 1;
                }
            }

            sendEmptyMessageDelayed(progress, 50);
        }
    }
}
