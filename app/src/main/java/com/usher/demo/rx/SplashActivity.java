package com.usher.demo.rx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SplashActivity extends AppCompatActivity {

    private static final int COUNTDOWN_SECONDS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
        TextView mSkipTextView = findViewById(R.id.skip_textview);

        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(v -> COUNTDOWN_SECONDS - v)
                .take(COUNTDOWN_SECONDS + 1)
                .compose(RxUtil.getSchedulerComposer())
                .takeUntil(RxView.clicks(mSkipTextView).take(1))
                .doOnComplete(() -> {
                    Toast.makeText(this, "Countdown Done", Toast.LENGTH_SHORT).show();
                    mSkipTextView.setEnabled(false);
                })
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> mSkipTextView.setText(getString(R.string.splash_skip, String.valueOf(v))));
    }
}
