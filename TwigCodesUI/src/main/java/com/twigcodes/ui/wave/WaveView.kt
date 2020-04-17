package com.twigcodes.ui.wave

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.sin

class WaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_FRONT_WAVE_COLOR = Color.WHITE
        private const val DEFAULT_BACK_WAVE_COLOR = Color.WHITE
        private const val DEFAULT_WAVE_HEIGHT = 20
        private const val DEFAULT_PROGRESS = 100
        private const val DEFAULT_WAVE_LENGTH_MULTIPLE = 1.0f
        private const val DEFAULT_WAVE_HZ = 0.09f
    }

    private val mFrontWaveColor: Int
    private val mBackWaveColor: Int
    private val mWaveHeight: Int
    private val mWaveLengthMultiple: Float
    private val mWaveHz: Float
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
        mFrontWaveColor = a.getColor(R.styleable.WaveView_frontColor, DEFAULT_FRONT_WAVE_COLOR)
        mBackWaveColor = a.getColor(R.styleable.WaveView_backColor, DEFAULT_BACK_WAVE_COLOR)
        mWaveHeight = a.getDimensionPixelSize(R.styleable.WaveView_wave_height, DEFAULT_WAVE_HEIGHT)
        mWaveLengthMultiple = a.getFloat(R.styleable.WaveView_wave_length_multiple, DEFAULT_WAVE_LENGTH_MULTIPLE)
        mWaveHz = a.getFloat(R.styleable.WaveView_wave_hz, DEFAULT_WAVE_HZ)
        _progress = a.getInt(R.styleable.WaveView_progress, DEFAULT_PROGRESS)
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

        mWave = Wave(context).apply { config(mWaveLengthMultiple, mWaveHeight, mWaveHz, frontWavePaint, backWavePaint) }

        val foundation = Foundation(context).apply { config(frontWavePaint, backWavePaint) }
        val waveParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2)
        val foundationParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0).apply { weight = 1f }

        addView(mWave, waveParams)
        addView(foundation, foundationParams)

        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    progress = _progress
                }
    }

    fun startWave() = mWave.startWave()

    fun stopWave() = mWave.stopWave()

    /**
     * y=Asin(ωx+φ)+k
     */
    private class Wave(context: Context) : View(context, null, 0) {
        companion object {
            private const val X_INTERVAL = 20f
            private const val PI2 = 2 * Math.PI
        }

        private val mStopSubject = PublishSubject.create<Unit>()
        private val mFrontWavePath = Path()
        private val mBackWavePath = Path()
        private var mFrontWavePaint = Paint()
        private var mBackWavePaint = Paint()
        private var mWaveMultiple = 0f
        private var mWaveLength = 0f
        private var mWaveHeight = 0
        private var mWaveHz = 0f

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
                    .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                    .subscribe {
                        mWaveLength = width.toFloat() * mWaveMultiple
                        mRight = width.toFloat()
                        mBottom = height.toFloat()
                        omega = PI2 / mWaveLength

                        startWave()
                    }
        }

        fun config(waveMultiple: Float, waveHeight: Int, waveHz: Float, frontPaint: Paint, backPaint: Paint) {
            mWaveMultiple = waveMultiple
            mWaveHeight = waveHeight
            mWaveHz = waveHz
            mFrontWavePaint = frontPaint
            mBackWavePaint = backPaint
        }

        fun startWave() {
            Observable.interval(25, TimeUnit.MILLISECONDS)
                    .takeUntil(mStopSubject)
                    .compose(RxUtil.getSchedulerComposer())
                    .`as`(RxUtil.autoDispose(context as LifecycleOwner))
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
            mFrontPhi += mWaveHz
            if (mFrontPhi >= PI2)
                mFrontPhi -= PI2

            mBackPhi += mWaveHz
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
                    .`as`(RxUtil.autoDispose(context as LifecycleOwner))
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