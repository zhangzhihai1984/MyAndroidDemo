package com.twigcodes.ui.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import kotlin.math.max

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_BRIGHTNESS_HEIGHT = 60
        private const val DEFAULT_BRIGHTNESS_MARGIN_TOP = 30
        private const val DEFAULT_ALPHA_HEIGHT = 60
        private const val DEFAULT_ALPHA_MARGIN_TOP = 30
    }

    private val mSimpleColorPickerView: SimpleColorPickerView
    private val mBrightnessView: BrightnessView
    private val mAlphaView: AlphaView

    init {
        orientation = VERTICAL

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickerView, defStyleAttr, defStyleRes)
        val hasBrightness = a.getBoolean(R.styleable.ColorPickerView_colorShowBrightness, true)
        val brightnessHeight = a.getDimensionPixelSize(R.styleable.ColorPickerView_colorBrightnessHeight, DEFAULT_BRIGHTNESS_HEIGHT)
        val brightnessMarginTop = a.getDimensionPixelSize(R.styleable.ColorPickerView_colorBrightnessMarginTop, DEFAULT_BRIGHTNESS_MARGIN_TOP)
        val hasAlpha = a.getBoolean(R.styleable.ColorPickerView_colorShowAlpha, false)
        val alphaHeight = a.getDimensionPixelSize(R.styleable.ColorPickerView_colorAlphaHeight, DEFAULT_ALPHA_HEIGHT)
        val alphaMarginTop = a.getDimensionPixelSize(R.styleable.ColorPickerView_colorAlphaMarginTop, DEFAULT_ALPHA_MARGIN_TOP)

        a.recycle()

        mSimpleColorPickerView = SimpleColorPickerView(context, attrs, defStyleAttr, defStyleRes)
        mBrightnessView = BrightnessView(context, attrs, defStyleAttr, defStyleRes)
        mAlphaView = AlphaView(context, attrs, defStyleAttr, defStyleRes)

        addView(mSimpleColorPickerView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        addView(mBrightnessView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, brightnessHeight).apply { topMargin = brightnessMarginTop })
        addView(mAlphaView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, alphaHeight).apply { topMargin = alphaMarginTop })

        if (!hasBrightness) mBrightnessView.visibility = View.GONE
        if (!hasAlpha) mAlphaView.visibility = View.GONE
        initView()
    }

    private fun initView() {
        /**
         * 为了美观, 我们通过设置margin的方式将[BrightnessView]的宽度与[SimpleColorPickerView]中的"颜色选取"区域
         * 这个圆形的宽度保持一致:
         * 如果[SimpleColorPickerView]的宽度大于高度, 左右margin = (width - height) / 2, 否则为0.
         */
        mSimpleColorPickerView.globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mBrightnessView.updateLayoutParams<LayoutParams> {
                        val margin = max(mSimpleColorPickerView.width - mSimpleColorPickerView.height, 0) / 2
                        leftMargin = margin
                        rightMargin = margin
                    }

                    mAlphaView.updateLayoutParams<LayoutParams> {
                        val margin = max(mSimpleColorPickerView.width - mSimpleColorPickerView.height, 0) / 2
                        leftMargin = margin
                        rightMargin = margin
                    }
                }

        mSimpleColorPickerView.colorChanges()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { color ->
                    mBrightnessView.updateColor(color)
                    mAlphaView.updateColor(color)
                }

        mBrightnessView.brightnessChanges()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { color ->
                    mSimpleColorPickerView.updateColor(color, false)
                    mAlphaView.updateColor(color)
                }

        mAlphaView.alphaChanges()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { color ->
                    mSimpleColorPickerView.updateColor(color, false)
                }
    }

    fun updateColor(color: Int) = mSimpleColorPickerView.updateColor(color)

    fun colorPicks(): Observable<Int> = mSimpleColorPickerView.colorPicks()
}