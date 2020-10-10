package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.sin

class WaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_FRONT_WAVE_COLOR = Color.WHITE
        private const val DEFAULT_BACK_WAVE_COLOR = Color.WHITE
        private const val DEFAULT_PROGRESS = 100
        private const val DEFAULT_WAVE_HEIGHT = 20
        private const val DEFAULT_WAVE_LENGTH_MULTIPLE = 1.0f
        private const val DEFAULT_WAVE_DURATION = 2000
    }

    private val mFrontWaveColor: Int
    private val mBackWaveColor: Int

    /**
     * 对于sinx来说, 它的取值范围为[-1,1], 对于Asinx来说, 它的取值范围为[-A,A], 这个A就是wave的高度, 那么[Wave]的高度为2A.
     */
    private val mWaveHeight: Int

    /**
     * 对于sinx来说, 一个周期的长度为2π, 对于sin0.5x来说, 一个周期的长度为4π, 那么对于sinωx来说, 一个周期的长度为2π/ω
     * 但是, 如果让用户来对ω进行赋值会是个比较痛疼的事情, 因为它不是一个很直观的长度或系数, 他们更关心的应该是一个周期的长度是多少,
     * 或者干脆一点, [WaveView]的宽度内显示几个周期, 比如说我想要两个周期的效果, 那么这个参数就传2.
     *
     * ω = 2π/waveLength = 2π/(width/multiple) = 2π*multiple/width
     */
    private val mWaveLengthMultiple: Float

    /**
     * sin(x+φ)中φ的取值范围为[0,2π), 如果我们动态调整φ的值, 那么每个点的值便可以在[-1,1]之间变化, 这样整个曲线便可产生类似波浪的动态效果.
     * 这个参数的目的就是让用户设置波浪上上下下这样一个来回的时间是多少.
     *
     * Δφ*period = 2π
     */
    private val mWaveDuration: Int
    private var _progress: Int
    private lateinit var mWave: Wave

    var progress: Int
        get() = _progress
        set(value) {
            _progress = min(value, 100)
            mWave.updateLayoutParams<MarginLayoutParams> { topMargin = (getHeight() * (1 - _progress / 100f)).toInt() }
        }

    init {
        orientation = VERTICAL
        setPadding(0)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.WaveView, defStyleAttr, defStyleRes)
        mFrontWaveColor = a.getColor(R.styleable.WaveView_waveFrontColor, DEFAULT_FRONT_WAVE_COLOR)
        mBackWaveColor = a.getColor(R.styleable.WaveView_waveBackColor, DEFAULT_BACK_WAVE_COLOR)
        mWaveHeight = a.getDimensionPixelSize(R.styleable.WaveView_waveHeight, DEFAULT_WAVE_HEIGHT)
        mWaveLengthMultiple = a.getFloat(R.styleable.WaveView_waveLengthMultiple, DEFAULT_WAVE_LENGTH_MULTIPLE)
        mWaveDuration = a.getInt(R.styleable.WaveView_waveDuration, DEFAULT_WAVE_DURATION)
        _progress = a.getInt(R.styleable.WaveView_waveProgress, DEFAULT_PROGRESS)
        a.recycle()

        initView()
    }

    private fun initView() {
        val frontWavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mFrontWaveColor
            style = Paint.Style.FILL
        }

        val backWavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mBackWaveColor
            style = Paint.Style.FILL
        }

        mWave = Wave(context).apply { config(mWaveLengthMultiple, mWaveHeight, mWaveDuration, frontWavePaint, backWavePaint) }

        val foundation = Foundation(context).apply { config(frontWavePaint, backWavePaint) }
        val waveParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2)
        val foundationParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0).apply { weight = 1f }

        addView(mWave, waveParams)
        addView(foundation, foundationParams)

        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    progress = _progress
                }
    }

    fun startWave() = mWave.startWave()

    fun stopWave() = mWave.stopWave()

    /**
     * y=Asin(ωx+φ)+k
     *
     * A=waveHeight
     * ω=2π*multiple/width
     * φ+=Δφ  1. φ∈[0,2π) 2. φ*period=2π
     * k=waveHeight
     */
    private class Wave(context: Context) : View(context, null, 0) {
        companion object {
            private const val X_INTERVAL = 20f
            private const val PI2 = 2 * Math.PI
            private const val DELTA_PHI_PERIOD = 25L
        }

        private val mStopSubject = PublishSubject.create<Unit>()
        private val mFrontWavePath = Path()
        private val mBackWavePath = Path()
        private var mFrontWavePaint = Paint()
        private var mBackWavePaint = Paint()
        private var mWaveLengthMultiple = 0f
        private var mWaveHeight = 0
        private var mWaveDeltaPhi = 0.0

        private val mLeft = 0f
        private var mRight = 0f
        private var mBottom = 0f

        // ω
        private var omega = 0.0

        //φ
        private var mFrontPhi = 0.0
        private var mBackPhi = Math.PI * 0.5

        init {
//            setLayerType(LAYER_TYPE_SOFTWARE, null)

            globalLayouts()
                    .take(1)
                    .to(RxUtil.autoDispose(context as LifecycleOwner))
                    .subscribe {
                        mRight = width.toFloat()
                        mBottom = height.toFloat()
                        omega = PI2 * mWaveLengthMultiple / width.toFloat()

                        startWave()
                    }
        }

        fun config(waveMultiple: Float, waveHeight: Int, waveDuration: Int, frontPaint: Paint, backPaint: Paint) {
            mWaveLengthMultiple = waveMultiple
            mWaveHeight = waveHeight
            mWaveDeltaPhi = PI2 / (waveDuration.toDouble() / DELTA_PHI_PERIOD)
            mFrontWavePaint = frontPaint
            mBackWavePaint = backPaint
        }

        fun startWave() {
            Observable.interval(DELTA_PHI_PERIOD, TimeUnit.MILLISECONDS)
                    .takeUntil(mStopSubject)
                    .compose(RxUtil.getSchedulerComposer())
                    .to(RxUtil.autoDispose(context as LifecycleOwner))
                    .subscribe {
                        updatePhi()
                        updatePath(mFrontWavePath, mFrontPhi)
                        updatePath(mBackWavePath, mBackPhi)
                        postInvalidate()
                    }
        }

        fun stopWave() {
            mStopSubject.onNext(Unit)
        }

        /**
         * φ取值范围为[0, 2π)
         */
        private fun updatePhi() {
            mFrontPhi += mWaveDeltaPhi
            if (mFrontPhi >= PI2)
                mFrontPhi -= PI2

            mBackPhi += mWaveDeltaPhi
            if (mBackPhi >= PI2)
                mBackPhi -= PI2
        }

        /**
         * 1. 清空
         * 2. 以左下角为起点开始打点
         * 3. x的取值范围为[0, width), 以[X_INTERVAL]作为取点的间隔, 逐个进行打点形成sin曲线
         * 4. x+[X_INTERVAL]的值可能为>=width, 因此再打一个x为width的点作为sin曲线的结点
         * 5. 以右下角为终点打最后一个点
         */
        private fun updatePath(path: Path, phi: Double) {
            path.reset()
            path.moveTo(mLeft, mBottom)

            var x = 0f

            while (x < mRight) {
                path.lineTo(x, getPathY(x, phi))
                x += X_INTERVAL
            }

            path.lineTo(mRight, getPathY(mRight, phi))
            path.lineTo(mRight, mBottom)
        }

        private fun getPathY(x: Float, phi: Double) = (mWaveHeight * sin(omega * x + phi) + mWaveHeight).toFloat()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawPath(mBackWavePath, mBackWavePaint)
            canvas.drawPath(mFrontWavePath, mFrontWavePaint)
        }
    }

    private class Foundation(context: Context) : View(context, null, 0) {
        private var mRect: Rect? = null
        private var mFrontWavePaint = Paint()
        private var mBackWavePaint = Paint()

        init {
            globalLayouts()
                    .take(1)
                    .to(RxUtil.autoDispose(context as LifecycleOwner))
                    .subscribe { mRect = Rect(0, 0, width, height) }
        }

        fun config(frontPaint: Paint, backPaint: Paint) {
            mFrontWavePaint = frontPaint
            mBackWavePaint = backPaint
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            mRect?.run {
                canvas.drawRect(this, mBackWavePaint)
                canvas.drawRect(this, mFrontWavePaint)
            }
        }
    }
}