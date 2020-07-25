package com.twigcodes.ui.util

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import com.squareup.picasso.Transformation
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ImageUtil {
    fun getViewBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun getRenderScriptBlurScaledBitmap(context: Context, source: Bitmap, scale: Float = 8f, radius: Float = 8f): Bitmap {
        val width = (source.width / scale).roundToInt()
        val height = (source.height / scale).roundToInt()
        val bitmap = Bitmap.createScaledBitmap(source, width, height, false)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).run {
            setRadius(min(max(radius, 1f), 25f))
            setInput(input)
            forEach(output)
        }
        output.copyTo(bitmap)
        renderScript.destroy()

        return bitmap
    }

    fun getRenderScriptBlurBitmap(context: Context, source: Bitmap, radius: Float = 8f): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, source)
        val output = Allocation.createFromBitmap(renderScript, bitmap)
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).run {
            setRadius(min(max(radius, 1f), 25f))
            setInput(input)
            forEach(output)
        }
        output.copyTo(bitmap)
        renderScript.destroy()

        return bitmap
    }

    fun getScaledBlurBitmap(source: Bitmap, scale: Float = 8f): Bitmap {
        val width = (source.width / scale).roundToInt()
        val height = (source.height / scale).roundToInt()

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
                        getRenderScriptBlurBitmap(context, source).apply { source.recycle() }

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