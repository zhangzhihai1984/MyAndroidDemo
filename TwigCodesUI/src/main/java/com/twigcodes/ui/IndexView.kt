package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.util.RxUtil
import io.reactivex.subjects.PublishSubject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class IndexView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_TEXT_SIZE = 40
        private const val DEFAULT_IDLE_COLOR = Color.GRAY
        private const val DEFAULT_INDEXED_COLOR = Color.BLACK
    }

    private val mIndexChangeSubject = PublishSubject.create<Int>()

    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 3f
    }

    private var mIdleColor: Int
    private var mIndexedColor: Int
    private var mTextSize: Float
    private var mIsDebug = false

    private var mData = listOf<String>()

    private var mItemHeight = 0f
    private var mCurrentIndex = -1

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.IndexView, defStyleAttr, defStyleRes)
        mIdleColor = a.getColor(R.styleable.IndexView_idleColor, DEFAULT_IDLE_COLOR)
        mIndexedColor = a.getColor(R.styleable.IndexView_indexedColor, DEFAULT_INDEXED_COLOR)
        mTextSize = a.getDimensionPixelSize(R.styleable.IndexView_textSize, DEFAULT_TEXT_SIZE).toFloat()
        mIsDebug = a.getBoolean(R.styleable.IndexView_debug, false)
        a.recycle()

        mTextPaint.run {
            textSize = mTextSize
        }

        initView()
    }

    private fun initView() {
        touches { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val index = min(max(floor((event.y / mItemHeight)).toInt(), 0), mData.size - 1)

                    //为了避免滑动过程中不断emit相同的值, 只有index发生变化了之后再emit
                    if (mCurrentIndex != index) {
                        mCurrentIndex = index
                        mIndexChangeSubject.onNext(mCurrentIndex)
                    }

                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                }
            }

            true
        }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { }
    }

    fun setData(data: List<String>) {
        mData = data

        requestLayout()

        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mItemHeight = height.toFloat() / mData.size
                    invalidate()
                }
    }

    fun indexChanges() = mIndexChangeSubject

    fun changeIndex(index: Int) {
        if (mCurrentIndex != index) {
            mCurrentIndex = index
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mData.forEachIndexed { i, text ->
            if (mIsDebug)
                canvas.drawLine(0f, i * mItemHeight, width.toFloat(), i * mItemHeight, mDividerPaint)

            val metrics = mTextPaint.fontMetrics
            canvas.drawText(text, width.toFloat() / 2, (i + 0.5f) * mItemHeight - metrics.top / 2 - metrics.bottom / 2, mTextPaint.apply {
                color = if (i == mCurrentIndex) mIndexedColor else mIdleColor
            })
        }
    }
}