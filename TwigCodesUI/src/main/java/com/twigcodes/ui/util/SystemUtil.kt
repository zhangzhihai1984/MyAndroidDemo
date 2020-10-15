package com.twigcodes.ui.util

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

object SystemUtil {
    fun getStatusBarHeight(context: Context): Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android").let { resId ->
                context.resources.getDimensionPixelSize(resId).takeIf { resId > 0 } ?: 72
            }

//    fun dip2px(context: Context, dpValue: Float): Int {
//        val scale = context.resources.displayMetrics.density
//        return (dpValue * scale + 0.5f).toInt()
//    }

    fun dip2px(context: Context, dpValue: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics).roundToInt()
}