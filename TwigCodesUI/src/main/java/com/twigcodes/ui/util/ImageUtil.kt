package com.twigcodes.ui.util

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import com.squareup.picasso.Transformation
import kotlin.math.min

object ImageUtil {
    fun getViewBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun getScriptBlurBitmap(context: Context, source: Bitmap): Bitmap {
        val width = source.width / 8
        val height = source.height / 8
        val bitmap = Bitmap.createScaledBitmap(source, width, height, false)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).run {
            setRadius(8f)
            setInput(input)
            forEach(output)
        }
        output.copyTo(bitmap)
        renderScript.destroy()

        return bitmap
    }

    fun getScaledBlurBitmap(source: Bitmap): Bitmap {
        val width = source.width / 16
        val height = source.height / 16

        return Bitmap.createScaledBitmap(source, width, height, true)
    }

    fun getSquareBitmap(source: Bitmap): Bitmap {
        val side = min(source.width, source.height)
        val x = (source.width - side) / 2
        val y = (source.height - side) / 2

        return Bitmap.createBitmap(source, x, y, side, side)
    }

    fun getCircleBitmap(source: Bitmap): Bitmap {
        val side = min(source.width, source.height)
        val bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val radius = side / 2f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawCircle(radius, radius, radius, paint)

//        val left = -(source.width - side) / 2f
//        val top = -(source.height - side) / 2f
//        canvas.drawBitmap(source, left, top, paint.apply {
//            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        })

        val left = (source.width - side) / 2
        val top = (source.height - side) / 2
        val right = (source.width + side) / 2
        val bottom = (source.height + side) / 2
        val src = Rect(left, top, right, bottom)
        val dst = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawBitmap(source, src, dst, paint.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        })

        return bitmap
    }

    fun getRoundBitmap(source: Bitmap, radius: Float = 50f): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawRoundRect(0f, 0f, source.width.toFloat(), source.height.toFloat(), radius, radius, paint)

        val dst = Rect(0, 0, bitmap.width, bitmap.height)
//        canvas.drawBitmap(source, 0f, 0f, paint.apply {
//            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        })
        canvas.drawBitmap(source, null, dst, paint.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        })

        return bitmap
    }

    fun getColorFilterBitmap(source: Bitmap, color: Int = Color.RED, mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val dst = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawBitmap(source, null, dst, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(color, mode)
        })

        return bitmap
    }

    fun getBlurTransformation(context: Context): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap =
                        getScriptBlurBitmap(context, source).apply { source.recycle() }

                override fun key(): String = "BlurTransformation"
            }

    fun getSquareTransformation(): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap =
                        getSquareBitmap(source).apply { source.recycle() }

                override fun key(): String = "SquareTransformation"
            }

    fun getCircleTransformation(): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap =
                        getCircleBitmap(source).apply { source.recycle() }

                override fun key(): String = "CircleTransformation"
            }

    fun getRoundTransformation(radius: Float = 50f): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap =
                        getRoundBitmap(source, radius).apply { source.recycle() }

                override fun key(): String = "RoundTransformation"
            }
}