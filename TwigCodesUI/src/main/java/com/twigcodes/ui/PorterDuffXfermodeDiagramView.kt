package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class PorterDuffXfermodeDiagramView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(233, 30, 99) }
    private val mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(33, 150, 243) }
    private val mDstBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSrcBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            val bitmap = Bitmap.createBitmap(intArrayOf(0xFFFFFF, 0xCCCCCC, 0xCCCCCC, 0xFFFFFF), 2, 2, Bitmap.Config.RGB_565)
            val bitmapShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT).apply {
                setLocalMatrix(Matrix().apply { setScale(16f, 16f) })
            }
            shader = bitmapShader
        }
    }

    var mode: PorterDuff.Mode = PorterDuff.Mode.SRC_OVER
        set(value) {
            field = value
            invalidate()
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
        canvas.drawBitmap(makeSrcBitmap(), 0f, 0f, mSrcBitmapPaint.apply { xfermode = PorterDuffXfermode(mode) })

        canvas.restoreToCount(saveCount)
    }
}