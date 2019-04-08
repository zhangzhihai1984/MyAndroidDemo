package com.usher.demo.rx;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;
import com.usher.demo.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class RxSplashActivity extends BaseActivity {

    private static final int COUNTDOWN_SECONDS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_splash);

        initView();
    }

    private String adUrl;

    private static final int COUNTDOWN_TAG = 0;
    private static final int SKIP_TAG = -1;
    private static final int AD_TAG = -2;

    private void initView() {
        ImageView adImageView = findViewById(R.id.imageview);
        Button skipButton = findViewById(R.id.skip_button);

        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> {
                    adImageView.setAlpha(1.0f);
                    adUrl = "URL";
                });

        Observable<Integer> ad$ = RxView.clicks(adImageView)
//                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .filter(v -> !TextUtils.isEmpty(adUrl))
                .take(1)
                .map(v -> AD_TAG)
                .share();

        ad$.as(RxUtil.autoDispose(this))
                .subscribe(
                        v -> Log.i("zzh", "AD Next"),
                        err -> {
                        },
                        () -> Log.i("zzh", "AD Completed")
                );


        Observable<Integer> skip$ = RxView.clicks(skipButton)
                .take(1)
                .map(v -> SKIP_TAG)
                .share();

        skip$.as(RxUtil.autoDispose(this))
                .subscribe(v -> Log.i("zzh", "Skip Next"),
                        err -> {
                        },
                        () -> Log.i("zzh", "Skip Completed")
                );

        Observable<Integer> countdown$ = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(v -> (int) (COUNTDOWN_SECONDS - v))
                .take(COUNTDOWN_SECONDS + 1);

        Observable.merge(ad$, skip$)
                .take(1)
                .startWith(COUNTDOWN_TAG)
                .switchMap(tag -> tag == COUNTDOWN_TAG ? countdown$ : Observable.just(tag))
                .compose(RxUtil.getSchedulerComposer())
                .takeUntil(v -> v <= 0)
                .as(RxUtil.autoDispose(this))
                .subscribe(
                        v -> {
                            Log.i("zzh", "" + v);
                            if (v >= 0) {
                                skipButton.setText(getString(R.string.splash_skip, String.valueOf(v)));
                            }
                        },
                        err -> {
                        },
                        () -> {
                            Log.i("zzh", "ALL Completed");
                            Toast.makeText(this, "Countdown Done", Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
