package com.twigcodes.ui.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mSimpleColorPickerView: SimpleColorPickerView
    private val mBrightnessView: BrightnessView

    init {
        orientation = VERTICAL

        mSimpleColorPickerView = SimpleColorPickerView(context, attrs, defStyleAttr, defStyleRes)
        mBrightnessView = BrightnessView(context, attrs, defStyleAttr, defStyleRes)
        addView(mSimpleColorPickerView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0).apply { weight = 1f })
        addView(mBrightnessView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100))
    }

    private fun initView() {
    }

    fun colorPicks() = mSimpleColorPickerView.colorPicks()
}