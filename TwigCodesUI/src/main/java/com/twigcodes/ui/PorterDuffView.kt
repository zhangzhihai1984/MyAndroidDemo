package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class PorterDuffView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {

    }

    private val mDstPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSrcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var mode: PorterDuff.Mode? = null
        set(value) {
            field = value
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        mDstPaint.apply {
            color = 0xFFFFCC44.toInt()
        }

        mSrcPaint.apply {
            color = 0xFF66AAFF.toInt()
        }
    }

    private fun makeDstBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val side = min(width, height)
        canvas.drawCircle(width / 3f, height / 3f, side / 3f, mDstPaint)

        return bitmap
    }

    private fun makeSrcBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRect(width / 3f, height / 3f, width.toFloat(), height.toFloat(), mSrcPaint)

        return bitmap
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawBitmap(makeDstBitmap(), 0f, 0f, mBitmapPaint)
        mBitmapPaint.xfermode = PorterDuffXfermode(mode)
        canvas.drawBitmap(makeSrcBitmap(), 0f, 0f, mBitmapPaint)
        mBitmapPaint.xfermode = null
    }
}