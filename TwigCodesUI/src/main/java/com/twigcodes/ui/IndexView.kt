package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.util.RxUtil

class IndexView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_TEXT_SIZE = 40f
        private const val DEFAULT_IDLE_COLOR = Color.BLACK
        private const val DEFAULT_SELECTED_COLOR = Color.RED
    }

    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 3f
    }

    private val mData = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")

    private var mItemHeight = 0f

    init {
//        val textSize = DEFAULT_TEXT_SIZE
        val textIdleColor = DEFAULT_IDLE_COLOR
        val textSelectedColor = DEFAULT_SELECTED_COLOR

        mTextPaint.run {
            textSize = DEFAULT_TEXT_SIZE
            color = DEFAULT_IDLE_COLOR
        }

        initView()
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    Log.i("zzh", "width: $width height:$height itemHeight:${height.toFloat() / mData.size}")
                    mItemHeight = height.toFloat() / mData.size
                }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mData.forEachIndexed { i, text ->
            canvas.drawLine(0f, i * mItemHeight, width.toFloat(), i * mItemHeight, mDividerPaint)

            val metrics = mTextPaint.fontMetrics
            canvas.drawText(text, width.toFloat() / 2, (i + 0.5f) * mItemHeight - metrics.top / 2 - metrics.bottom / 2, mTextPaint)
        }
    }
}