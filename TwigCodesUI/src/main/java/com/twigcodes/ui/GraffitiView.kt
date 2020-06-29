package com.twigcodes.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.util.RxUtil

class GraffitiView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_STROKE_WIDTH = 15f
        private const val DEFAULT_STROKE_COLOR = Color.BLACK
    }

    private val mPaths = arrayListOf<Path>()

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.run {
            style = Paint.Style.STROKE
            strokeWidth = DEFAULT_STROKE_WIDTH
            color = DEFAULT_STROKE_COLOR
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mPaths.add(Path().apply { moveTo(event.x, event.y) })
                        }
                        MotionEvent.ACTION_MOVE -> {
                            mPaths.lastOrNull()?.lineTo(event.x, event.y)
                        }
                        MotionEvent.ACTION_UP -> {

                        }
                    }
                    invalidate()
                }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        mPaths.forEach { path ->
            canvas.drawPath(path, mPaint)
        }
    }

    fun clear() {
        mPaths.clear()
        invalidate()
    }

    fun undo() {
        mPaths.lastOrNull()?.let { path -> mPaths.remove(path) }
        invalidate()
    }
}