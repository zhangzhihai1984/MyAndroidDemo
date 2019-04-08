package com.usher.demo.rx;

import android.os.Bundle;
import android.widget.Button;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;
import com.usher.demo.utils.RxUtil;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class RxSumActivity extends BaseActivity {
    @BindView(R.id.param1_button)
    Button mParam1Button;
    @BindView(R.id.param2_button)
    Button mParam2Button;
    @BindView(R.id.sum_button)
    Button mSumButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_sum);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Observable<Integer> parm1$ = RxView.clicks(mParam1Button)
                .map(v -> 1)
                .scan((acc, curr) -> acc + curr)
                .doOnNext(v -> mParam1Button.setText(String.valueOf(v)));

        Observable<Integer> parm2$ = RxView.clicks(mParam2Button)
                .map(v -> 1)
                .scan((acc, curr) -> acc + curr)
                .doOnNext(v -> mParam2Button.setText(String.valueOf(v)));

        Observable.combineLatest(
                parm1$,
                parm2$,
                (first, second) -> first + second
        )
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> mSumButton.setText(String.valueOf(v)));
    }
}
