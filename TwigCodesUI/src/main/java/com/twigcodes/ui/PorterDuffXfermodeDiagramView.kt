package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class PorterDuffXfermodeDiagramView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr) {

    private val mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDstBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSrcBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            val bitmap = Bitmap.createBitmap(intArrayOf(0xFFFFFF, 0xCCCCCC, 0xCCCCCC, 0xFFFFFF), 2, 2, Bitmap.Config.RGB_565)

            shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT).apply {
                setLocalMatrix(Matrix().apply { setScale(16f, 16f) })
            }
        }
    }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PorterDuffXfermodeDiagramView, defStyleAttr, defStyleRes)

        mCirclePaint.run {
            color = a.getColor(R.styleable.PorterDuffXfermodeDiagramView_porterduffDstColor, Color.rgb(233, 30, 99))
        }
        mRectPaint.run {
            color = a.getColor(R.styleable.PorterDuffXfermodeDiagramView_porterduffSrcColor, Color.rgb(33, 150, 243))
        }

        a.recycle()
    }

    private fun makeDstBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val side = min(width, height)
        canvas.drawCircle(width / 3f, height / 3f, side / 3f, mCirclePaint)

        return bitmap
    }

    private fun makeSrcBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRect(width / 3f, height / 3f, width.toFloat(), height.toFloat(), mRectPaint)

        return bitmap
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mBackgroundPaint)

        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        canvas.drawBitmap(makeDstBitmap(), 0f, 0f, mDstBitmapPaint)
        canvas.drawBitmap(makeSrcBitmap(), 0f, 0f, mSrcBitmapPaint)

        canvas.restoreToCount(saveCount)
    }

    fun update(mode: PorterDuff.Mode, srcColor: Int = mRectPaint.color, dstColor: Int = mCirclePaint.color) {
        mSrcBitmapPaint.xfermode = PorterDuffXfermode(mode)
        mRectPaint.color = srcColor
        mCirclePaint.color = dstColor
        invalidate()
    }
}