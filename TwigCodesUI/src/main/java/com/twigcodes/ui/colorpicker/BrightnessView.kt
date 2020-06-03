package com.twigcodes.ui.colorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.subjects.PublishSubject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class BrightnessView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private val COLORS = intArrayOf(Color.WHITE, Color.BLACK, Color.BLACK)
        private const val DEFAULT_CORNER_RADIUS = 0
    }

    private val mCornerRadius: Float
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBrightnessChangeSubject = PublishSubject.create<Int>()

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickerView, defStyleAttr, defStyleRes)
        mCornerRadius = a.getDimensionPixelSize(R.styleable.ColorPickerView_brightnessCornerRadius, DEFAULT_CORNER_RADIUS).toFloat()
        a.recycle()
        initView()
    }

    private fun initView() {
        globalLayouts()
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    invalidate()
                }

        touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    val fraction = min(max(0f, (event.x / width)), 1f)

                    val color = if (fraction <= 0.5f)
                        makeColor(COLORS[0], COLORS[1], fraction * 2)
                    else
                        makeColor(COLORS[1], COLORS[2], fraction * 2 - 1)

                    mBrightnessChangeSubject.onNext(color)
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), mCornerRadius, mCornerRadius, mPaint)
    }

    private fun makeColor(color1: Int, color2: Int, fraction: Float): Int {
        val a = mixColorComponent(Color.alpha(color1), Color.alpha(color2), fraction)
        val r = mixColorComponent(Color.red(color1), Color.red(color2), fraction)
        val g = mixColorComponent(Color.green(color1), Color.green(color2), fraction)
        val b = mixColorComponent(Color.blue(color1), Color.blue(color2), fraction)

        return Color.argb(a, r, g, b)
    }

    private fun mixColorComponent(component1: Int, component2: Int, fraction: Float): Int =
            ((1 - fraction) * component1 + fraction * component2).roundToInt()

    fun updateColor(color: Int) {
        COLORS[1] = color
        mPaint.shader = LinearGradient(0f, 0f, width.toFloat(), 0f, COLORS, null, Shader.TileMode.CLAMP)
        invalidate()
    }

    fun brightnessChanges() = mBrightnessChangeSubject
}