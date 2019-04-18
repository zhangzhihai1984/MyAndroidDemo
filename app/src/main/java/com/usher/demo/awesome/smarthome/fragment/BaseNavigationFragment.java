package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class BaseNavigationFragment extends Fragment {
    private Theme mTheme = Theme.UNKNOWN;

    public enum Theme {
        UNKNOWN,
        DARK,
        LIGHT
    }

    public void setStatusBarTheme(Theme theme) {
        mTheme = theme;

        View decorView = requireActivity().getWindow().getDecorView();
        int visibility = decorView.getSystemUiVisibility();

        if (theme == Theme.DARK) {
            visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (theme == Theme.LIGHT) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        decorView.setSystemUiVisibility(visibility);
    }

    public Theme getTheme() {
        return mTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.bind(this, fragmentView);
        init();

        return fragmentView;
    }

    public abstract int getLayoutRes();

    public abstract void init();
}
