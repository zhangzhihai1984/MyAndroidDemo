package com.twigcodes.ui.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.twigcodes.ui.R

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_BRIGHTNESS_HEIGHT = 60
        private const val DEFAULT_BRIGHTNESS_MARGIN_TOP = 30
    }

    private val mSimpleColorPickerView: SimpleColorPickerView
    private val mBrightnessView: BrightnessView

    init {
        orientation = VERTICAL

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickerView, defStyleAttr, defStyleRes)
        val hasBrightness = a.getBoolean(R.styleable.ColorPickerView_hasBrightness, true)
        val brightnessHeight = a.getDimensionPixelSize(R.styleable.ColorPickerView_brightnessHeight, DEFAULT_BRIGHTNESS_HEIGHT)
        val brightnessMarginTop = a.getDimensionPixelSize(R.styleable.ColorPickerView_brightnessMarginTop, DEFAULT_BRIGHTNESS_MARGIN_TOP)

        a.recycle()

        mSimpleColorPickerView = SimpleColorPickerView(context, attrs, defStyleAttr, defStyleRes)
        mBrightnessView = BrightnessView(context, attrs, defStyleAttr, defStyleRes)
        addView(mSimpleColorPickerView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        if (hasBrightness)
            addView(mBrightnessView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100).apply { topMargin = brightnessMarginTop })

        initView()
    }

    private fun initView() {
    }

    fun colorPicks() = mSimpleColorPickerView.colorPicks()
}