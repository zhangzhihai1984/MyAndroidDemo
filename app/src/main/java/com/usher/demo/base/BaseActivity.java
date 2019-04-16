package com.usher.demo.base;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.usher.demo.utils.PermissionUtil;
import com.usher.demo.utils.RxUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Observable.range(0, grantResults.length)
                .map(i -> grantResults[i] == PackageManager.PERMISSION_GRANTED)
                .reduce(true, (acc, curr) -> acc && curr)
                .as(RxUtil.autoDispose(this))
                .subscribe(granted -> {
                    if (granted) {
                        PermissionUtil.grant();
                    } else {
                        PermissionUtil.deny();
                    }
                });
    }
}
