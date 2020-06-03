package com.twigcodes.ui.colorpicker

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
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.subjects.PublishSubject
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
internal class SimpleColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private val COLORS = intArrayOf(
                0xFFFF0000.toInt(),
                0xFFFF00FF.toInt(),
                0xFF0000FF.toInt(),
                0xFF00FFFF.toInt(),
                0xFF00FF00.toInt(),
                0xFFFFFF00.toInt(),
                0xFFFF0000.toInt())
        private const val DEFAULT_PALETTE_STROKE_WIDTH = 32
        private const val DEFAULT_PALETTE_MARGIN_INNER = 150
        private const val DEFAULT_PICKER_HALO_WIDTH = 6
    }

    private val mPaletteMarginInner: Float
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mPaletteRadius = 0f
    private var mPickerRadius = 0f
    private var mDownInPicker = false
    private var mOpaquePickerHalo = false

    private val mPalettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val mPickerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val mPickerHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val mColorChangeSubject = PublishSubject.create<Int>()
    private val mColorPickSubject = PublishSubject.create<Int>()

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickerView, defStyleAttr, defStyleRes)

        mPalettePaint.run {
            strokeWidth = a.getDimensionPixelSize(R.styleable.ColorPickerView_paletteWidth, DEFAULT_PALETTE_STROKE_WIDTH).toFloat()
        }

        mPickerHaloPaint.run {
            strokeWidth = a.getDimensionPixelSize(R.styleable.ColorPickerView_pickerHaloWidth, DEFAULT_PICKER_HALO_WIDTH).toFloat()
        }

        mPaletteMarginInner = a.getDimensionPixelSize(R.styleable.ColorPickerView_paletteMarginInner, DEFAULT_PALETTE_MARGIN_INNER).toFloat()

        a.recycle()

        initView()
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mCenterX = width / 2f
                    mCenterY = height / 2f
                    mPaletteRadius = min(mCenterX, mCenterY) - mPalettePaint.strokeWidth / 2
                    mPickerRadius = mPaletteRadius - mPalettePaint.strokeWidth / 2 - mPaletteMarginInner
                    mPalettePaint.shader = SweepGradient(mCenterX, mCenterY, COLORS, null)

                    updateColor(COLORS[0])
                }

        /**
         * 我们将控件分为"颜色选取"区域(palette)和"颜色确认"区域(picker).
         * 当"down"发生时, 我们判断(x,y)落入的区域, 记录一下这个"起始"区域.
         *
         * 当"move"发生时:
         * (1) 如果"起始"区域为"确认"区域, 在"确认"区域外层绘制halo, 可以时刻提醒用户他当前的操作是在"确认"
         * 选取的颜色. 另外, 如果当前的区域不是"确认"区域的话, 也就是"滑出圈了", 那么绘制的halo会进行透明处理以提示用户.
         * (2) 如果"起始"区域为"选取"区域, 我们要根据(x,y)计算出对应的颜色同时更新"确认"区域的颜色.
         *
         * 当"up"发生时, 如果"起始"区域与当前区域均为"确认", 说明用户"确认"了选取的颜色同时将该颜色告知用户.
         *
         * 注: "down"中应该也有"move"处理(2)中的逻辑, 考虑到只有"down"和"up"的操作需要一定的技巧和偶然性, 姑且就让
         * 用户认为是自己刚刚"手抖"了吧.
         */
        touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    val x = event.x - mCenterX
                    val y = event.y - mCenterY
                    val inPicker = sqrt(x.pow(2) + y.pow(2)) <= mPickerRadius

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mDownInPicker = inPicker
                            if (mDownInPicker) {
                                mOpaquePickerHalo = true
                                invalidate()
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (mDownInPicker) {
                                mOpaquePickerHalo = inPicker
                                invalidate()
                            } else {
                                val color = makeColor((atan2(y, x) / (2 * Math.PI.toFloat())).run {
                                    if (this < 0) this + 1 else this
                                })

                                updateColor(color)
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if (mDownInPicker) {
                                if (inPicker)
                                    mColorPickSubject.onNext(mPickerPaint.color)

                                mDownInPicker = false
                                invalidate()
                            }
                        }
                    }
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val innerColor = mPickerPaint.color

        canvas.drawCircle(mCenterX, mCenterY, mPaletteRadius, mPalettePaint)
        canvas.drawCircle(mCenterX, mCenterY, mPickerRadius, mPickerPaint)

        if (mDownInPicker) {
            canvas.drawCircle(mCenterX, mCenterY, mPickerRadius + mPickerHaloPaint.strokeWidth, mPickerHaloPaint.apply {
                alpha = if (mOpaquePickerHalo) 0xFF else 0x80
            })

            mPickerHaloPaint.color = innerColor
        }
    }

    private fun makeColor(percentPosition: Float): Int =
            when {
                percentPosition <= 0 -> COLORS[0]
                percentPosition >= 1 -> COLORS[COLORS.size - 1]
                else -> {
                    val position: Int
                    val fraction: Float

                    (percentPosition * (COLORS.size - 1)).let { positionWithFraction ->
                        position = positionWithFraction.toInt()
                        fraction = positionWithFraction - position
                    }

                    val color1 = COLORS[position]
                    val color2 = COLORS[position + 1]
                    val a = mixColorComponent(Color.alpha(color1), Color.alpha(color2), fraction)
                    val r = mixColorComponent(Color.red(color1), Color.red(color2), fraction)
                    val g = mixColorComponent(Color.green(color1), Color.green(color2), fraction)
                    val b = mixColorComponent(Color.blue(color1), Color.blue(color2), fraction)
                    Color.argb(a, r, g, b)
                }
            }

    private fun mixColorComponent(component1: Int, component2: Int, fraction: Float): Int =
            ((1 - fraction) * component1 + fraction * component2).roundToInt()

    fun updateColor(color: Int, emit: Boolean = true) {
        mPickerPaint.color = color
        mPickerHaloPaint.color = color

        if (emit)
            mColorChangeSubject.onNext(color)

        invalidate()
    }

    fun colorChanges() = mColorChangeSubject

    fun colorPicks() = mColorPickSubject

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