package com.usher.demo.rx;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;
import com.usher.demo.utils.RxUtil;

import androidx.annotation.Nullable;
import io.reactivex.subjects.PublishSubject;

public class RxExitActivity extends BaseActivity {
    private static final int EXIT_DURATION = 500;

    private PublishSubject<Integer> subject = PublishSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rx_exit);

        initView();
    }

    private void initView() {
        Button exitButton = findViewById(R.id.exit_button);

        RxView.clicks(exitButton)
                .timeInterval()
                .doOnNext(v -> {
                    if (v.time() > EXIT_DURATION)
                        Toast.makeText(this, "Click once more to exit", Toast.LENGTH_SHORT).show();
                })
                .filter(v -> v.time() < EXIT_DURATION)
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> finish());

        subject.timeInterval()
                .doOnNext(v -> {
                    if (v.time() > EXIT_DURATION)
                        Toast.makeText(this, "Click once more to exit", Toast.LENGTH_SHORT).show();
                })
                .filter(v -> v.time() < EXIT_DURATION)
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> finish());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            subject.onNext(1);
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
}

