package com.twigcodes.ui.wave

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.twigcodes.ui.R

class WaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_FRONT_WAVE_COLOR = Color.WHITE
        private const val DEFAULT_BACK_WAVE_COLOR = Color.WHITE
        private const val DEFAULT_FRONT_WAVE_ALPHA = 255
        private const val DEFAULT_BACK_WAVE_ALPHA = 255
        private const val DEFAULT_WAVE_HEIGHT = 20
        private const val DEFAULT_PROGRESS = 100
        private const val DEFAULT_WAVE_LENGTH_MULTIPLE = 1.0f
        private const val DEFAULT_WAVE_HZ = 0.09f
    }

    private val mFrontWaveColor: Int
    private val mBackWaveColor: Int
    private var mProgress: Int
    private val mWaveHeight: Int
    private val mWaveLengthMultiple: Float
    private val mWaveHz: Float
    private lateinit var mWave: Wave
    private lateinit var mSolid: Solid

    init {
        orientation = VERTICAL

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.WaveView, defStyleAttr, defStyleRes)
        mFrontWaveColor = a.getColor(R.styleable.WaveView_frontColor, DEFAULT_FRONT_WAVE_COLOR)
        mBackWaveColor = a.getColor(R.styleable.WaveView_backColor, DEFAULT_BACK_WAVE_COLOR)
        mWaveHeight = a.getDimensionPixelSize(R.styleable.WaveView_wave_height, DEFAULT_WAVE_HEIGHT)
        mWaveLengthMultiple = a.getFloat(R.styleable.WaveView_wave_length_multiple, DEFAULT_WAVE_LENGTH_MULTIPLE)
        mWaveHz = a.getFloat(R.styleable.WaveView_wave_hz, DEFAULT_WAVE_HZ)
        mProgress = a.getInt(R.styleable.WaveView_progress, DEFAULT_PROGRESS)
        a.recycle()

        initView()
        setProgress(mProgress)
    }

    private fun initView() {
        val frontWavePaint = Paint().apply {
            color = mFrontWaveColor
            alpha = DEFAULT_FRONT_WAVE_ALPHA
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val backWavePaint = Paint().apply {
            color = mBackWaveColor
            alpha = DEFAULT_BACK_WAVE_ALPHA
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        mWave = Wave(context).apply { config(mWaveLengthMultiple, mWaveHeight, mWaveHz, frontWavePaint, backWavePaint) }
        mSolid = Solid(context).apply { config(frontWavePaint, backWavePaint) }

        val waveParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2)
        val soldParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        soldParams.weight = 1f

        addView(mWave, waveParams)
        addView(mSolid, soldParams)
    }

    fun setProgress(progress: Int) {
        mProgress = if (progress > 100) 100 else progress
        computeWaveToTop()
    }

    fun startWave() {
        mWave.startWave()
    }

    fun stopWave() {
        mWave.stopWave()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            computeWaveToTop()
        }
    }

    private fun computeWaveToTop() {
        val params = mWave.layoutParams
        if (params != null) {
            (params as LayoutParams).topMargin = (height * (1f - mProgress / 100f)).toInt()
        }
        mWave.layoutParams = params
    }

    /**
     * y=Asin(ωx+φ)+k
     */
    private class Wave @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
        private val mFrontWavePath = Path()
        private val mBackWavePath = Path()
        private var mFrontWavePaint = Paint()
        private var mBackWavePaint = Paint()
        private var mWaveMultiple = 0f
        private var mWaveLength = 0f
        private var mWaveHeight = 0
        private var mMaxRight = 0f
        private var mWaveHz = 0f

        // wave animation
        private var mFrontOffset = 0.0f
        private var mBackOffset = (Math.PI * 0.5f).toFloat()
        private var mRefreshProgressRunnable: RefreshProgressRunnable? = null
        private var mLeft = 0
        private var mRight = 0
        private var mBottom = 0

        // ω
        private var omega = 0.0

        fun config(waveMultiple: Float, waveHeight: Int, waveHz: Float, frontPaint: Paint, backPaint: Paint) {
            mWaveMultiple = waveMultiple
            mWaveHeight = waveHeight
            mWaveHz = waveHz
            mFrontWavePaint = frontPaint
            mBackWavePaint = backPaint
        }

        fun startWave() {
            if (width != 0) {
                if (mWaveLength == 0f) {
                    mWaveLength = width * mWaveMultiple
                    mLeft = 0
                    mRight = width
                    mBottom = height
                    mMaxRight = mRight + Companion.X_SPACE
                    omega = Companion.PI2 / mWaveLength
                }
                if (null == mRefreshProgressRunnable) {
                    mRefreshProgressRunnable = RefreshProgressRunnable()
                } else {
                    removeCallbacks(mRefreshProgressRunnable)
                }
                postDelayed(mRefreshProgressRunnable, 100)
            }
        }

        fun stopWave() {
            removeCallbacks(mRefreshProgressRunnable)
        }

        /**
         * calculate wave track
         */
        private fun calculatePath() {
            mFrontWavePath.reset()
            mBackWavePath.reset()
            waveOffset
            var y: Float
            mFrontWavePath.moveTo(mLeft.toFloat(), mBottom.toFloat())
            run {
                var x = 0f
                while (x <= mMaxRight) {
                    y = (mWaveHeight * Math.sin(omega * x + mFrontOffset) + mWaveHeight).toFloat()
                    mFrontWavePath.lineTo(x, y)
                    x += Companion.X_SPACE
                }
            }
            mFrontWavePath.lineTo(mRight.toFloat(), mBottom.toFloat())
            mBackWavePath.moveTo(mLeft.toFloat(), mBottom.toFloat())
            var x = 0f
            while (x <= mMaxRight) {
                y = (mWaveHeight * Math.sin(omega * x + mBackOffset) + mWaveHeight).toFloat()
                mBackWavePath.lineTo(x, y)
                x += Companion.X_SPACE
            }
            mBackWavePath.lineTo(mRight.toFloat(), mBottom.toFloat())
        }

        private val waveOffset: Unit
            private get() {
                if (mBackOffset > Companion.PI2) {
                    mBackOffset = 0f
                } else {
                    mBackOffset += mWaveHz
                }
                if (mFrontOffset > Companion.PI2) {
                    mFrontOffset = 0f
                } else {
                    mFrontOffset += mWaveHz
                }
            }

        override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
            super.onWindowFocusChanged(hasWindowFocus)
            if (hasWindowFocus) {
                if (mWaveLength == 0f) {
                    startWave()
                }
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawPath(mBackWavePath, mBackWavePaint)
            canvas.drawPath(mFrontWavePath, mFrontWavePaint)
        }

        private inner class RefreshProgressRunnable : Runnable {
            override fun run() {
                synchronized(this) {
                    calculatePath()
                    invalidate()
                    postDelayed(this, 25)
                }
            }
        }

        companion object {
            private const val X_SPACE = 20f
            private const val PI2 = 2 * Math.PI
        }

        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    private class Solid @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
        private var mFrontWavePaint = Paint()
        private var mBackWavePaint = Paint()

        fun config(frontPaint: Paint, backPaint: Paint) {
            mFrontWavePaint = frontPaint
            mBackWavePaint = backPaint
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mBackWavePaint)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mFrontWavePaint)
        }
    }
}