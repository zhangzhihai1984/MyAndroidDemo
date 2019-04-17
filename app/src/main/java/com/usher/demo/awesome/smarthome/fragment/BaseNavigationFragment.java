package com.usher.demo.awesome.smarthome.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;

public class BaseNavigationFragment extends Fragment {
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
}
