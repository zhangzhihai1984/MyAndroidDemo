package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.util.RxUtil
import kotlin.math.*

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
                    val inner = sqrt(x.pow(2) + y.pow(2)) <= mInnerRadius

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mIsTracking = inner
                            mNeedHighlight = true
                            invalidate()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (mIsTracking) {
                                if (mNeedHighlight != inner) {
                                    mNeedHighlight = inner
                                    invalidate()
                                }
                            } else {
                                val angle = atan2(y.toDouble(), x.toDouble()).toFloat()
                                // need to turn angle [-PI ... PI] into unit [0....1]
                                // need to turn angle [-PI ... PI] into unit [0....1]
                                var unit: Float = angle / (2 * Math.PI.toFloat())
                                if (unit < 0) {
                                    unit += 1f
                                }
                                Log.i("zzh", "angle:$angle x:$x y:$y unit:$unit")
                                mInnerPaint.color = interpColor(mColors, unit)
                                invalidate()
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if (mIsTracking) {
                                if (inner) {

                                }
                                mIsTracking = false
                                invalidate()
                            }
                        }
                    }
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val innerColor = mInnerPaint.color

        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint)
        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mInnerPaint)

        if (mIsTracking) {
            mInnerPaint.style = Paint.Style.STROKE
            mInnerPaint.alpha = if (mNeedHighlight) 0xFF else 0x80

            canvas.drawCircle(mCenterX, mCenterY, mInnerRadius + INNER_STROKE_WIDTH, mInnerPaint)

            mInnerPaint.style = Paint.Style.FILL
            mInnerPaint.color = innerColor
        }
    }

//    private fun floatToByte(x: Float): Int {
//        return Math.round(x)
//    }
//
//    private fun pinToByte(n: Int): Int {
//        var n = n
//        if (n < 0) {
//            n = 0
//        } else if (n > 255) {
//            n = 255
//        }
//        return n
//    }

    private fun ave(s: Int, d: Int, p: Float): Int {
        return s + (p * (d - s)).roundToInt()
    }

    private fun interpColor(colors: IntArray, unit: Float): Int {
        if (unit <= 0) {
            return colors[0]
        }
        if (unit >= 1) {
            return colors[colors.size - 1]
        }
        var p = unit * (colors.size - 1)
        val i = p.toInt()
        p -= i.toFloat()

        // now p is just the fractional part [0...1) and i is the index
        val c0 = colors[i]
        val c1 = colors[i + 1]
        val a = ave(Color.alpha(c0), Color.alpha(c1), p)
        val r = ave(Color.red(c0), Color.red(c1), p)
        val g = ave(Color.green(c0), Color.green(c1), p)
        val b = ave(Color.blue(c0), Color.blue(c1), p)
        return Color.argb(a, r, g, b)
    }

//    private fun rotateColor(color: Int, rad: Float): Int {
//        val deg = rad * 180 / 3.1415927f
//        val r = Color.red(color)
//        val g = Color.green(color)
//        val b = Color.blue(color)
//        val cm = ColorMatrix()
//        val tmp = ColorMatrix()
//        cm.setRGB2YUV()
//        tmp.setRotate(0, deg)
//        cm.postConcat(tmp)
//        tmp.setYUV2RGB()
//        cm.postConcat(tmp)
//        val a = cm.array
//        val ir = floatToByte(a[0] * r + a[1] * g + a[2] * b)
//        val ig = floatToByte(a[5] * r + a[6] * g + a[7] * b)
//        val ib = floatToByte(a[10] * r + a[11] * g + a[12] * b)
//        return Color.argb(Color.alpha(color), pinToByte(ir),
//                pinToByte(ig), pinToByte(ib))
//    }
}