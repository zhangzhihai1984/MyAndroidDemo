package com.twigcodes.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.util.RxUtil
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.max

class ScanView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val ANIMATION_DURATION = 2000L
        private const val CIRCLE_COUNT = 5
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRatios = FloatArray(CIRCLE_COUNT)
    private var mAnimator: ValueAnimator? = null
    private var mRadius = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f

    init {
        mAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = ANIMATION_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener {
                val value = animatedValue as Float

                mRatios.indices.forEach { i ->
                    var ratio = value - i * 1f / CIRCLE_COUNT
                    if (ratio < 0) {
                        /*
                         * 以CIRCLE_COUNT为5为例, index为0的circle的ratio为0, 那么index为1的circle的ratio为-0.2,
                         * 这个所谓的-0.2其实表达的含义就是它和前一个circle保持着20%的差距, 这就好像跑步, 前面的选手始终领先你20%的距离.
                         * 那么如何将这个-0.2转化为一个有效的数据呢, 毕竟ratio不能是负数嘛.
                         * 还是以跑步为例, 前面的选手此时ratio为0, 其实是要开始下一圈了,而你此时其实是处于上一圈的80%的位置, 差20%嘛.
                         * 所以你此时的ratio其实是-0.2 + 1 = 0.8.
                         *
                         * 这时需要考虑一种情况, 如果你此时的ratio为0, 说明你还没开始跑, 需要等前面的选手领先你20%之后再跑,
                         * 所以就不要转换为负数的ratio了, 继续保持ratio为0.
                         */
                        if (mRatios[i] > 0)
                            ratio += 1f
                        else
                            ratio = 0f
                    }

                    /*
                    * 每一个circle的ratio从0增至1后变不再变化, 看到的视觉效果就是5个circle依次从小变大, 这个过程只有一次, 没有循环
                    */
                    if (ratio > mRatios[i])
                        mRatios[i] = ratio
                }

                invalidate()
            }
        }

        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mRadius = max(width, height) / 2f
                    mCenterX = width / 2f
                    mCenterY = height / 2f
                    mPaint.shader = RadialGradient(mCenterX, mCenterY, mRadius, Color.parseColor("#1F2D49"), Color.parseColor("#4C77DA"), Shader.TileMode.CLAMP)

                    startScan()
                }
    }

    private fun startScan() {
        mAnimator?.run {
            start()

            /*
            * timer的值为从第一个circle开始变大, 直到最后一个circle变为最大的时间长度
            * 这个值分为两部分:
            * 1. 第一个circle开始变大直到最后一个circle开始变大的时间: 以CIRCLE_COUNT为5为例, 这个值就是 (5-1)/5个ANIMATION_DURATION的值
            * 2. 最后一个circle从最小到最大的时间: 也就是ANIMATION_DURATION的值
            */
            Observable.timer((ANIMATION_DURATION * (1 + (CIRCLE_COUNT - 1) * 1f / CIRCLE_COUNT)).toLong(), TimeUnit.MILLISECONDS)
                    .compose(RxUtil.getSchedulerComposer())
                    .`as`(RxUtil.autoDispose((context as LifecycleOwner)))
                    .subscribe { stopScan() }
        }
    }

    private fun stopScan() {
        mAnimator?.run {
            end()
            mRatios.indices.forEach { mRatios[it] = 0f }
            invalidate()

            Observable.timer(0, TimeUnit.MILLISECONDS)
                    .compose(RxUtil.getSchedulerComposer())
                    .`as`(RxUtil.autoDispose((context as LifecycleOwner)))
                    .subscribe { startScan() }
        }
    }

    fun destroy() {
        mAnimator?.run {
            end()
            mAnimator = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mRatios.forEach { ratio ->
            mPaint.alpha = (255 * (1 - ratio)).toInt()
            canvas.drawCircle(mCenterX, mCenterY, ratio * mRadius, mPaint)
        }
    }
}