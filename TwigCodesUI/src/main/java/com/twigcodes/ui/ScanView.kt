package com.twigcodes.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.LifecycleOwner
import com.twigcodes.ui.util.RxUtil.autoDispose
import com.twigcodes.ui.util.RxUtil.getSchedulerComposer
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ScanView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(mContext, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val WAVE_DURATION: Long = 2000L
        private const val WAVE_COUNT = 5
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRatios = FloatArray(WAVE_COUNT)
    private var mAnimator: ValueAnimator? = null
    private var mRadius = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f

    init {
        mAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = WAVE_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener {
                val value = animatedValue as Float

                for (i in mRatios.indices) {
                    var ratio = value - i * 1f / WAVE_COUNT
                    if (ratio < 0) {
                        /*
                         * 以WAVE_COUNT为5为例, index为0的circle的ratio为0, 那么index为1的circle的ratio为-0.2,
                         * 这个所谓的-0.2其实表达的含义就是它和前一个circle保持着20%的差距, 这就好像跑步, 前面的选手始终领先你20%的距离.
                         * 那么如何将这个-0.2转化为一个有效的数据呢, 毕竟ratio不能是负数嘛.
                         * 还是以跑步为例, 前面的选手此时ratio为0, 其实是要开始下一圈了,而你此时其实是处于上一圈的80%的位置, 差20%嘛.
                         * 所以你此时的ratio其实是-0.2 + 1 = 0.8.
                         *
                         * 这时需要考虑一种情况, 如果你此时的ratio为0, 说明你还没开始跑, 需要等前面的选手领先你20%之后再跑,
                         * 所以就不要转换为负数的ratio了, 继续保持ratio为0.
                         */
                        if (mRatios[i] > 0) ratio += 1f else ratio = 0f
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

        start()
    }

    private fun start() {
        if (null == mAnimator) return
        mAnimator!!.start()
        Observable.timer((WAVE_DURATION * (1 + (WAVE_COUNT - 1) * 1.0f / WAVE_COUNT)).toLong(), TimeUnit.MILLISECONDS)
                .compose(getSchedulerComposer())
                .`as`(autoDispose((mContext as LifecycleOwner)))
                .subscribe { v: Long? -> stop() }
    }

    private fun stop() {
        if (null == mAnimator) return
        mAnimator!!.end()
        for (i in mRatios.indices) {
            mRatios[i] = 0f
        }
        invalidate()
        Observable.timer(0, TimeUnit.MILLISECONDS)
                .compose(getSchedulerComposer())
                .`as`(autoDispose((mContext as LifecycleOwner)))
                .subscribe { v: Long? -> start() }
    }

    fun destroy() {
        if (null == mAnimator) return
        mAnimator!!.end()
        mAnimator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mRadius <= 0) {
            val width = width
            val height = height
            mRadius = Math.max(width, height) * 1.0f / 2
            mCenterX = getWidth() / 2f
            mCenterY = getHeight() / 2f
            mPaint.shader = RadialGradient(mCenterX, mCenterY, mRadius, Color.parseColor("#1F2D49"), Color.parseColor("#4C77DA"), Shader.TileMode.CLAMP)
        }
        for (ratio in mRatios) {
            mPaint.alpha = (255 * (1 - ratio)).toInt()
            canvas.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), ratio * mRadius, mPaint)
        }
    }
}