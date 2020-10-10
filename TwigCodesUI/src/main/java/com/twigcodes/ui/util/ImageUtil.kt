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
//        val bitmap = Bitmap.createBitmap(source)
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

    /**
     * 关于Canvas的创建
     *
     * 举例, 设备的dpi为480
     * source对应的density为:
     * hdpi: 240
     * xdpi: 320
     * xxdpi: 480
     *
     * 以source放在hdpi为例, 它的density为240, 而[Bitmap.createBitmap]获得的bitmap的density与设备一致,
     * 即480, 参见方法注释:
     * The default density is the same density as the current display.
     *
     * 进而创建的[Canvas]的density也为480, 参见构造函数注释:
     * The initial target density of the canvas is the same as the given bitmap's density.
     *
     * 这样产生的效果就是, source和通过[Canvas.drawBitmap]得到的bitmap显示在设备上的尺寸是不一样的, 尽管
     * 两个Bitmap的width和height是一样的, 但是由于dpi不一样, 后者的尺寸是前者的1/4.
     *
     * 解决方案有两种:
     * 1. 保证在与设备dpi对应的drawable文件夹下能够找到该图片(一般来说就是将图片放在xxdpi及更高密度的文件夹中)
     * 2. 设置Canvas的density与source一致.
     */
    fun getCircleBitmap(source: Bitmap): Bitmap {
        val side = min(source.width, source.height)
        val bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply { density = source.density }
        val radius = side / 2f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawCircle(radius, radius, radius, paint)

        val left = -(source.width - side) / 2f
        val top = -(source.height - side) / 2f
        canvas.drawBitmap(source, left, top, paint.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        })

//        val left = (source.width - side) / 2
//        val top = (source.height - side) / 2
//        val right = (source.width + side) / 2
//        val bottom = (source.height + side) / 2
//        val src = Rect(left, top, right, bottom)
//        val dst = Rect(0, 0, bitmap.width, bitmap.height)
//        canvas.drawBitmap(source, src, dst, paint.apply {
//            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        })

        return bitmap
    }

    fun getRoundBitmap(source: Bitmap, radius: Float = 30f): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply { density = source.density }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            canvas.drawRoundRect(0f, 0f, source.width.toFloat(), source.height.toFloat(), radius, radius, paint)
        else
            canvas.drawRect(0f, 0f, source.width.toFloat(), source.height.toFloat(), paint)

        canvas.drawBitmap(source, 0f, 0f, paint.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        })

        return bitmap
    }

    fun getPorterDuffColorFilterBitmap(source: Bitmap, color: Int, mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply { density = source.density }

        canvas.drawBitmap(source, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(color, mode)
        })

        return bitmap
    }

    fun getLightingColorFilterBitmap(source: Bitmap, mul: Int, add: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply { density = source.density }

        canvas.drawBitmap(source, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = LightingColorFilter(mul, add)
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