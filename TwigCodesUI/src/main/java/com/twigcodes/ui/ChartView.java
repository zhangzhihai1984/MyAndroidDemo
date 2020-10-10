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
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.twigcodes.ui.util.RxUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import kotlin.Unit;

public class ChartView extends View {
    /**
     * 表格固定配置项
     */
    //x轴首个series距离左边界的原始距离 (同时用于计算每个series的宽度)
    private static final float X_AXIS_MARGIN_START_ORIGINAL = 65;
    //x轴最后一个series距离右边界的距离
    private static final float X_AXIS_MARGIN_END = 65;
    //x轴每个series的最小宽度 (防止因数据过多导致计算出的每个series宽度过小, 进而导致底部相邻label出现重叠等影响UI体验的现象出现)
    private static final float X_AXIS_SERIES_MIN_INTERVAL = 35;
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

    //【折线图】折线的stroke宽度
    private static final float DATA_LINE_STROKE_WIDTH = 15;
    //【折线图】折线连接处的圆角半径
    private static final float DATA_LINE_PATH_CORNER = 12;
    //【折线图】折线阴影距离折线的距离
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
    //【折线图】数据选中tooltip矩形的left坐标距离左边界的距离
    private static final float TOOLTIP_RECT_LEFT_MARGIN_START = 65;

    //【柱状图】数据线stroke宽度
    private static final float DATA_COLUMN_STROKE_WIDTH = 6;
    //【柱状图】数据线距离底部边界的距离
    private static final float DATA_COLUMN_MARGIN_BOTTOM = 110;

    /**
     * 表格固定配置项 (设置Paint后计算)
     */
    //调整y轴label的baseline以保证居中显示
    private float Y_AXIS_TEXT_BASELINE_OFFSET = 0;
    //调整tooltip文字的baseline以保证居中显示
    private float TOOLTIP_TEXT_BASELINE_OFFSET = 0;

    /**
     * 日固定配置项
     */
    private static final int X_AXIS_NO_DATA_SERIES_COUNT_DAY = 2;
    //x轴series的数量
    private static final int X_AXIS_SERIES_COUNT_DAY = 24 + X_AXIS_NO_DATA_SERIES_COUNT_DAY;

    /**
     * 周固定配置项
     */
    //x轴series的数量
    private static final int X_AXIS_SERIES_COUNT_WEEK = 7;

    /**
     * 月固定配置项
     */
    private static final int X_AXIS_NO_DATA_SERIES_COUNT_MONTH = 1;
    //x轴series的数量
    private static final int X_AXIS_SERIES_COUNT_MONTH = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) + X_AXIS_NO_DATA_SERIES_COUNT_MONTH;

    /**
     * 年固定配置项
     */
    private static final int X_AXIS_SERIES_COUNT_YEAR = 12;

    /**
     * 温度固定配置项
     */
    //y轴series的数量
    private static final int Y_AXIS_SERIES_COUNT_TEMPERATURE = 5;
    //y轴每个series的差值 (5摄氏度)
    private static final int Y_AXIS_DELTA_VALUE_PER_SERIES_TEMPERATURE = 5;
    //y轴可显示的最大值
    private static final float Y_AXIS_MAX_VALUE_TEMPERATURE = 30;
    //y轴可拖动到的最大值
    private static final float DATA_MAX_VALUE_TEMPERATURE = 30;
    //y轴可拖动到的最小值
    private static final float DATA_MIN_VALUE_TEMPERATURE = 15;
    //tooltip文字后缀 (℃)
    private static final String TOOLTIP_TEXT_SUFFIX_TEMPERATURE = "℃";

    //TODO:
    //y轴拖动可识别的最小幅度(0.5摄氏度)
    private final float DATA_MIN_RANGE_TEMPERATURE = 0.5f;

    /**
     * 湿度固定配置项
     */
    //x轴series的数量
    private static final int Y_AXIS_SERIES_COUNT_HUMIDITY = 6;
    //y轴每个series的差值 (5摄氏度)
    private static final int Y_AXIS_DELTA_VALUE_PER_SERIES_HUMIDITY = 10;
    //y轴可显示的最大值
    private static final float Y_AXIS_MAX_VALUE_HUMIDITY = 80;
    //y轴可拖动到的最大值
    private static final float DATA_MAX_VALUE_HUMIDITY = 80;
    //y轴可拖动到的最小值
    private static final float DATA_MIN_VALUE_HUMIDITY = 30;
    //tooltip文字后缀 (%)
    private static final String TOOLTIP_TEXT_SUFFIX_HUMIDITY = "%";

    //TODO:
    //y轴拖动可识别的最小幅度(0.5摄氏度)
    private float DATA_MIN_RANGE_HUMIDITY = 1.0f;

    /**
     * 表格动态配置项
     */
    //x轴series的数量
    private int X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_DAY;
    //x轴不显示数据的series的数量
    private int X_AXIS_NO_DATA_SERIES_COUNT = X_AXIS_NO_DATA_SERIES_COUNT_DAY;
    //y轴series的数量
    private int Y_AXIS_SERIES_COUNT = Y_AXIS_SERIES_COUNT_TEMPERATURE;
    //y轴每个series的差值 (比如说温度, 一个series代表5个摄氏度)
    private int Y_AXIS_DELTA_VALUE_PER_SERIES = Y_AXIS_DELTA_VALUE_PER_SERIES_TEMPERATURE;
    //y轴可显示的最大值
    private float Y_AXIS_MAX_VALUE = Y_AXIS_MAX_VALUE_TEMPERATURE;
    //y轴可拖动到的最大值
    private float DATA_MAX_VALUE = DATA_MAX_VALUE_TEMPERATURE;
    //y轴可拖动到的最小值
    private float DATA_MIN_VALUE = DATA_MIN_VALUE_TEMPERATURE;
    //tooltip文字后缀
    private String TOOLTIP_TEXT_SUFFIX = TOOLTIP_TEXT_SUFFIX_TEMPERATURE;

    /**
     * x轴首个series距离左边界的距离 (左右滑动chart时, 动态更新, 调用{@link #setConfig(ChartType, XType, YType, boolean)}后恢复初始值)
     */
    private float X_AXIS_MARGIN_START = X_AXIS_MARGIN_START_ORIGINAL;

    /**
     * 表格动态配置后计算项
     */
    //x轴每个series的宽度
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

    /**
     * 表格Paint (柱状图)
     */
    private final Paint mDataColumnPaint = new Paint();
    private final Paint mDataColumnSelectedPaint = new Paint();

    private final List<Float> mSolidData = new ArrayList<>();
    private final List<Float> mDashData = new ArrayList<>();

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

    private int mWidth;
    private int mHeight;

    private int mSelectedDataIndex = -1;

    private float mPreX;
    private float mPreY;
    private float mTouchDownY;
    private float mTouchDownData;

    private boolean mCanDragData = false;

    private ChartType mChartType = ChartType.LINE;
    private XType mXType = XType.DAY;
    private YType mYType = YType.TEMPERATURE;
    private DragOrientaion mDragOrientaion = DragOrientaion.UNKNOWN;

    private enum DragOrientaion {
        UNKNOWN,
        HORIZONTAL,
        VERTICAL
    }

    public enum ChartType {
        LINE,
        COLUMN
    }

    public enum YType {
        TEMPERATURE,
        HUMIDITY
    }

    public enum XType {
        DAY,
        WEEK,
        MONTH,
        YEAR,
        CUSTOM
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
        super(context, attrs, defStyleAttr);

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

        mDataColumnPaint.setAntiAlias(true);
        mDataColumnPaint.setStrokeWidth(DATA_COLUMN_STROKE_WIDTH);
        mDataColumnPaint.setStrokeCap(Paint.Cap.ROUND);

        mDataColumnSelectedPaint.setAntiAlias(true);
        mDataColumnSelectedPaint.setStrokeWidth(DATA_COLUMN_STROKE_WIDTH);
        mDataColumnSelectedPaint.setColor(Color.parseColor("#3765B5"));

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
        //TODO:
        X_AXIS_MARGIN_START = X_AXIS_MARGIN_START_ORIGINAL;

        switch (mXType) {
            case WEEK:
                X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_WEEK;
                break;
            case MONTH:
                X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_MONTH;
                break;
            case YEAR:
                X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_YEAR;
                break;
            case DAY:
            case CUSTOM:
            default:
                X_AXIS_SERIES_COUNT = X_AXIS_SERIES_COUNT_DAY;
                break;
        }

        switch (mYType) {
            case TEMPERATURE: {
                Y_AXIS_SERIES_COUNT = Y_AXIS_SERIES_COUNT_TEMPERATURE;
                Y_AXIS_DELTA_VALUE_PER_SERIES = Y_AXIS_DELTA_VALUE_PER_SERIES_TEMPERATURE;
                Y_AXIS_MAX_VALUE = Y_AXIS_MAX_VALUE_TEMPERATURE;
                DATA_MAX_VALUE = DATA_MAX_VALUE_TEMPERATURE;
                DATA_MIN_VALUE = DATA_MIN_VALUE_TEMPERATURE;
                TOOLTIP_TEXT_SUFFIX = TOOLTIP_TEXT_SUFFIX_TEMPERATURE;
            }
            case HUMIDITY:
            default: {
                Y_AXIS_SERIES_COUNT = Y_AXIS_SERIES_COUNT_HUMIDITY;
                Y_AXIS_DELTA_VALUE_PER_SERIES = Y_AXIS_DELTA_VALUE_PER_SERIES_HUMIDITY;
                Y_AXIS_MAX_VALUE = Y_AXIS_MAX_VALUE_HUMIDITY;
                DATA_MAX_VALUE = DATA_MAX_VALUE_HUMIDITY;
                DATA_MIN_VALUE = DATA_MIN_VALUE_HUMIDITY;
                TOOLTIP_TEXT_SUFFIX = TOOLTIP_TEXT_SUFFIX_HUMIDITY;
            }
            break;
        }

        if (mWidth > 0)
            updateExtraConfigItems();
    }

    private void updateExtraConfigItems() {
        X_AXIS_SERIES_INTERVAL = (mWidth - X_AXIS_MARGIN_START_ORIGINAL - X_AXIS_MARGIN_END) / X_AXIS_SERIES_COUNT;
        X_AXIS_SERIES_INTERVAL = Math.max(X_AXIS_SERIES_INTERVAL, X_AXIS_SERIES_MIN_INTERVAL);

        Y_AXIS_SERIES_INTERVAL = (mHeight - Y_AXIS_MARGIN_TOP - Y_AXIS_MARGIN_BOTTOM) / Y_AXIS_SERIES_COUNT;

        mYAxisTickPaint.setShader(new LinearGradient(0, 0, 0, mHeight, Color.TRANSPARENT, Color.parseColor("#4CFFFFFF"), Shader.TileMode.CLAMP));
        mDataSolidLinePaint.setShader(new LinearGradient(0, 0, mWidth, 0, Color.parseColor("#66F8DB"), Color.parseColor("#E8FF7B"), Shader.TileMode.CLAMP));
        mDataColumnPaint.setShader(new LinearGradient(0, 0, 0, mHeight, Color.parseColor("#E8FF7B"), Color.parseColor("#66F8DB"), Shader.TileMode.CLAMP));
    }

    public void setConfig(ChartType chartType, XType xType, YType yType) {
        setConfig(chartType, xType, yType, false);
    }

    public void setConfig(XType xType, YType yType, boolean drag) {
        setConfig(ChartType.LINE, xType, yType, drag);
    }

    /**
     * 考虑到此时出于用户体验, tooltip和marker可能尚未消失, 为了防止其继续绘制, 需要将选中数据点的索引置为-1 {@link #drawDataLineMarkerAndTooltip(Canvas)}
     * 同时通过subject让延时的Observable在此时takeUntil掉, 因为延时的存在已经没有意义了
     */
    private void setConfig(ChartType chartType, XType xType, YType yType, boolean drag) {
        mSelectedDataIndex = -1;
        mSetConfigSubject.onNext(Unit.INSTANCE);

        mChartType = chartType;
        mXType = xType;
        mYType = yType;
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

    /**
     * 【ACTION_DOWN】
     * 用于判断是否有数据点被选中以及它的索引值.对于哪个数据点是否选中, 正常来说, 我们只需要判断手指按下的点距离哪个数据点近即可.
     * 但是, 我们的数据是24个, 而series是26个, 这是因为, 首个数据对应的是1点钟, 它对应的是x轴的第二个tick,
     * 也就是说前两个series是没有数据的, 首个数据是从第三个series开始打点, 因此, 我们需要对计算出的索引值减去2.
     * index = Math.round((x - 首个series距离左边界的距离) / 每个series的宽度) - 2
     * <p>
     * 【ACTION_MOVE】
     * 用于判断是否支持滑动以及坐标及数据的更新
     * 首先需要通过手指移动的横纵距离判断此时为横向或纵向移动
     * <p>
     * 如果为横向移动:
     * 如果x轴每个series的宽度为限定的最小宽度, 说明series的总宽度已经超出了控件可显示的宽度, 此时支持横向滑动以显示全部数据,
     * 进而根据移动距离更新"x轴首个series距离左边界的距离".
     * <p>
     * 如果为纵向移动:
     * 如果当前为折线图且支持拖拽的情况下, 通过{@link #getMovingData(float)}获取拖拽后的数据点的y值.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mSolidData.isEmpty())
                    break;

                mPreX = event.getX();
                mPreY = event.getY();

                mTouchDownY = event.getY();
                mTouchDownSubject.onNext(Unit.INSTANCE);

                mDragOrientaion = DragOrientaion.UNKNOWN;

                float x = event.getX();

                int index;

                switch (mXType) {
                    case WEEK:
                    case YEAR:
                        index = (int) Math.floor((x - X_AXIS_MARGIN_START) / X_AXIS_SERIES_INTERVAL);
                        break;
                    case MONTH:
                        index = Math.round((x - X_AXIS_MARGIN_START) / X_AXIS_SERIES_INTERVAL) - X_AXIS_NO_DATA_SERIES_COUNT_MONTH;
                        break;
                    case DAY:
                    case CUSTOM:
                    default:
                        index = Math.round((x - X_AXIS_MARGIN_START) / X_AXIS_SERIES_INTERVAL) - X_AXIS_NO_DATA_SERIES_COUNT_DAY;
                        break;
                }

                if (index < 0 || index >= mSolidData.size()) {
                    mSelectedDataIndex = -1;
                } else {
                    mSelectedDataIndex = index;
                    mTouchDownData = mSolidData.get(mSelectedDataIndex);
                }

                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float y = event.getY();

                switch (mDragOrientaion) {
                    case HORIZONTAL:
                        handleHorizontalDrag(x);
                        break;
                    case VERTICAL:
                        handleVerticalDrag(y);
                        break;
                    case UNKNOWN:
                    default: {
                        if (Math.abs(x - mPreX) > 0 || Math.abs(y - mPreY) > 0) {
                            if (Math.abs(x - mPreX) > Math.abs(y - mPreY)) {
                                mDragOrientaion = DragOrientaion.HORIZONTAL;
                                handleHorizontalDrag(x);
                            } else {
                                mDragOrientaion = DragOrientaion.VERTICAL;
                                handleVerticalDrag(y);
                            }
                        }
                    }
                    break;
                }

                mPreX = x;
                mPreY = y;

                break;
            }
            case MotionEvent.ACTION_UP: {
                mPreX = 0;
                mPreY = 0;
                mTouchDownY = 0;

                Observable.timer(1000, TimeUnit.MILLISECONDS)
                        .takeUntil(mTouchDownSubject)
                        .takeUntil(mSetConfigSubject)
                        .compose(RxUtil.getSchedulerComposer())
                        .to(RxUtil.autoDispose((LifecycleOwner) getContext()))
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

    /**
     * 需要限定滑动的左右边界, 也就是限定"x轴首个series距离左边界的距离".
     * 往右侧滑动时, 这个值是变大的, 这个值要小于等于"x轴首个series距离左边界的原始距离".
     * 往左侧滑动时, 这个值是变小的, 这个值要大于等于 控件的宽度 - 每个series的最小宽度 * series的数量 - 最后一个series距离右边界的距离
     */
    private void handleHorizontalDrag(float x) {
        if (X_AXIS_SERIES_INTERVAL <= X_AXIS_SERIES_MIN_INTERVAL) {
            X_AXIS_MARGIN_START += x - mPreX;
            X_AXIS_MARGIN_START = Math.min(X_AXIS_MARGIN_START_ORIGINAL, Math.max(X_AXIS_MARGIN_START, mWidth - X_AXIS_SERIES_MIN_INTERVAL * X_AXIS_SERIES_COUNT - X_AXIS_MARGIN_END));
            makeDataLinePath();
            invalidate();
        }
    }

    private void handleVerticalDrag(float y) {
        if (mChartType == ChartType.LINE && mCanDragData) {
            if (mSelectedDataIndex >= 0) {
                mSolidData.set(mSelectedDataIndex, Math.min(DATA_MAX_VALUE, Math.max(getMovingData(y), DATA_MIN_VALUE)));
                makeDataLinePath();
                invalidate();
            }
        }
    }

    private float getMovingData(float currentY) {
        float data;
        /*
         * rawDeltaData表示手指滑动的距离转化成的对应data的增减值.
         * rawDeltaData = (手指按下时的Y - 当前手指的Y) / (每个series的高度 / 每个series的差值)
         * 其中(每个series的高度 / 每个series的差值)代表的是data对应一个单位的距离.
         * 以温度为例, 如果一个series的高度为214.8, 这一个series的差值为5摄氏度, 那么这一个单位, 也就是说一个摄氏度的高度为42.96
         * 如果手指向上移动了193.00134, 那么rawDeltaData就为4.4925823摄氏度
         */
        float rawDeltaData = (mTouchDownY - currentY) / (Y_AXIS_SERIES_INTERVAL / Y_AXIS_DELTA_VALUE_PER_SERIES);

        switch (mYType) {
            /*
             * 温度以0.5度为一个调整幅度, 如果小数部分小于0.5, 认为小数部分为0.0, 否则为0.5.
             * 比如当前选中的温度为21.0, rawDeltaData为0.42747343, 那么我们将data保持在21.0不变.
             * 如果rawDeltaData为0.81504667, 那么我们将data调整为21.5.
             * 再比如上面的4.4925823, 它对应的调整值为4.0.
             */
            case TEMPERATURE: {
                float deltaData = (float) Math.floor(rawDeltaData);
                if (rawDeltaData - deltaData > DATA_MIN_RANGE_TEMPERATURE)
                    deltaData += DATA_MIN_RANGE_TEMPERATURE;

                data = mTouchDownData + deltaData;
            }
            break;
            case HUMIDITY:
            default: {
                data = mTouchDownData + Math.round(rawDeltaData);
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

        drawAxisX(canvas);
        drawData(canvas);
        drawDataLineMarkerAndTooltip(canvas);
        drawAxisY(canvas);
    }

    /**
     * 折线图打点的过程
     * <p>
     * 我们的数据是24个, 而series是26个, 我们需要将[0-23]的数据点打到[2-25]的series上.
     * <p>
     * x = 首个series距离左边界的距离 + (i + 2) * 每个series的宽度
     * <p>
     * y = 首个series距离顶部边界的距离 + (可显示的最大值 - 当前data的值) * (每个series的高度 / 每个series的差值)
     * 以温度为例, 如果一个series的高度为214.8, 这一个series的差值为5摄氏度, 那么这一个单位, 也就是说一个摄氏度的高度为42.96
     * 如果当前的data为21.0摄氏度, 可显示的最大值为35摄氏度 那么它的y就是 Y_AXIS_MARGIN_TOP + (35.0 - 21.0) * 42.96
     * 也就是 Y_AXIS_MARGIN_TOP + 14个1摄氏度的高度
     */
    private void makeDataLinePath() {
        mDataDashLinePath.reset();
        mDataDashShadowLinePath.reset();


        if (!mDashData.isEmpty()) {
            for (int i = 0; i < mDashData.size(); i++) {
                float x = getDataX(i);
                float y = getDataY(mDashData.get(i));

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

        for (int i = 0; i < mSolidData.size(); i++) {
            float x = getDataX(i);
            float y = getDataY(mSolidData.get(i));

            if (i <= 0) {
                mDataSolidLinePath.moveTo(x, y);
                mDataSolidShadowLinePath.moveTo(x, y + DATA_LINE_SHADOW_OFFSET);
            } else {
                mDataSolidLinePath.lineTo(x, y);
                mDataSolidShadowLinePath.lineTo(x, y + DATA_LINE_SHADOW_OFFSET);
            }
        }
    }

    private float getDataX(int index) {
        float x;
        switch (mXType) {
            case WEEK:
            case YEAR:
                x = (index + 0.5f) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
                break;
            case MONTH:
                x = (index + X_AXIS_NO_DATA_SERIES_COUNT_MONTH) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
                break;
            case DAY:
            case CUSTOM:
            default:
                x = (index + X_AXIS_NO_DATA_SERIES_COUNT_DAY) * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
                break;
        }

        return x;
    }

    private float getDataY(float value) {
        return Y_AXIS_MARGIN_TOP + (Y_AXIS_MAX_VALUE - value) * (Y_AXIS_SERIES_INTERVAL / Y_AXIS_DELTA_VALUE_PER_SERIES);
    }

    /**
     * 横坐标分为26等分, 其中tick和label的距离为1等分, label和下一个tick的距离也为1等分.
     * 也就是说, 视觉上, 两个tick或两个label之间的距离其实是2等分的距离.
     * 因此, 如果当前index为偶数的话, 绘制tick, 如果为基数的话, 绘制数字.
     * 需要注意的是, 为了提升视觉体验, 看到首尾都是数字的效果, 我们不绘制首个tick.
     * <p>
     * 上面提到, 如果当index为基数的话, 绘制数字. 此时的索引为[1,3...23,25]
     * 我们需要绘制的数字为[0,2...22,24], 因此, 我们绘制的数字为 index-1.
     * <p>
     * 关于绘制选中数据对应的数字, 需要注意的是, 选中数据的索引为[0-23], 是比横坐标的26等分少两个的.
     * 这是因为, 首个数据对应的是1点钟, 它对应的是x轴的第二个tick, 也就是说它需要跳过首个tick和"0"这两个等分距离.
     * 因此, 我们需要将选中数据点索引值加上2, 变成[2-25]后再做比较.
     * 需要注意的是, 选中数据点索引值是可能为-1的(未选中), 因此数据点索引值为-1时直接排除掉.
     */
    private void drawAxisX(Canvas canvas) {
        for (int i = 0; i < X_AXIS_SERIES_COUNT; i++) {
            float x = i * X_AXIS_SERIES_INTERVAL + X_AXIS_MARGIN_START;
            float textY = mHeight - X_AXIS_TEXT_BASELINE_MARGIN_BOTTOM;

            switch (mXType) {
                case WEEK:
                case YEAR: {
                    if (i > 0)
                        canvas.drawLine(x, 0, x, mHeight, mYAxisTickPaint);
                    canvas.drawText(String.valueOf(i + 1), x + 0.5f * X_AXIS_SERIES_INTERVAL, textY, i == mSelectedDataIndex ? mXAxisSelectedLabelPaint : mXAxisLabelPaint);
                }
                break;
                case MONTH: {
                    if (i % 2 == 0) {
                        if (i > 0)
                            canvas.drawLine(x, 0, x, mHeight, mYAxisTickPaint);
                    } else {
                        boolean textSelected = mSelectedDataIndex >= 0 && i == mSelectedDataIndex + X_AXIS_NO_DATA_SERIES_COUNT_MONTH;
                        canvas.drawText(String.valueOf(i), x, textY, textSelected ? mXAxisSelectedLabelPaint : mXAxisLabelPaint);
                    }
                }
                break;
                case DAY:
                case CUSTOM:
                default: {
                    if (i % 2 == 0) {
                        if (i > 0)
                            canvas.drawLine(x, 0, x, mHeight, mYAxisTickPaint);
                    } else {
                        boolean textSelected = mSelectedDataIndex >= 0 && i == mSelectedDataIndex + X_AXIS_NO_DATA_SERIES_COUNT_DAY;
                        canvas.drawText(String.valueOf(i - 1), x, textY, textSelected ? mXAxisSelectedLabelPaint : mXAxisLabelPaint);
                    }
                }
                break;
            }
        }
    }

    /**
     *
     */
    private void drawAxisY(Canvas canvas) {
        for (int i = 0; i < Y_AXIS_SERIES_COUNT; i++) {
            float y = i * Y_AXIS_SERIES_INTERVAL + Y_AXIS_MARGIN_TOP;

            canvas.drawLine(0, y, Y_AXIS_TICK_LENGTH, y, mXAxisTickPaint);
            canvas.drawText(String.valueOf((int) Y_AXIS_MAX_VALUE - i * Y_AXIS_DELTA_VALUE_PER_SERIES), Y_AXIS_LABEL_MARGIN_START, y + Y_AXIS_TEXT_BASELINE_OFFSET, mYAxisLabelPaint);
        }
    }

    private void drawData(Canvas canvas) {
        if (mChartType == ChartType.LINE) {
            drawDataLine(canvas);
        } else {
            drawDataColumn(canvas);
        }
    }

    private void drawDataLine(Canvas canvas) {
        if (!mDashData.isEmpty()) {
            canvas.drawPath(mDataDashShadowLinePath, mDataShadowLinePaint);
            canvas.drawPath(mDataDashLinePath, mDataDashLinePaint);
        }

        if (!mSolidData.isEmpty()) {
            canvas.drawPath(mDataSolidShadowLinePath, mDataShadowLinePaint);
            canvas.drawPath(mDataSolidLinePath, mDataSolidLinePaint);
        }
    }

    private void drawDataColumn(Canvas canvas) {
        if (!mSolidData.isEmpty()) {
            for (int i = 0; i < mSolidData.size(); i++) {
                float x = getDataX(i);
                float y = getDataY(mSolidData.get(i));

                canvas.drawLine(x, mHeight - DATA_COLUMN_MARGIN_BOTTOM, x, y, i == mSelectedDataIndex ? mDataColumnSelectedPaint : mDataColumnPaint);
            }
        }
    }

    /**
     *
     */
    private void drawDataLineMarkerAndTooltip(Canvas canvas) {
        if (mSelectedDataIndex >= 0) {
            float x = getDataX(mSelectedDataIndex);
            float y = getDataY(mSolidData.get(mSelectedDataIndex));

            if (mChartType == ChartType.LINE) {
                canvas.drawCircle(x, y, MARKER_CIRCLE_RADIUS, mMarkerBackgroundPaint);
                canvas.drawCircle(x, y, MARKER_CIRCLE_RADIUS, mMarkerBorderPaint);
                canvas.drawCircle(x, y, MARKER_INNER_RADIUS, mMarkerInnerPaint);
            }

            float rectLeft = x - TOOLTIP_CENTER_MARGIN_TO_MARKER - TOOLTIP_WIDTH / 2;
            float rectRight = x - TOOLTIP_CENTER_MARGIN_TO_MARKER + TOOLTIP_WIDTH / 2;
            float valueTextX = x - TOOLTIP_CENTER_MARGIN_TO_MARKER;

            if (rectLeft <= TOOLTIP_RECT_LEFT_MARGIN_START) {
                rectLeft = x + TOOLTIP_CENTER_MARGIN_TO_MARKER - TOOLTIP_WIDTH / 2;
                rectRight = x + TOOLTIP_CENTER_MARGIN_TO_MARKER + TOOLTIP_WIDTH / 2;
                valueTextX = x + TOOLTIP_CENTER_MARGIN_TO_MARKER;
            }

            float rectTop = y - TOOLTIP_HEIGHT / 2;
            float rectBottom = y + TOOLTIP_HEIGHT / 2;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS, mTooltipBackgroundPaint);
                canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS, mTooltipBorderPaint);
            } else {
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mTooltipBackgroundPaint);
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mTooltipBorderPaint);
            }

            canvas.drawText(mSolidData.get(mSelectedDataIndex) + TOOLTIP_TEXT_SUFFIX, valueTextX, y + TOOLTIP_TEXT_BASELINE_OFFSET, mTooltipTextPaint);
        }
    }
}