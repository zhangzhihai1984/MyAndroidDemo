package com.twigcodes.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WindowInsetsFrameLayout extends FrameLayout {
    public WindowInsetsFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public WindowInsetsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindowInsetsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WindowInsetsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                requestApplyInsets();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        for (int i = 0; i < getChildCount(); i++)
            getChildAt(i).dispatchApplyWindowInsets(insets);

        return insets;
    }
}
