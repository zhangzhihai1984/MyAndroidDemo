package com.twigcodes.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.twigcodes.ui.util.RxUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class CircleWaveView extends View {
    private static final long WAVE_DURATION = 2000;
    private static final int WAVE_COUNT = 5;

    private final Context mContext;
    private final Paint mPaint = new Paint();
    private final float[] WAVE_RADIUS_RATIOS = new float[WAVE_COUNT];

    private ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1);

    private float WAVE_MAX_RADIUS;
    private int mCenterX;
    private int mCenterY;

    public CircleWaveView(Context context) {
        this(context, null);
    }

    public CircleWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircleWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;

        initView();
    }

    private void initView() {
        mPaint.setAntiAlias(true);

        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(WAVE_DURATION);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("zzh", "end " + System.currentTimeMillis());
            }
        });
        mAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            for (int i = 0; i < WAVE_RADIUS_RATIOS.length; i++) {
                float ratio = value - i * 1.0f / WAVE_COUNT;

                if (ratio < 0) {
                    /*
                     * 以WAVE_COUNT为5为例, index为0的circle的ratio为0, 那么index为1的circle的ratio为-0.2,
                     * 这个所谓的-0.22其实表达的含义就是它和前一个circle保持着20%的差距, 这就好像跑步, 前面的选手始终领先你20%的距离.
                     * 那么如何将这个-0.2转化为一个有效的数据呢, 毕竟ratio不能是负数嘛.
                     * 还是以跑步为例, 前面的选手此时ratio为0, 其实是要开始下一圈了,而你此时其实是处于上一圈的80%的位置, 差20%嘛.
                     * 所以你此时的ratio其实是-0.2 + 1 = 0.8.
                     *
                     * 这时需要考虑一种情况, 如果你此时的ratio为0, 说明你还没开始跑, 需要等前面的选手领先你20%之后再跑,
                     * 所以就不要转换为负数的ratio了, 继续保持ratio为0.
                     */
                    if (WAVE_RADIUS_RATIOS[i] > 0)
                        ratio += 1;
                    else
                        ratio = 0;
                }

                if (ratio > WAVE_RADIUS_RATIOS[i])
                    WAVE_RADIUS_RATIOS[i] = ratio;
            }

            invalidate();
        });

        start();
    }

    private void start() {
        if (null == mAnimator)
            return;

        mAnimator.start();

        Observable.timer((long) (WAVE_DURATION * (1 + (WAVE_COUNT - 1) * 1.0f / WAVE_COUNT)), TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose((LifecycleOwner) mContext))
                .subscribe(v -> stop());
    }

    private void stop() {
        if (null == mAnimator)
            return;

        mAnimator.end();

        for (int i = 0; i < WAVE_RADIUS_RATIOS.length; i++) {
            WAVE_RADIUS_RATIOS[i] = 0;
        }

        invalidate();

        Observable.timer(0, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose((LifecycleOwner) mContext))
                .subscribe(v -> start());
    }

    public void destroy() {
        if (null == mAnimator)
            return;

        mAnimator.end();
        mAnimator = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (WAVE_MAX_RADIUS <= 0) {
            int width = getWidth();
            int height = getHeight();

            WAVE_MAX_RADIUS = Math.max(width, height) * 1.0f / 2;
            mCenterX = getWidth() / 2;
            mCenterY = getHeight() / 2;
            mPaint.setShader(new RadialGradient(mCenterX, mCenterY, WAVE_MAX_RADIUS, Color.parseColor("#1F2D49"), Color.parseColor("#4C77DA"), Shader.TileMode.CLAMP));
        }

        for (float ratio : WAVE_RADIUS_RATIOS) {
            mPaint.setAlpha((int) (255 * (1 - ratio)));
            canvas.drawCircle(mCenterX, mCenterY, ratio * WAVE_MAX_RADIUS, mPaint);
        }
    }
}