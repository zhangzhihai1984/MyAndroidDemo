package com.twigcodes.ui.wave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twigcodes.ui.R;

public class WaveView extends LinearLayout {
    private static final int DEFAULT_FRONT_WAVE_COLOR = Color.WHITE;
    private static final int DEFAULT_BACK_WAVE_COLOR = Color.WHITE;
    private static final int DEFAULT_FRONT_WAVE_ALPHA = 255;
    private static final int DEFAULT_BACK_WAVE_ALPHA = 255;
    private static final int DEFAULT_WAVE_HEIGHT = 20;
    private static final int DEFAULT_PROGRESS = 100;
    private static final float DEFAULT_WAVE_LENGTH_MULTIPLE = 1.0f;
    private static final float DEFAULT_WAVE_HZ = 0.09f;

    private final int mFontWaveColor;
    private final int mBackWaveColor;
    private int mProgress;
    private final int mWaveHeight;
    private final float mWaveLengthMultiple;
    private final float mWaveHz;

    private Wave mWave;
    private Solid mSolid;

    private final Paint mFrontWavePaint = new Paint();
    private final Paint mBackWavePaint = new Paint();

    private final Context mContext;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;

        setOrientation(VERTICAL);

        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, defStyleAttr, defStyleRes);

        mFontWaveColor = a.getColor(R.styleable.WaveView_frontColor, DEFAULT_FRONT_WAVE_COLOR);
        mBackWaveColor = a.getColor(R.styleable.WaveView_backColor, DEFAULT_BACK_WAVE_COLOR);
        mWaveHeight = a.getDimensionPixelSize(R.styleable.WaveView_wave_height, DEFAULT_WAVE_HEIGHT);
        mWaveLengthMultiple = a.getFloat(R.styleable.WaveView_wave_length_multiple, DEFAULT_WAVE_LENGTH_MULTIPLE);
        mWaveHz = a.getFloat(R.styleable.WaveView_wave_hz, DEFAULT_WAVE_HZ);
        mProgress = a.getInt(R.styleable.WaveView_progress, DEFAULT_PROGRESS);

        a.recycle();

        initPaints();
        initViews();

        setProgress(mProgress);
    }

    private void initPaints() {
        mFrontWavePaint.setColor(mFontWaveColor);
        mFrontWavePaint.setAlpha(DEFAULT_FRONT_WAVE_ALPHA);
        mFrontWavePaint.setStyle(Paint.Style.FILL);
        mFrontWavePaint.setAntiAlias(true);

        mBackWavePaint.setColor(mBackWaveColor);
        mBackWavePaint.setAlpha(DEFAULT_BACK_WAVE_ALPHA);
        mBackWavePaint.setStyle(Paint.Style.FILL);
        mBackWavePaint.setAntiAlias(true);
    }

    private void initViews() {
        mWave = new Wave(mContext, null);
        mWave.setWaveSize(mWaveLengthMultiple, mWaveHeight, mWaveHz);
        mWave.setWavePaint(mFrontWavePaint, mBackWavePaint);

        mSolid = new Solid(mContext, null);
        mSolid.setWavePaint(mFrontWavePaint, mBackWavePaint);

        LinearLayout.LayoutParams waveParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2);
        LinearLayout.LayoutParams soldParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        soldParams.weight = 1;

        addView(mWave, waveParams);
        addView(mSolid, soldParams);
    }

    public void setProgress(int progress) {
        this.mProgress = progress > 100 ? 100 : progress;
        computeWaveToTop();
    }

    public void startWave() {
        mWave.startWave();
    }

    public void stopWave() {
        mWave.stopWave();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            computeWaveToTop();
        }
    }

    private void computeWaveToTop() {
        ViewGroup.LayoutParams params = mWave.getLayoutParams();
        if (params != null) {
            ((LayoutParams) params).topMargin = (int) (getHeight() * (1f - mProgress / 100f));
        }
        mWave.setLayoutParams(params);
    }

    /**
     * y=Asin(ωx+φ)+k
     */
    private class Wave extends View {
        private static final float X_SPACE = 20;
        private static final double PI2 = 2 * Math.PI;

        private final Path mFrontWavePath = new Path();
        private final Path mBackWavePath = new Path();

        private Paint mFrontWavePaint = new Paint();
        private Paint mBackWavePaint = new Paint();

        private float mWaveMultiple;
        private float mWaveLength;
        private int mWaveHeight;
        private float mMaxRight;
        private float mWaveHz;

        // wave animation
        private float mFrontOffset = 0.0f;
        private float mBackOffset = (float) (Math.PI * 0.5f);

        private RefreshProgressRunnable mRefreshProgressRunnable;

        private int left, right, bottom;
        // ω
        private double omega;

        public Wave(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Wave(Context context, @Nullable AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void setWaveSize(float waveMultiple, int waveHeight, float waveHz) {
            mWaveMultiple = waveMultiple;
            mWaveHeight = waveHeight;
            mWaveHz = waveHz;
        }

        void setWavePaint(Paint frontPaint, Paint backPaint) {
            mFrontWavePaint = frontPaint;
            mBackWavePaint = backPaint;
        }

        void startWave() {
            if (getWidth() != 0) {
                if (mWaveLength == 0) {
                    mWaveLength = getWidth() * mWaveMultiple;
                    left = 0;
                    right = getWidth();
                    bottom = getHeight();
                    mMaxRight = right + X_SPACE;
                    omega = PI2 / mWaveLength;
                }

                if (null == mRefreshProgressRunnable) {
                    mRefreshProgressRunnable = new RefreshProgressRunnable();
                } else {
                    removeCallbacks(mRefreshProgressRunnable);
                }

                postDelayed(mRefreshProgressRunnable, 100);
            }
        }

        void stopWave() {
            removeCallbacks(mRefreshProgressRunnable);
        }

        /**
         * calculate wave track
         */
        private void calculatePath() {
            mFrontWavePath.reset();
            mBackWavePath.reset();

            getWaveOffset();

            float y;
            mFrontWavePath.moveTo(left, bottom);
            for (float x = 0; x <= mMaxRight; x += X_SPACE) {
                y = (float) (mWaveHeight * Math.sin(omega * x + mFrontOffset) + mWaveHeight);
                mFrontWavePath.lineTo(x, y);
            }
            mFrontWavePath.lineTo(right, bottom);

            mBackWavePath.moveTo(left, bottom);
            for (float x = 0; x <= mMaxRight; x += X_SPACE) {
                y = (float) (mWaveHeight * Math.sin(omega * x + mBackOffset) + mWaveHeight);
                mBackWavePath.lineTo(x, y);
            }
            mBackWavePath.lineTo(right, bottom);
        }

        private void getWaveOffset() {
            if (mBackOffset > PI2) {
                mBackOffset = 0;
            } else {
                mBackOffset += mWaveHz;
            }

            if (mFrontOffset > PI2) {
                mFrontOffset = 0;
            } else {
                mFrontOffset += mWaveHz;
            }
        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus) {
                if (mWaveLength == 0) {
                    startWave();
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawPath(mBackWavePath, mBackWavePaint);
            canvas.drawPath(mFrontWavePath, mFrontWavePaint);
        }

        private class RefreshProgressRunnable implements Runnable {
            public void run() {
                synchronized (this) {
                    calculatePath();

                    invalidate();

                    postDelayed(this, 25);
                }
            }
        }
    }

    private class Solid extends View {
        private Paint mFrontWavePaint = new Paint();
        private Paint mBackWavePaint = new Paint();

        public Solid(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        void setWavePaint(Paint frontPaint, Paint backPaint) {
            mFrontWavePaint = frontPaint;
            mBackWavePaint = backPaint;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(0, 0, getWidth(), getHeight(), mBackWavePaint);
            canvas.drawRect(0, 0, getWidth(), getHeight(), mFrontWavePaint);
        }
    }
}
