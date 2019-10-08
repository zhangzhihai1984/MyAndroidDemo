package com.usher.demo.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.twigcodes.ui.CircleWaveView;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;
import com.usher.demo.utils.RxUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class CircleWaveActivity extends BaseActivity {
    @BindView(R.id.circlewaveview)
    CircleWaveView mCircleWaveView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_circle_wave);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
//        Observable.timer(5000, TimeUnit.MILLISECONDS)
//                .compose(RxUtil.getSchedulerComposer())
//                .as(RxUtil.autoDispose(this))
//                .subscribe(v -> mCircleWaveView.destroy());

//        Observable.timer(8000, TimeUnit.MILLISECONDS)
//                .compose(RxUtil.getSchedulerComposer())
//                .as(RxUtil.autoDispose(this))
//                .subscribe(v -> mCircleWaveView.start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCircleWaveView.destroy();
    }
}