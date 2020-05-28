package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.util.RxUtil
import kotlin.math.min

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val PALETTE_STROKE_WIDTH = 32f
        private const val INNER_STROKE_WIDTH = 5f
    }

    private val mColors = intArrayOf(0xFFFF0000.toInt(), 0xFFFF00FF.toInt(), 0xFF0000FF.toInt(), 0xFF00FFFF.toInt(), 0xFF00FF00.toInt(),
            0xFFFFFF00.toInt(), 0xFFFF0000.toInt())

    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mRadius = 0f
    private var mInnerRadius = 0f
    private var mIsTracking = false
    private var mNeedHighlight = false

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.run {
            style = Paint.Style.STROKE
            strokeWidth = PALETTE_STROKE_WIDTH
        }

        mInnerPaint.run {
            style = Paint.Style.FILL
            color = Color.BLUE
            strokeWidth = INNER_STROKE_WIDTH
        }

        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mCenterX = width / 2f
                    mCenterY = height / 2f
                    mRadius = min(mCenterX, mCenterY) - PALETTE_STROKE_WIDTH / 2f
                    mInnerRadius = mRadius - 150f
                    mPaint.shader = SweepGradient(mCenterX, mCenterY, mColors, null)

                    invalidate()
                }

        touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    val x = event.x - mCenterX
                    val y = event.y - mCenterY
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                        }
                        MotionEvent.ACTION_MOVE -> {

                        }
                        MotionEvent.ACTION_UP -> {

                        }
                    }
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint)

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mInnerPaint)
    }
}