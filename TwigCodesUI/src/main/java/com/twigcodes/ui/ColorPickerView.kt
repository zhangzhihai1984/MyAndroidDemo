package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
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
//        addView(mBrightnessView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100))
    }

    private fun initView() {
    }

    fun colorPicks() = mSimpleColorPickerView.colorPicks()

    private class BrightnessView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.BLUE)
        }
    }
}