package com.twigcodes.ui.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import kotlin.math.abs

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
            addView(mBrightnessView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, brightnessHeight).apply { topMargin = brightnessMarginTop })

        initView()
    }

    private fun initView() {
        mSimpleColorPickerView.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mBrightnessView.updateLayoutParams<LayoutParams> {
                        val margin = abs(mSimpleColorPickerView.width - mSimpleColorPickerView.height) / 2
                        leftMargin = margin
                        rightMargin = margin
                    }
                }

        mSimpleColorPickerView.colorChanges()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { color -> mBrightnessView.updateColor(color) }

        mBrightnessView.brightnessChanges()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { color -> mSimpleColorPickerView.updateColor(color, false) }
    }

    fun updateColor(color: Int) = mSimpleColorPickerView.updateColor(color)

    fun colorPicks() = mSimpleColorPickerView.colorPicks()
}