package com.usher.demo.rx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import io.reactivex.Observable;

public class RxSumActivity extends AppCompatActivity {
    private Button mParam1Button;
    private Button mParam2Button;
    private Button mSumButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_sum);

        initView();
    }

    private void initView() {
        mParam1Button = findViewById(R.id.param1_button);
        mParam2Button = findViewById(R.id.param2_button);
        mSumButton = findViewById(R.id.sum_button);

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
