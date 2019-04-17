package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public abstract class BasePagerFragment extends Fragment {
    private final PublishSubject<Boolean> mVisible$ = PublishSubject.create();
    private final PublishSubject<Boolean> mCreated$ = PublishSubject.create();

    private View mFragmentView;

    public BasePagerFragment() {
        Observable.combineLatest(mVisible$, mCreated$, (visible, created) -> true)
                .take(1)
                .doOnComplete(this::init)
                .subscribe();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mFragmentView) {
            mFragmentView = inflater.inflate(getLayoutRes(), container, false);
            ButterKnife.bind(this, mFragmentView);
            mCreated$.onNext(true);
        } else {
            ViewGroup parent = (ViewGroup) mFragmentView.getParent();
            if (null != parent)
                parent.removeView(mFragmentView);
        }

        return mFragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mVisible$.onNext(isVisibleToUser);
    }

    public abstract int getLayoutRes();

    public abstract void init();
}
