package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

class IndexView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_TEXT_SIZE = 40
        private const val DEFAULT_IDLE_COLOR = Color.GRAY
        private const val DEFAULT_INDEXED_COLOR = Color.BLACK
        private const val DEFAULT_DEBOUNCE_TIME = 300
        private const val DEFAULT_TEXT_OFFSET_DIRECTION = -1

        private const val CENTER_TEXT_OFFSET_MAX = 60f
        private const val CENTER_TEXT_OFFSET_MIN = 40f
        private const val SECOND_TEXT_OFFSET_MAX = CENTER_TEXT_OFFSET_MIN
        private const val SECOND_TEXT_OFFSET_MIN = 20f
        private const val THIRD_TEXT_OFFSET_MAX = SECOND_TEXT_OFFSET_MIN
    }

    private val mTouchSubject = PublishSubject.create<Int>()
    private val mChangeIndexSubject = PublishSubject.create<Unit>()

    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 3f
    }

    private var mIdleColor: Int
    private var mIndexedColor: Int
    private var mDebounceTime: Long

    //text offset方向, 默认向左(-1)
    private var mTextOffsetDirection: Int
    private var mIsDebug: Boolean

    private var mData = listOf<String>()
    private var mTextOffsets = listOf<Float>()

    private var mItemHeight = 0f
    private var _currentIndex = -1

    var index: Int
        get() = _currentIndex
        private set(value) {
            _currentIndex = value
            invalidate()
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.IndexView, defStyleAttr, defStyleRes)
        mIdleColor = a.getColor(R.styleable.IndexView_idleColor, DEFAULT_IDLE_COLOR)
        mIndexedColor = a.getColor(R.styleable.IndexView_indexedColor, DEFAULT_INDEXED_COLOR)
        mDebounceTime = a.getInteger(R.styleable.IndexView_debounceTime, DEFAULT_DEBOUNCE_TIME).toLong()
        mTextOffsetDirection = a.getInteger(R.styleable.IndexView_OffsetDirection, DEFAULT_TEXT_OFFSET_DIRECTION)
        mIsDebug = a.getBoolean(R.styleable.IndexView_debug, false)

        mTextPaint.run {
            textSize = a.getDimensionPixelSize(R.styleable.IndexView_textSize, DEFAULT_TEXT_SIZE).toFloat()
        }

        a.recycle()

        initView()
    }

    private fun initView() {
        /**
         * event.y在[i*itemHeight, (i+1)*itemHeight)范围内均可认为落入了第i个item的区间.
         *
         * 对于第i个item, 当event.y处于item中间位置时, offset达到最大值, 随着event.y向两侧移动,
         * offset逐渐减小, 当到达item顶部或底部时, offset达到最小值.
         * dy = abs(event.y - (i+0.5)*itemHeight)
         * dy与offset的对照为: [0, itemHeight/2] -> [max, min]
         * offset = min + (max-min) * (1 - dy/(itemHeight/2))
         *
         * 对于上面的第i-1, i-2个item, 当event.y处于item顶部时, offset达到最大值, 随着event.y向下移动,
         * offset逐渐减小, 当到达item底部时, offset达到最小值.
         * dy = event.y - i*itemHeight
         * dy与offset的对照为: [0, itemHeight) -> [max, min)
         * offset = min + (max-min) * (1 - dy/itemHeight)
         *
         * 对于下面的第i+1, i+2个item, 情况与上面正好相反.
         * dy = event.y - i*itemHeight
         * dy与offset的对照为: [0, itemHeight) -> [min, max)
         * offset = min + (max-min) * (dy/itemHeight)
         */
        touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                            val intervalIndex = floor((event.y / mItemHeight)).toInt()

                            if (intervalIndex >= 0 && intervalIndex <= mData.size - 1) {
                                val centerY = (intervalIndex + 0.5f) * mItemHeight
                                val centerDeltaY = abs(event.y - centerY)
                                val centerRatio = 1 - centerDeltaY / (mItemHeight / 2)

                                val topY = intervalIndex * mItemHeight
                                val topDeltaY = event.y - topY
                                val topRatio = 1 - topDeltaY / mItemHeight
                                val bottomRatio = topDeltaY / mItemHeight

                                mTextOffsets = mTextOffsets.mapIndexed { i, _ ->
                                    when (i) {
                                        intervalIndex -> CENTER_TEXT_OFFSET_MIN + (CENTER_TEXT_OFFSET_MAX - CENTER_TEXT_OFFSET_MIN) * centerRatio
                                        intervalIndex - 1 -> SECOND_TEXT_OFFSET_MIN + (SECOND_TEXT_OFFSET_MAX - SECOND_TEXT_OFFSET_MIN) * topRatio
                                        intervalIndex + 1 -> SECOND_TEXT_OFFSET_MIN + (SECOND_TEXT_OFFSET_MAX - SECOND_TEXT_OFFSET_MIN) * bottomRatio
                                        intervalIndex - 2 -> THIRD_TEXT_OFFSET_MAX * topRatio
                                        intervalIndex + 2 -> THIRD_TEXT_OFFSET_MAX * bottomRatio
                                        else -> 0f
                                    }
                                }

                                index = intervalIndex
                            }
                        }
                    }

                    mTouchSubject.onNext(event.action)
                }

        /**
         * 对IndexView的滑动或外部滑动调用[changeIndex]均会让text产生offset, 如果两个subject超过
         * [mDebounceTime]ms均未emit, 则让text的offset归零.
         */
        Observable.merge(mTouchSubject.map { Unit }, mChangeIndexSubject)
                .debounce(mDebounceTime, TimeUnit.MILLISECONDS)
                .switchMap {
                    Observable.interval(20, TimeUnit.MILLISECONDS)
                            .takeUntil { mTextOffsets.indexOfFirst { it > 0f } < 0 }
                            .takeUntil(mTouchSubject)
                            .takeUntil(mChangeIndexSubject)
                }
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mTextOffsets = mTextOffsets.map { max(it - 10, 0f) }
                    invalidate()
                }
    }

    fun changeIndex(index: Int, showOffset: Boolean = false) {
        this.index = index

        if (showOffset) {
            mTextOffsets = mTextOffsets.mapIndexed { i, _ ->
                when (i) {
                    _currentIndex -> CENTER_TEXT_OFFSET_MAX
                    _currentIndex - 1, _currentIndex + 1 -> SECOND_TEXT_OFFSET_MAX
                    _currentIndex - 2, _currentIndex + 2 -> THIRD_TEXT_OFFSET_MAX
                    else -> 0f
                }
            }

            invalidate()

            mChangeIndexSubject.onNext(Unit)
        }
    }

    fun setData(data: List<String>) {
        mData = data
        mTextOffsets = data.map { 0f }
        _currentIndex = -1

        requestLayout()

        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mItemHeight = height.toFloat() / mData.size
                    invalidate()
                }
    }

    fun touches(): Observable<Int> = mTouchSubject

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mData.forEachIndexed { i, text ->
            if (mIsDebug)
                canvas.drawLine(0f, i * mItemHeight, width.toFloat(), i * mItemHeight, mDividerPaint)

            val metrics = mTextPaint.fontMetrics

            canvas.drawText(text, width.toFloat() / 2 + mTextOffsets[i] * mTextOffsetDirection, (i + 0.5f) * mItemHeight - metrics.top / 2 - metrics.bottom / 2, mTextPaint.apply {
                color = if (i == _currentIndex) mIndexedColor else mIdleColor
            })
        }
    }
}