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
    /**
     * 表格固定配置项
     */
    //x轴首个series距离左边界的距离
    private static final float X_AXIS_MARGIN_START = 65;
    //x轴最后一个series距离右边界的距离
    private static final float X_AXIS_MARGIN_END = 65;
    //x轴文字baseline距离底部边界的距离
    private static final float X_AXIS_TEXT_BASELINE_MARGIN_BOTTOM = 48;

    //y轴首个series距离顶部边界的距离
    private static final float Y_AXIS_MARGIN_TOP = 84;
    //y轴最后一个series距离底部边界的距离
    private static final float Y_AXIS_MARGIN_BOTTOM = 0;
    //y轴tick的长度
    private static final float Y_AXIS_TICK_LENGTH = 24;
    //y轴label距离左边界的距离
    private static final float Y_AXIS_LABEL_MARGIN_START = 51;

    //xy轴label文字大小
    private static final float AXIS_LABEL_SIZE = 27;
    //有数据选中时, x轴对应的label的text size
    private static final float AXIS_LABEL_SELECTED_SIZE = 48;
    //xy轴tick的stroke宽度
    private static final float AXIS_TICK_WIDTH = 3;

    //折线的stroke宽度
    private static final float DATA_LINE_STROKE_WIDTH = 15;
    //折线连接处的圆角半径
    private static final float DATA_LINE_PATH_CORNER = 12;
    //折线阴影距离折线的距离
    private static final float DATA_LINE_SHADOW_OFFSET = 30;

    //【折线图】数据选中marker的圆形半径
    private static final float MARKER_CIRCLE_RADIUS = 56;
    //【折线图】数据选中marker的圆形border宽度
    private static final float MARKER_BORDER_WIDTH = 6;
    //【折线图】数据选中marker内部圆形的半径
    private static final float MARKER_INNER_RADIUS = 21;
    //数据选中tooltip圆角矩形的半径
    private static final float TOOLTIP_CORNER_RADIUS = 24;
    //数据选中tooltip的宽度
    private static final float TOOLTIP_WIDTH = 171;
    //数据选中tooltip的高度
    private static final float TOOLTIP_HEIGHT = 108;
    //【折线图】数据选中tooltip的中心距离marker中心(选中数据的坐标)的横向距离
    private static final float TOOLTIP_CENTER_MARGIN_TO_MARKER = 219;

    /**
     * 表格固定配置项 (设置Paint后计算)
     */
    //调整y轴label的baseline以保证居中显示
    private float Y_AXIS_TEXT_BASELINE_OFFSET = 0;
    //调整tooltip文字的baseline以保证居中显示
    private float TOOLTIP_TEXT_BASELINE_OFFSET = 0;

    /**
     * 温度固定配置项
     */
    //x轴series的数量
    private static final int X_AXIS_SERIES_COUNT_TEMPERATURE = 13;
    //y轴series的数量
    private static int Y_AXIS_SERIES_COUNT_TEMPERATURE = 5;
    //y轴每个series的差值 (5摄氏度)
    private static int Y_AXIS_DELTA_VALUE_PER_SERIES_TEMPERATURE = 5;
    //y轴显示的最大值
    private static final float Y_AXIS_MAX_VALUE_TEMPERATURE = 30;
    //y轴可拖动到的最大值
    private static final float DATA_MAX_VALUE_TEMPERATURE = 30;
    //y轴可拖动到的最小值
    private static float DATA_MIN_VALUE_TEMPERATURE = 15;

    //TODO:
    //y轴拖动可识别的最小幅度(0.5摄氏度)
    private float DATA_MIN_RANGE_TEMPERATURE = 0.5f;

    /**
     * 表格动态配置项
     */
    //x轴series的数量
    private int X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_TEMPERATURE;
    //y轴series的数量
    private int Y_AXIS_SERIES_COUNT = Y_AXIS_SERIES_COUNT_TEMPERATURE;
    //y轴每个series的差值 (比如说温度, 一个series代表5个摄氏度)
    private int Y_AXIS_DELTA_VALUE_PER_SERIES = Y_AXIS_DELTA_VALUE_PER_SERIES_TEMPERATURE;
    //y轴显示的最大值
    private float Y_AXIS_MAX_VALUE = Y_AXIS_MAX_VALUE_TEMPERATURE;
    //y轴可拖动到的最大值
    private float DATA_MAX_VALUE = DATA_MAX_VALUE_TEMPERATURE;
    //y轴可拖动到的最小值
    private float DATA_MIN_VALUE = DATA_MIN_VALUE_TEMPERATURE;

    /**
     * 表格动态配置后计算项
     */
    //x轴每个series的长度
    private float X_AXIS_SERIES_INTERVAL = 0;
    //y轴每个series的高度
    private float Y_AXIS_SERIES_INTERVAL = 0;

    /**
     * 表格Paint (通用)
     */
    private final Paint mXAxisTickPaint = new Paint();
    private final Paint mYAxisTickPaint = new Paint();
    private final Paint mYAxisLabelPaint = new Paint();
    private final Paint mXAxisLabelPaint = new Paint();
    private final Paint mXAxisSelectedLabelPaint = new Paint();
    private final Paint mTooltipBackgroundPaint = new Paint();
    private final Paint mTooltipBorderPaint = new Paint();
    private final Paint mTooltipTextPaint = new Paint();

    /**
     * 表格Paint (折线图)
     */
    private final Paint mDataSolidLinePaint = new Paint();
    private final Paint mDataDashLinePaint = new Paint();
    private final Paint mDataShadowLinePaint = new Paint();
    private final Path mDataSolidLinePath = new Path();
    private final Path mDataSolidShadowLinePath = new Path();
    private final Path mDataDashLinePath = new Path();
    private final Path mDataDashShadowLinePath = new Path();
    private final Paint mMarkerBackgroundPaint = new Paint();
    private final Paint mMarkerBorderPaint = new Paint();
    private final Paint mMarkerInnerPaint = new Paint();

    private int mWidth;
    private int mHeight;

    private List<Float> mSolidData = new ArrayList<>();
    private List<Float> mDashData = new ArrayList<>();

    private int mSelectedDataIndex = -1;

    /**
     * 考虑到用户体验, 在通过手势查看数值或拖拽曲线后, tooptip和marker会延迟消失, 如果在尚未消失时点击了其他的数据点,
     * 那么新数据点tooltip和marker可能会因为延时时间到而突然消失, 因此通过这个subject让延时的Observable在此时takeUntil掉
     */
    private final PublishSubject<Unit> mTouchDownSubject = PublishSubject.create();

    /**
     * 考虑到用户体验, 在通过手势查看数值或拖拽曲线后, tooptip和marker会延迟消失, 如果在尚未消失时切换ChartType,
     * 比如从温度切换到湿度, 由于此时已经没有数据了, 应该停止继续绘制数据点的tooltip和marker
     */
    private final PublishSubject<Unit> mSetConfigSubject = PublishSubject.create();
    private float mTouchDownY;
    private float mTouchDownData;
    private boolean mCanDragData = false;
    private DataType mDataType = DataType.TEMPERATURE;
    private ChartType mChartType = ChartType.LINE;

    public enum DataType {
        TEMPERATURE,
        HUMIDITY
    }

    public enum ChartType {
        LINE,
        COLUMN
    }

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

    private void initView() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mXAxisTickPaint.setColor(Color.WHITE);
        mXAxisTickPaint.setStrokeWidth(AXIS_TICK_WIDTH);

        mYAxisTickPaint.setColor(Color.WHITE);
        mYAxisTickPaint.setStrokeWidth(AXIS_TICK_WIDTH);

        mYAxisLabelPaint.setAntiAlias(true);
        mYAxisLabelPaint.setColor(Color.WHITE);
        mYAxisLabelPaint.setTextAlign(Paint.Align.LEFT);
        mYAxisLabelPaint.setTextSize(AXIS_LABEL_SIZE);
        mYAxisLabelPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Regular.otf"));

        Y_AXIS_TEXT_BASELINE_OFFSET = -mYAxisLabelPaint.getFontMetrics().top / 2 - mYAxisLabelPaint.getFontMetrics().bottom / 2;

        mXAxisLabelPaint.setAntiAlias(true);
        mXAxisLabelPaint.setColor(Color.WHITE);
        mXAxisLabelPaint.setTextAlign(Paint.Align.CENTER);
        mXAxisLabelPaint.setTextSize(AXIS_LABEL_SIZE);
        mXAxisLabelPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Regular.otf"));

        mXAxisSelectedLabelPaint.setAntiAlias(true);
        mXAxisSelectedLabelPaint.setColor(Color.WHITE);
        mXAxisSelectedLabelPaint.setTextAlign(Paint.Align.CENTER);
        mXAxisSelectedLabelPaint.setTextSize(AXIS_LABEL_SELECTED_SIZE);
        mXAxisSelectedLabelPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPro-Bold.ttf"));

        mDataSolidLinePaint.setAntiAlias(true);
        mDataSolidLinePaint.setStrokeWidth(DATA_LINE_STROKE_WIDTH);
        mDataSolidLinePaint.setPathEffect(new CornerPathEffect(DATA_LINE_PATH_CORNER));
        mDataSolidLinePaint.setStyle(Paint.Style.STROKE);
        mDataSolidLinePaint.setStrokeCap(Paint.Cap.ROUND);

        mDataShadowLinePaint.setAntiAlias(true);
        mDataShadowLinePaint.setColor(Color.parseColor("#417ACC"));
        mDataShadowLinePaint.setStrokeWidth(DATA_LINE_STROKE_WIDTH);
        mDataShadowLinePaint.setPathEffect(new CornerPathEffect(DATA_LINE_PATH_CORNER));
        mDataShadowLinePaint.setStyle(Paint.Style.STROKE);
        mDataShadowLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mDataShadowLinePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));

        mDataDashLinePaint.setAntiAlias(true);
        mDataDashLinePaint.setStrokeWidth(DATA_LINE_STROKE_WIDTH);
        mDataDashLinePaint.setColor(Color.parseColor("#3765B5"));
        mDataDashLinePaint.setStyle(Paint.Style.STROKE);
        mDataDashLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mDataDashLinePaint.setPathEffect(new ComposePathEffect(new DashPathEffect(new float[]{30, 25}, 0), new CornerPathEffect(DATA_LINE_PATH_CORNER)));

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
    }

    private void updateConfigItems() {
        switch (mDataType) {
            case TEMPERATURE:
            default: {
                X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_TEMPERATURE;
                Y_AXIS_SERIES_COUNT = Y_AXIS_SERIES_COUNT_TEMPERATURE;
                Y_AXIS_MAX_VALUE = Y_AXIS_MAX_VALUE_TEMPERATURE;
                DATA_MAX_VALUE = DATA_MAX_VALUE_TEMPERATURE;
                DATA_MIN_VALUE = DATA_MIN_VALUE_TEMPERATURE;
            }
            break;
        }

        if (mWidth > 0)
            updateExtraConfigItems();
    }

    private void updateExtraConfigItems() {
        X_AXIS_SERIES_INTERVAL = (mWidth - X_AXIS_MARGIN_START - X_AXIS_MARGIN_END) / X_AXIS_SERIES_COUNT;
        Y_AXIS_SERIES_INTERVAL = (mHeight - Y_AXIS_MARGIN_TOP - Y_AXIS_MARGIN_BOTTOM) / Y_AXIS_SERIES_COUNT;

        mYAxisTickPaint.setShader(new LinearGradient(0, 0, 0, mHeight, Color.TRANSPARENT, Color.parseColor("#4CFFFFFF"), Shader.TileMode.CLAMP));
        mDataSolidLinePaint.setShader(new LinearGradient(0, 0, mWidth, 0, Color.parseColor("#66F8DB"), Color.parseColor("#E8FF7B"), Shader.TileMode.CLAMP));
    }

    public void setConfig(ChartType chartType, DataType dataType) {
        setConfig(chartType, dataType, false);
    }

    public void setConfig(DataType dataType, boolean drag) {
        setConfig(ChartType.LINE, dataType, drag);
    }

    /**
     * 考虑到此时处于用户体验, tooltip和marker可能尚未消失, 为了防止其继续绘制, 需要将选中数据点的索引置为-1 {@link #drawMarkerAndTooltip(Canvas)}
     * 同时通过subject让延时的Observable在此时takeUntil掉, 因为延时的存在已经没有意义了
     */
    private void setConfig(ChartType chartType, DataType dataType, boolean drag) {
        mSelectedDataIndex = -1;
        mSetConfigSubject.onNext(Unit.INSTANCE);

        mChartType = chartType;
        mDataType = dataType;
        mCanDragData = drag;

        mSolidData.clear();
        mDashData.clear();

        updateConfigItems();
        invalidate();
    }

    public void setData(List<Float> data) {
        mSolidData.clear();
        mSolidData.addAll(data);

        mDashData.clear();
        if (mCanDragData) {
            mDashData.addAll(data);
        }

        makeDataLinePath();
        postInvalidate();
    }

    public List<Float> getData() {
        return mSolidData;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mSolidData.isEmpty())
                    break;

                mTouchDownY = event.getY();
                mTouchDownSubject.onNext(Unit.INSTANCE);

                float x = event.getX();

                if (x < X_AXIS_MARGIN_START || x >= mWidth - X_AXIS_MARGIN_START) {
                    mSelectedDataIndex = -1;
                } else {
                    mSelectedDataIndex = (int) Math.floor((x - X_AXIS_MARGIN_START) / X_AXIS_SERIES_INTERVAL);
                    mTouchDownData = mSolidData.get(mSelectedDataIndex);
                }

                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                //TODO: LINE & Drag 放行
                if (!mCanDragData)
                    break;

                if (mSelectedDataIndex >= 0) {
                    mSolidData.set(mSelectedDataIndex, Math.min(DATA_MAX_VALUE, Math.max(getMovingData(event.getY()), DATA_MIN_VALUE)));
                    makeDataLinePath();
                }

                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                mTouchDownY = 0;

                Observable.timer(1000, TimeUnit.MILLISECONDS)
                        .takeUntil(mTouchDownSubject)
                        .takeUntil(mSetConfigSubject)
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

    private float getMovingData(float currentY) {
        float data;
        /*
         * rawDeltaData表示手指滑动的距离转化成的对应data的增减值.
         * rawDeltaData = (手指按下时的Y - 当前手指的Y) / (一个series的高度 / 每个series的差值)
         * 其中(一个series的高度 / 每个series的差值)代表的是data对应一个单位的距离.
         * 比如温度, 一个series的高度为214.8, 这一个series的差值为5摄氏度, 那么这一个单位, 也就是说一个摄氏度的高度为42.96
         * 如果手指向上移动了193.00134, 那么rawDeltaData就为4.4925823摄氏度
         */
        float rawDeltaData = (mTouchDownY - currentY) / (Y_AXIS_SERIES_INTERVAL / Y_AXIS_DELTA_VALUE_PER_SERIES);

        switch (mDataType) {
            /*
             * 温度以0.5度为一个调整幅度, 如果小数部分小于0.5, 认为小数部分为0.0, 否则为0.5.
             * 比如当前选中的温度为21.0, rawDeltaData为0.42747343, 那么我们将data保持在21.0不变.
             * 如果rawDeltaData为0.81504667, 那么我们将data调整为21.5.
             * 再比如上面的4.4925823, 它对应的调整值为4.0.
             */
            case TEMPERATURE:
            default: {
                float deltaData = (float) Math.floor(rawDeltaData);
                if (rawDeltaData - deltaData > DATA_MIN_RANGE_TEMPERATURE)
                    deltaData += DATA_MIN_RANGE_TEMPERATURE;

                data = mTouchDownData + deltaData;
            }
            break;
        }

        return data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWidth <= 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            updateExtraConfigItems();
        }

        drawAxis(canvas);
        drawData(canvas);
        drawMarkerAndTooltip(canvas);
    }

    /**
     *
     */
    private void makeDataLinePath() {
        mDataDashLinePath.reset();
        mDataDashShadowLinePath.reset();

        if (!mDashData.isEmpty()) {
            for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
                float x = (i + 0.5f) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
                float y = Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - mDashData.get(i)) * Y_AXIS_SERIES_INTERVAL / Y_AXIS_DELTA_VALUE_PER_SERIES;
                if (i <= 0) {
                    mDataDashLinePath.moveTo(x, y);
                    mDataDashShadowLinePath.moveTo(x, y + DATA_LINE_SHADOW_OFFSET);
                } else {
                    mDataDashLinePath.lineTo(x, y);
                    mDataDashShadowLinePath.lineTo(x, y + DATA_LINE_SHADOW_OFFSET);
                }
            }
        }

        mDataSolidLinePath.reset();
        mDataSolidShadowLinePath.reset();

        for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
            float x = (i + 0.5f) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
            float y = Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - mSolidData.get(i)) * Y_AXIS_SERIES_INTERVAL / Y_AXIS_DELTA_VALUE_PER_SERIES;
            if (i <= 0) {
                mDataSolidLinePath.moveTo(x, y);
                mDataSolidShadowLinePath.moveTo(x, y + DATA_LINE_SHADOW_OFFSET);
            } else {
                mDataSolidLinePath.lineTo(x, y);
                mDataSolidShadowLinePath.lineTo(x, y + DATA_LINE_SHADOW_OFFSET);
            }
        }
    }

    private void drawAxis(Canvas canvas) {
        for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
            if (i > 0)
                canvas.drawLine(i * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START, 0, i * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START, mHeight, mYAxisTickPaint);
            canvas.drawText(String.valueOf(i * 2), (i + 0.5f) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START, mHeight - X_AXIS_TEXT_BASELINE_MARGIN_BOTTOM, i == mSelectedDataIndex ? mXAxisSelectedLabelPaint : mXAxisLabelPaint);
        }

        for (int i = 0; i < Y_AXIS_SERIES_COUNT; i++) {
            canvas.drawLine(0, i * Y_AXIS_SERIES_INTERVAL + Y_AXIS_MARGIN_TOP, Y_AXIS_TICK_LENGTH, i * Y_AXIS_SERIES_INTERVAL + Y_AXIS_MARGIN_TOP, mXAxisTickPaint);
            canvas.drawText(String.valueOf((int) Y_AXIS_MAX_VALUE - i * Y_AXIS_DELTA_VALUE_PER_SERIES), Y_AXIS_LABEL_MARGIN_START, i * Y_AXIS_SERIES_INTERVAL + Y_AXIS_MARGIN_TOP + Y_AXIS_TEXT_BASELINE_OFFSET, mYAxisLabelPaint);
        }
    }

    private void drawData(Canvas canvas) {
        if (!mDashData.isEmpty()) {
            canvas.drawPath(mDataDashShadowLinePath, mDataShadowLinePaint);
            canvas.drawPath(mDataDashLinePath, mDataDashLinePaint);
        }

        if (!mSolidData.isEmpty()) {
            canvas.drawPath(mDataSolidShadowLinePath, mDataShadowLinePaint);
            canvas.drawPath(mDataSolidLinePath, mDataSolidLinePaint);
        }
    }

    private void drawMarkerAndTooltip(Canvas canvas) {
        if (mSelectedDataIndex >= 0) {
            float x = (mSelectedDataIndex + 0.5f) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
            float y = Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - mSolidData.get(mSelectedDataIndex)) * Y_AXIS_SERIES_INTERVAL * 1.0f / Y_AXIS_DELTA_VALUE_PER_SERIES;
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