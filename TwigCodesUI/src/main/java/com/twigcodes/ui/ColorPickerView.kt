package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.util.RxUtil

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val mColors = intArrayOf(0xFFFF0000.toInt(), 0xFFFF00FF.toInt(), 0xFF0000FF.toInt(), 0xFF00FFFF.toInt(), 0xFF00FF00.toInt(),
            0xFFFFFF00.toInt(), 0xFFFF0000.toInt())
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.run {
            style = Paint.Style.STROKE
            strokeWidth = 32f

        }

        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mPaint.shader = SweepGradient(width / 2f, height / 2f, mColors, null)
                    invalidate()
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(width / 2f, height / 2f, width / 2f, mPaint)
    }
}