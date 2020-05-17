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
import kotlin.math.roundToInt

object ImageUtil {
    fun getBlurTransformation(context: Context): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap {
                    val width = (source.width / 8f).roundToInt()
                    val height = (source.height / 8f).roundToInt()
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
                    source.recycle()

                    return bitmap
                }

                override fun key(): String = "BlurTransformation"
            }

    fun getSquareTransformation(): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap {
                    val size = min(source.width, source.height)
                    val x = (source.width - size) / 2
                    val y = (source.height - size) / 2
                    val bitmap = Bitmap.createBitmap(source, x, y, size, size)

                    source.recycle()

                    return bitmap
                }

                override fun key(): String = "SquareTransformation"
            }

    fun getCircleTransformation(): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap {
                    val size = min(source.width, source.height)
                    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    val radius = size.toFloat() / 2

                    canvas.drawCircle(radius, radius, radius, paint)
                    canvas.drawBitmap(source, 0f, 0f, paint.apply {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    })

                    source.recycle()

                    return bitmap
                }

                override fun key(): String = "CircleTransformation"
            }

    fun getRoundTransformation(radius: Float = 50f): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap {
                    val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                    canvas.drawRoundRect(0f, 0f, source.width.toFloat(), source.height.toFloat(), radius, radius, paint)
                    canvas.drawBitmap(source, 0f, 0f, paint.apply {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    })

                    source.recycle()

                    return bitmap
                }

                override fun key(): String = "RoundTransformation"
            }

    fun getViewBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }
}