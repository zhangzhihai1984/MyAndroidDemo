package com.twigcodes.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.squareup.picasso.Transformation
import kotlin.math.roundToInt

object PicassoUtil {
    fun getBlurTransformation(context: Context): Transformation =
            object : Transformation {
                override fun transform(source: Bitmap): Bitmap {
                    val width = (source.width / 8f).roundToInt()
                    val height = (source.height / 8f).roundToInt()
                    val result = Bitmap.createScaledBitmap(source, width, height, false)
                    val renderScript = RenderScript.create(context)
                    val input = Allocation.createFromBitmap(renderScript, result)
                    val output = Allocation.createTyped(renderScript, input.type)
                    ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).run {
                        setRadius(8f)
                        setInput(input)
                        forEach(output)
                    }
                    output.copyTo(result)
                    renderScript.destroy()
                    source.recycle()

                    return result
                }

                override fun key(): String = "BlurTransformation"
            }
}