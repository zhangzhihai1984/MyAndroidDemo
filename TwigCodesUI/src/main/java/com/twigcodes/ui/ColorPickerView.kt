package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.util.RxUtil
import kotlin.math.*

/**
 *      |
 *   3  |  4
 * ----------->
 *   2  |  1
 *      |
 *      ∨
 * 首先看一下上面的这个坐标系, [SweepGradient]会将color数组按照1-2-3-4象限顺时针分布.
 * 再来看一下touch时的坐标(event.x, event.y)与(centerX, centerY)差值的正负值, 也是符合这个坐标轴的,
 * 1-2-3-4象限分别为(+,+), (-,+), (-,-), (+,-).
 *
 * 接下来我们需要解决的就是如何将这个差值与color数组的索引对应上.
 * 单就(x,y)的值来说, 从某种程度上来说它是没有意义的, 因为它只体现了距离, 我们需要的是"角度".
 * 这里需要简单介绍一下"极坐标系", 它表示点的位置并不是(x, y), 而是(r, θ), r为极径, 可以理解为点P(x,y)到(0,0)的距离,
 * θ为极角, 可以理解为OX与OP的夹角.
 * [atan2]方法用于获取θ的值, 这个值其实就是arctan(y/x), 取值范围为(-π,π].
 * 1->2象限的取值为0->π, 3->4象限的取值为-π->0
 * 我们需要将这个取值"加工"一下:
 * (1) 除以2π, 1->2象限的取值变为0->0.5, 3->4象限的取值变为-0.5->0
 * (2) 如果为负数则加1, 1->2象限的取值变为0->0.5, 3->4象限的取值变为0.5->1
 * 这样1-2-3-4象限的取值变为0->1, 这与"color数组按照1-2-3-4象限顺时针分布"就对应上了, 于是我们就可以知道touch了
 * color数组的哪个位置了.
 *
 * 接下来我们需要解决的就是颜色的"生成".
 * 上面获得的值可以理解为是个百分比, 举个例子, 该值为0.4, color数组size为7, 那么0.4*(7-1)=2.4, 也就是当前的选中的颜色
 * 介于color[2]和color[3]之间, 这个颜色有color[2]60%的基因, 有color[3]40%的基因.
 */
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
                                val angle = atan2(y, x)
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