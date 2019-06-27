package com.twigcodes.ui;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.twigcodes.ui.util.RxUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import kotlin.Unit;

public class ChartView extends View {
    private static final int X_AXIS_SERIES_COUNT = 13; //TODO:
    private static final float X_AXIS_MARGIN_START = 65;
    private static final float X_AXIS_MARGIN_END = 65;
    private static final float X_AXIS_TEXT_BASELINE_MARGIN_BOTTOM = 48;
    private float X_AXIS_TICK_INTERVAL = 0;

    private static final int Y_AXIS_SERIES_COUNT = 5;
    private static final float Y_AXIS_MARGIN_TOP = 84;
    private static final float Y_AXIS_MARGIN_BOTTOM = 0;
    private static final int Y_AXIS_MAX_VALUE = 30;
    private static final int Y_AXIS_MIN_VALUE = 5;
    private static final float Y_AXIS_TICK_LENGTH = 24;
    private float Y_AXIS_TICK_INTERVAL = 0;
    private float Y_AXIS_TEXT_BASELINE_OFFSET = 0;

    private static final float AXIS_LABEL_SIZE = 27;
    private static final float AXIS_LABEL_SELECTED_SIZE = 48;
    private static final float AXIS_TICK_WIDTH = 3;

    private static final float DATA_LINE_STROKE_WIDTH = 15;
    private static final float DATA_LINE_PATH_CORNER = 12;
    private static final float DATA_LINE_SHADOW_OFFSET = 30;
    private static final float MARKER_CIRCLE_RADIUS = 56;
    private static final float MARKER_BORDER_WIDTH = 6;
    private static final float MARKER_INNER_RADIUS = 21;
    private static final float TOOLTIP_CORNER_RADIUS = 24;
    private static final float TOOLTIP_WIDTH = 171;
    private static final float TOOLTIP_HEIGHT = 108;
    private static final float TOOLTIP_CENTER_MARGIN_TO_MARKER = 219;
    private float TOOLTIP_TEXT_BASELINE_OFFSET = 0;

    private final Paint mXAxisTickPaint = new Paint();
    private final Paint mYAxisTickPaint = new Paint();
    private final Paint mAxisTextPaint = new Paint();
    private final Paint mAxisSelectedTextPaint = new Paint();
    private final Paint mDataSolidPaint = new Paint();
    private final Paint mDataDashPaint = new Paint();
    private final Paint mDataShadowPaint = new Paint();
    private final Paint mMarkerBackgroundPaint = new Paint();
    private final Paint mMarkerBorderPaint = new Paint();
    private final Paint mMarkerInnerPaint = new Paint();
    private final Paint mTooltipBackgroundPaint = new Paint();
    private final Paint mTooltipBorderPaint = new Paint();
    private final Paint mTooltipTextPaint = new Paint();

    private final Path mDataSolidPath = new Path();
    private final Path mDataSolidShadowPath = new Path();
    private final Path mDataDashPath = new Path();
    private final Path mDataDashShadowPath = new Path();

    private int mWidth;
    private int mHeight;

    private List<Float> mSolidData = new ArrayList<>();
    private List<Float> mDashData = new ArrayList<>();

    private int mSelectedDataIndex = -1;

    private float mTouchDownY;
    private float mTouchDownData;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.i("zzh", "getWidth " + getWidth());
        Log.i("zzh", "getMeasuredWidth " + getMeasuredWidth());

        mWidth = getWidth();
        mHeight = getHeight();

        X_AXIS_TICK_INTERVAL = (mWidth - X_AXIS_MARGIN_START - X_AXIS_MARGIN_END) / X_AXIS_SERIES_COUNT;
        Y_AXIS_TICK_INTERVAL = (mHeight - Y_AXIS_MARGIN_TOP - Y_AXIS_MARGIN_BOTTOM) / Y_AXIS_SERIES_COUNT;
        Log.i("zzh", "Y AXIS GAP: " + X_AXIS_TICK_INTERVAL);

        mYAxisTickPaint.setColor(Color.WHITE);
        mYAxisTickPaint.setStrokeWidth(AXIS_TICK_WIDTH);
        mYAxisTickPaint.setShader(new LinearGradient(0, 0, 0, mHeight, Color.TRANSPARENT, Color.parseColor("#4CFFFFFF"), Shader.TileMode.CLAMP));

        mDataSolidPaint.setShader(new LinearGradient(0, 0, getWidth(), 0, Color.parseColor("#66F8DB"), Color.parseColor("#E8FF7B"), Shader.TileMode.CLAMP));

        makeDataPath();
        invalidate();
    }

    private void initView() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mAxisTextPaint.setAntiAlias(true);
        mAxisTextPaint.setColor(Color.WHITE);
        mAxisTextPaint.setTextAlign(Paint.Align.CENTER);
        mAxisTextPaint.setTextSize(AXIS_LABEL_SIZE);
        mAxisTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Regular.otf"));

        Y_AXIS_TEXT_BASELINE_OFFSET = -mAxisTextPaint.getFontMetrics().top / 2 - mAxisTextPaint.getFontMetrics().bottom / 2;

        mAxisSelectedTextPaint.setAntiAlias(true);
        mAxisSelectedTextPaint.setColor(Color.WHITE);
        mAxisSelectedTextPaint.setTextAlign(Paint.Align.CENTER);
        mAxisSelectedTextPaint.setTextSize(AXIS_LABEL_SELECTED_SIZE);
        mAxisSelectedTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Bold.ttf"));

        mXAxisTickPaint.setColor(Color.WHITE);
        mXAxisTickPaint.setStrokeWidth(AXIS_TICK_WIDTH);

        mDataSolidPaint.setAntiAlias(true);
        mDataSolidPaint.setStrokeWidth(DATA_LINE_STROKE_WIDTH);
        mDataSolidPaint.setPathEffect(new CornerPathEffect(DATA_LINE_PATH_CORNER));
        mDataSolidPaint.setStyle(Paint.Style.STROKE);
        mDataSolidPaint.setStrokeCap(Paint.Cap.ROUND);

        mDataShadowPaint.setAntiAlias(true);
        mDataShadowPaint.setColor(Color.parseColor("#417ACC"));
        mDataShadowPaint.setStrokeWidth(DATA_LINE_STROKE_WIDTH);
        mDataShadowPaint.setPathEffect(new CornerPathEffect(DATA_LINE_PATH_CORNER));
        mDataShadowPaint.setStyle(Paint.Style.STROKE);
        mDataShadowPaint.setStrokeCap(Paint.Cap.ROUND);
        mDataShadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));

        mDataDashPaint.setAntiAlias(true);
        mDataDashPaint.setStrokeWidth(DATA_LINE_STROKE_WIDTH);
        mDataDashPaint.setColor(Color.parseColor("#3765B5"));
        mDataDashPaint.setStyle(Paint.Style.STROKE);
        mDataDashPaint.setStrokeCap(Paint.Cap.ROUND);
        mDataDashPaint.setPathEffect(new ComposePathEffect(new DashPathEffect(new float[]{30, 25}, 0), new CornerPathEffect(DATA_LINE_PATH_CORNER)));

        mMarkerBackgroundPaint.setAntiAlias(true);
        mMarkerBackgroundPaint.setColor(Color.parseColor("#66000000"));
        mMarkerBackgroundPaint.setStyle(Paint.Style.FILL);

        mMarkerBorderPaint.setAntiAlias(true);
        mMarkerBorderPaint.setColor(Color.WHITE);
        mMarkerBorderPaint.setStyle(Paint.Style.STROKE);
        mMarkerBorderPaint.setStrokeWidth(MARKER_BORDER_WIDTH);

        mMarkerInnerPaint.setAntiAlias(true);
        mMarkerInnerPaint.setColor(Color.WHITE);
        mMarkerInnerPaint.setStyle(Paint.Style.FILL);

        mTooltipBackgroundPaint.setAntiAlias(true);
        mTooltipBackgroundPaint.setColor(Color.parseColor("#66000000"));
        mTooltipBackgroundPaint.setStyle(Paint.Style.FILL);

        mTooltipBorderPaint.setAntiAlias(true);
        mTooltipBorderPaint.setColor(Color.WHITE);
        mTooltipBorderPaint.setStyle(Paint.Style.STROKE);
        mTooltipBorderPaint.setStrokeWidth(MARKER_BORDER_WIDTH);

        mTooltipTextPaint.setAntiAlias(true);
        mTooltipTextPaint.setColor(Color.WHITE);
        mTooltipTextPaint.setTextAlign(Paint.Align.CENTER);
        mTooltipTextPaint.setTextSize(AXIS_LABEL_SELECTED_SIZE);
        mTooltipTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Bold.ttf"));

        TOOLTIP_TEXT_BASELINE_OFFSET = -mTooltipTextPaint.getFontMetrics().top / 2 - mTooltipTextPaint.getFontMetrics().bottom / 2;

        //TODO: Mock Data
        mSolidData.add(25.0f);
        mSolidData.add(23.0f);
        mSolidData.add(22.0f);
        mSolidData.add(20.0f);
        mSolidData.add(25.0f);
        mSolidData.add(25.0f);
        mSolidData.add(25.0f);
        mSolidData.add(20.0f);
        mSolidData.add(18.0f);
        mSolidData.add(30.0f);
        mSolidData.add(28.0f);
        mSolidData.add(27.0f);
        mSolidData.add(26.0f);

        mDashData.add(20.0f);
        mDashData.add(18.0f);
        mDashData.add(17.0f);
        mDashData.add(15.0f);
        mDashData.add(20.0f);
        mDashData.add(20.0f);
        mDashData.add(20.0f);
        mDashData.add(15.0f);
        mDashData.add(13.0f);
        mDashData.add(25.0f);
        mDashData.add(23.0f);
        mDashData.add(22.0f);
        mDashData.add(21.0f);
    }

    private PublishSubject<Unit> mTouchDownSubject = PublishSubject.create();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mTouchDownY = event.getY();
                mTouchDownSubject.onNext(Unit.INSTANCE);

                float x = event.getX();

                if (x < X_AXIS_MARGIN_START || x > mWidth - X_AXIS_MARGIN_START) {
                    mSelectedDataIndex = -1;
                } else {
                    mSelectedDataIndex = (int) Math.floor((x - X_AXIS_MARGIN_START) / X_AXIS_TICK_INTERVAL);
                    mTouchDownData = mSolidData.get(mSelectedDataIndex);
                }

                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mSelectedDataIndex >= 0) {
                    float y = event.getY();

                    float rawDeltaData = (mTouchDownY - y) / Y_AXIS_TICK_INTERVAL * 5;
                    float deltaData = (float) Math.floor(rawDeltaData);
                    if (rawDeltaData - deltaData > 0.5)
                        deltaData += 0.5;

                    float data = mTouchDownData + deltaData;

                    mSolidData.set(mSelectedDataIndex, Math.min(Y_AXIS_MAX_VALUE, Math.max(data, Y_AXIS_MIN_VALUE)));
                    makeDataPath();
                }

                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                mTouchDownY = 0;

                Observable.timer(1000, TimeUnit.MILLISECONDS)
                        .takeUntil(mTouchDownSubject)
                        .as(RxUtil.autoDispose((LifecycleOwner) getContext()))
                        .subscribe(v -> {
                            mSelectedDataIndex = -1;
                            invalidate();
                        });

                break;
            }
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWidth <= 0)
            return;

        drawAxis(canvas);
        drawData(canvas);
        drawMarkerAndTooltip(canvas);
    }

    private void makeDataPath() {
        mDataDashPath.reset();
        mDataDashShadowPath.reset();
        mDataSolidPath.reset();
        mDataSolidShadowPath.reset();

        for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
            float x = (i + 0.5f) * X_AXIS_TICK_INTERVAL + X_AXIS_MARGIN_START;
            float y = Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - mDashData.get(i)) * Y_AXIS_TICK_INTERVAL * 1.0f / 5;
            if (i <= 0) {
                mDataDashPath.moveTo(x, y);
                mDataDashShadowPath.moveTo(x, y + DATA_LINE_SHADOW_OFFSET);
            } else {
                mDataDashPath.lineTo(x, y);
                mDataDashShadowPath.lineTo(x, y + DATA_LINE_SHADOW_OFFSET);
            }
        }

        for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
            float x = (i + 0.5f) * X_AXIS_TICK_INTERVAL + X_AXIS_MARGIN_START;
            float y = Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - mSolidData.get(i)) * Y_AXIS_TICK_INTERVAL * 1.0f / 5;
            if (i <= 0) {
                mDataSolidPath.moveTo(x, y);
                mDataSolidShadowPath.moveTo(x, y + DATA_LINE_SHADOW_OFFSET);
            } else {
                mDataSolidPath.lineTo(x, y);
                mDataSolidShadowPath.lineTo(x, y + DATA_LINE_SHADOW_OFFSET);
            }
        }
    }

    private void drawAxis(Canvas canvas) {
        for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
            if (i > 0)
                canvas.drawLine(i * X_AXIS_TICK_INTERVAL + X_AXIS_MARGIN_START, 0, i * X_AXIS_TICK_INTERVAL + X_AXIS_MARGIN_START, mHeight, mYAxisTickPaint);
            canvas.drawText(String.valueOf(i * 2), (i + 0.5f) * X_AXIS_TICK_INTERVAL + X_AXIS_MARGIN_START, mHeight - X_AXIS_TEXT_BASELINE_MARGIN_BOTTOM, i == mSelectedDataIndex ? mAxisSelectedTextPaint : mAxisTextPaint);
        }

        for (int i = 0; i < Y_AXIS_SERIES_COUNT; i++) {
            canvas.drawLine(0, i * Y_AXIS_TICK_INTERVAL + Y_AXIS_MARGIN_TOP, Y_AXIS_TICK_LENGTH, i * Y_AXIS_TICK_INTERVAL + Y_AXIS_MARGIN_TOP, mXAxisTickPaint);
            canvas.drawText(String.valueOf(Y_AXIS_MAX_VALUE - i * 5), 50, i * Y_AXIS_TICK_INTERVAL + Y_AXIS_MARGIN_TOP + Y_AXIS_TEXT_BASELINE_OFFSET, mAxisTextPaint);
        }
    }

    private void drawData(Canvas canvas) {
        canvas.drawPath(mDataDashShadowPath, mDataShadowPaint);
        canvas.drawPath(mDataDashPath, mDataDashPaint);

        canvas.drawPath(mDataSolidShadowPath, mDataShadowPaint);
        canvas.drawPath(mDataSolidPath, mDataSolidPaint);
    }

    private void drawMarkerAndTooltip(Canvas canvas) {
        if (mSelectedDataIndex >= 0) {
            float x = (mSelectedDataIndex + 0.5f) * X_AXIS_TICK_INTERVAL + X_AXIS_MARGIN_START;
            float y = Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - mSolidData.get(mSelectedDataIndex)) * Y_AXIS_TICK_INTERVAL * 1.0f / 5;
            canvas.drawCircle(x, y, MARKER_CIRCLE_RADIUS, mMarkerBackgroundPaint);
            canvas.drawCircle(x, y, MARKER_CIRCLE_RADIUS, mMarkerBorderPaint);
            canvas.drawCircle(x, y, MARKER_INNER_RADIUS, mMarkerInnerPaint);

            float rectLeft = x - TOOLTIP_CENTER_MARGIN_TO_MARKER - TOOLTIP_WIDTH / 2;
            float rectRight = x - TOOLTIP_CENTER_MARGIN_TO_MARKER + TOOLTIP_WIDTH / 2;
            float valueTextX = x - TOOLTIP_CENTER_MARGIN_TO_MARKER;

            if (rectLeft <= X_AXIS_MARGIN_START) {
                rectLeft = x + TOOLTIP_CENTER_MARGIN_TO_MARKER - TOOLTIP_WIDTH / 2;
                rectRight = x + TOOLTIP_CENTER_MARGIN_TO_MARKER + TOOLTIP_WIDTH / 2;
                valueTextX = x + TOOLTIP_CENTER_MARGIN_TO_MARKER;
            }

            float rectTop = y - TOOLTIP_HEIGHT / 2;
            float rectBottom = y + TOOLTIP_HEIGHT / 2;

            canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS, mTooltipBackgroundPaint);
            canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS, mTooltipBorderPaint);
            canvas.drawText(mSolidData.get(mSelectedDataIndex) + "℃", valueTextX, y + TOOLTIP_TEXT_BASELINE_OFFSET, mTooltipTextPaint);
        }
    }
}