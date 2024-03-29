package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.util.RxUtil

class GraffitiView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_STROKE_COLOR = Color.BLACK
        private const val DEFAULT_STROKE_WIDTH = 15
        private const val DEFAULT_MASK_COLOR = Color.TRANSPARENT
    }

    private val mMaskColor: Int
    private val mPathWithPaints = arrayListOf<Pair<Path, Paint>>()

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        pathEffect = CornerPathEffect(15f)
    }

    var strokeColor: Int = mPaint.color
        set(value) {
            field = value
            mPaint.color = value
        }

    var strokeWidth: Float = mPaint.strokeWidth
        set(value) {
            field = value
            mPaint.strokeWidth = value
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.GraffitiView, defStyleAttr, defStyleRes)

        mMaskColor = a.getColor(R.styleable.GraffitiView_graffitiMaskColor, DEFAULT_MASK_COLOR)

        mPaint.run {
            color = a.getColor(R.styleable.GraffitiView_graffitiStrokeColor, DEFAULT_STROKE_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.GraffitiView_graffitiStrokeWidth, DEFAULT_STROKE_WIDTH).toFloat()
        }

        a.recycle()

        touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val path = Path().apply { moveTo(event.x, event.y) }
                            val paint = Paint(mPaint)
                            mPathWithPaints.add(path to paint)
                        }
                        MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                            mPathWithPaints.lastOrNull()?.first?.lineTo(event.x, event.y)
                            invalidate()
                        }
                    }
                }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        mPathWithPaints.forEach { pathWithPaint ->
            canvas.drawPath(pathWithPaint.first, pathWithPaint.second)
        }

        canvas.drawColor(mMaskColor)
    }

    fun clear() {
        mPathWithPaints.clear()
        invalidate()
    }

    fun undo() {
        mPathWithPaints.lastOrNull()?.let { pathWithPaint ->
            mPathWithPaints.remove(pathWithPaint)
            invalidate()
        }
    }
}