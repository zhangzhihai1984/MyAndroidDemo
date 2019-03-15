package com.usher.demo.retrofit;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.AutoDisposeAndroidPlugins;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.usher.demo.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class RxActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        initData();
    }

    private Disposable disposable;

    DefaultLifecycleObserver b = new DefaultLifecycleObserver() {
        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {

        }
    };

    private void initData() {

        getLifecycle().addObserver(new MyLifecycleObserverImpl());
        /*Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(() -> Log.i("zzh", "doOnDispose"))
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
//                .takeUntil(v -> v == 5)
                .takeUntil(Observable.timer(5, TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        Log.i("zzh", "onSubscribe");
                    }

                    @Override
                    public void onNext(Long v) {
                        Log.i("zzh", "onNext: " + v + " " + disposable.isDisposed());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("zzh", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zzh", "onComplete " + disposable.isDisposed());
                    }
                });*/

        BehaviorSubject<String> subject = BehaviorSubject.create();
        subject.onNext("A");
        subject.onNext("B");

        Observable.just("1", "2")
                .delay(3, TimeUnit.SECONDS)
                .zipWith(subject, (v1, v2) -> (v1 + v2))
                .doOnDispose(() -> Log.i("zzh", "doOnDispose"))
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("zzh", "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("zzh", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zzh", "onComplete");
                    }
                });

        Observable.timer(6, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        subject.onNext("C");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        B b = this.<B, A, F3>genericTest(Observable.just(new C()), new A());

//        LifecycleObserver bb = new DefaultLifecycleObserver() {
//        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("zzh", "onDestroy");
    }

    private <T> ObservableTransformer<T, T> getThreadTransformer() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T, R> ObservableTransformer<? super T, ? extends R> getTransformer() {
        return new ObservableTransformer<T, R>() {
            @Override
            public ObservableSource<R> apply(Observable<T> upstream) {
                return null;
            }
        };
    }

    private <T extends A, U extends A, R extends F1 & F2> T genericTest(Observable<? extends T> t, U u) {
        u.hello();
        return null;
    }

    private <T> void g(Class<T> t) {
        try {
            Constructor<T> con = t.getConstructor(Integer.class, String.class);
            T t1 = con.newInstance(1, "2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface F1 {
    }

    interface F2 {
    }

    interface F3 extends F1, F2 {

    }

    class A {
        void hello() {
        }
    }

    class B extends A {

    }

    class C extends B {
    }

    private void genericTest() {
        List<B> aList = new ArrayList<>();
//        aList.add(new A());
        aList.add(new B());
        aList.add(new C());
        genericTest2(aList);

        List<? extends A> list1 = new ArrayList<B>();
//        list1.add(new A()); //Error
//        A a = list1.get(0);

        List<? super B> list2 = new ArrayList<>();
        list2.add(new B());
        list2.add(new C());
//        list2.add(new A()); //Error
    }

    private void genericTest2(List<? extends A> list) {
        A a1 = list.get(0);
        B b2 = (B) list.get(1);
    }
}
