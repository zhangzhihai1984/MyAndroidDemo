package com.twigcodes.ui.colorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.subjects.PublishSubject

internal class BrightnessView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
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
//                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    invalidate()
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), mCornerRadius, mCornerRadius, mPaint)
    }

    fun updateColor(color: Int) {
        mPaint.shader = LinearGradient(0f, 0f, width.toFloat(), 0f, intArrayOf(Color.WHITE, color, Color.BLACK), null, Shader.TileMode.CLAMP)
        invalidate()
    }

    fun brightnessChanges() = mBrightnessChangeSubject
}