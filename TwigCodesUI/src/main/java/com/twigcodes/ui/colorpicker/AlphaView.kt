package com.twigcodes.ui.colorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class AlphaView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private val COLORS = intArrayOf(Color.TRANSPARENT, Color.BLACK)
        private const val DEFAULT_CORNER_RADIUS = 0
        private const val DEFAULT_ALPHA_MARGIN_BOTTOM = 6f
        private const val DEFAULT_INDICATOR_HEIGHT = 3f
    }

    private val mCornerRadius: Float
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mAlphaChangeSubject = PublishSubject.create<Int>()

    private var mAlphaMarginBottom = 0f
    private var mIndicatorHeight = 0f

    private var mPercent = 1f

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickerView, defStyleAttr, defStyleRes)
        mCornerRadius = a.getDimensionPixelSize(R.styleable.ColorPickerView_alphaCornerRadius, DEFAULT_CORNER_RADIUS).toFloat()
        val hasIndicator = a.getBoolean(R.styleable.ColorPickerView_hasAlphaIndicator, false)

        if (hasIndicator) {
            mAlphaMarginBottom = DEFAULT_ALPHA_MARGIN_BOTTOM
            mIndicatorHeight = DEFAULT_INDICATOR_HEIGHT
        }
        a.recycle()
        initView()
    }

    private fun initView() {
        globalLayouts()
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    invalidate()
                }

        touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    mPercent = min(max(0f, (event.x / width)), 1f)
                    mAlphaChangeSubject.onNext(makeColor())
                    invalidate()
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat() - mAlphaMarginBottom, mCornerRadius, mCornerRadius, mPaint)
        canvas.drawRect(0f, height.toFloat() - mIndicatorHeight, width.toFloat() * mPercent, height.toFloat(), mPaint)
    }

    private fun makeColor(): Int = Color.argb((255 * mPercent).roundToInt(), Color.red(COLORS[1]), Color.green(COLORS[1]), Color.blue(COLORS[1]))

    fun updateColor(color: Int) {
        COLORS[1] = color
        mPercent = 1f
        mPaint.shader = LinearGradient(0f, 0f, width.toFloat(), 0f, COLORS, null, Shader.TileMode.CLAMP)
        invalidate()
    }

    fun alphaChanges(): Observable<Int> = mAlphaChangeSubject
}