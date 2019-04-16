package com.usher.demo.awesome.smarthome;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class IndoorAirQualityView extends RelativeLayout {

    public IndoorAirQualityView(Context context) {
        this(context, null);
    }

    public IndoorAirQualityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndoorAirQualityView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public IndoorAirQualityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
