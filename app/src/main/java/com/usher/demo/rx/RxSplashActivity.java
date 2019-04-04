package com.usher.demo.rx;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import kotlin.Unit;

public class RxSplashActivity extends AppCompatActivity {

    private static final int COUNTDOWN_SECONDS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_splash);

        initView();
    }

    ImageView adImageView;
    private Button skipButton;
    private String adUrl;

    private void initView() {
        adImageView = findViewById(R.id.imageview);
        skipButton = findViewById(R.id.skip_button);

        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> {
                    adImageView.setAlpha(1.0f);
                    adUrl = "URL";
                });

        Observable<Unit> ad$ = RxView.clicks(adImageView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .filter(v -> !TextUtils.isEmpty(adUrl))
                .share();

        ad$.as(RxUtil.autoDispose(this))
                .subscribe(v -> Log.i("zzh", "AD Clicked"));
        Observable<Unit> skip$ = RxView.clicks(skipButton).take(1).share();
//        Observable<Unit> stop$ = Observable.merge(ad$, skip$);

        skip$.as(RxUtil.autoDispose(this))
                .subscribe(v -> {
                    Log.i("zzh", "Skip Countdown");
                });

        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(v -> COUNTDOWN_SECONDS - v)
                .take(COUNTDOWN_SECONDS + 1)
                .compose(RxUtil.getSchedulerComposer())
//                .takeUntil(stop$)
                .doOnComplete(() -> {
                    Toast.makeText(this, "Countdown Done", Toast.LENGTH_SHORT).show();
                    skipButton.setEnabled(false);
                })
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> skipButton.setText(getString(R.string.splash_skip, String.valueOf(v))));

//        Observable.fromArray(1, 2, 3, 4, 5)
//                .zipWith(Observable.interval(1000, TimeUnit.MILLISECONDS), (v1, v2) -> v1)
//                .doOnNext(this::doNext)
//                .onErrorResumeNext(Observable.error(new Exception("HAHA")))
//                .onErrorResumeNext(err -> {
//                    return Observable.interval(1000, TimeUnit.MILLISECONDS).take(3).map(v -> 1);
//                } )
//                .onErrorReturn(err -> 100)
//                .onErrorReturnItem(100)
//                .takeUntil(v -> v == 4)
//                .compose(RxUtil.getSchedulerComposer())
//                .as(RxUtil.autoDispose(this))
//                .subscribe(
//                        v -> Log.i("zzh", String.valueOf(v)),
//                        err -> Log.i("zzh", "Error: " + err.getMessage()),
//                        () -> Log.i("zzh", "Completed")
//                );

    }

    private void startNextPage() {

    }
}
