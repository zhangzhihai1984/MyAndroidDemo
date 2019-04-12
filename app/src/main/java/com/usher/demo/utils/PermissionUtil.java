package com.usher.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class PermissionUtil {
    private static PublishSubject<Boolean> subject = PublishSubject.create();

    public static Observable<Boolean> requestPermission(Context context, String permission) {
        return requestPermissions(context, new String[]{permission});
    }

    public static Observable<Boolean> requestPermissions(Context context, String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, permission)) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, permissionList.toArray(new String[permissionList.size()]), 0);
        } else {
            return Observable.just(true);
        }

        return subject;
    }

    public static void grant() {
        subject.onNext(true);
    }

    public static void deny() {
        subject.onNext(false);
    }
}
