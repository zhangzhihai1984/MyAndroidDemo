package com.usher.demo.web.three;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ThreeWebView extends WebView {
    public ThreeWebView(Context context) {
        super(context);
    }

    public ThreeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return false;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(0, 0);
    }
}
