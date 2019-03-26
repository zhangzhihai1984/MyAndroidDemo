package com.usher.demo.rx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class SplashActivity extends AppCompatActivity {
    private TextView mSkipTextView;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private PublishSubject<String> subject = PublishSubject.create();
    private boolean mSkipFlag = false;

    private void initView() {
        mSkipTextView = findViewById(R.id.skip_textview);
//        mSkipTextView.setText(getString(R.string.splash_skip, 1));

        Disposable d = RxView.clicks(mSkipTextView)
                .subscribe(o -> {
                    Log.i("zzh", "Click");
                    mSkipFlag = true;
                    subject.onNext("Hello");
                });

        findViewById(R.id.dispose_button).setOnClickListener(v -> {
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        });
        Observable<Integer> click$ = subject.map(v -> 1).scan((x, y) -> x + y).map(r -> r % 2); //1010
//        Observable<Integer> startCondition = click$.filter(x -> x == 1);

        Observable<Long> countdown$ = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(v -> 5 - v)
                .takeUntil(click$.filter(x -> x == 0).doOnNext(v -> Log.i("zzh", "In: " + v)));

//        countdown$.subscribe(new Observer<Long>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                mDisposable = d;
//            }
//
//            @Override
//            public void onNext(Long aLong) {
//                Log.i("zzh", "[Countdown] onNext: " + aLong);
//                mSkipTextView.setText(getString(R.string.splash_skip, String.valueOf(aLong)));
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("zzh", "[Countdown] onError");
//            }
//
//            @Override
//            public void onComplete() {
//                Log.i("zzh", "[Countdown] onComplete");
//            }
//        });

        click$.filter(x -> x == 1)
                .doOnNext(v -> Log.i("zzh", "Out: " + v))
                .flatMap(a -> Observable.interval(0, 1, TimeUnit.SECONDS)
                        .map(v -> 5 - v)
                        .takeUntil(click$.filter(x -> x == 0).doOnNext(v -> Log.i("zzh", "In: " + v)))
                )
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.i("zzh", "[Countdown] onNext: " + aLong);
                        mSkipTextView.setText(getString(R.string.splash_skip, String.valueOf(aLong)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("zzh", "[Countdown] onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zzh", "[Countdown] onComplete");
                    }
                });

        startCountdown();
    }

    <T> ObservableTransformer<T, T> getThreadTransformer() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private void startCountdown() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(v -> 3 - v)
                .takeWhile(v -> v >= 0)
                .compose(getThreadTransformer())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.i("zzh", "[Countdown] onNext: " + aLong);
                        mSkipTextView.setText(getString(R.string.splash_skip, String.valueOf(aLong)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("zzh", "[Countdown] onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zzh", "[Countdown] onComplete");
                    }
                });

        Observable.interval(1000, TimeUnit.SECONDS)
                .take(10)
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> {
                });
    }
}
