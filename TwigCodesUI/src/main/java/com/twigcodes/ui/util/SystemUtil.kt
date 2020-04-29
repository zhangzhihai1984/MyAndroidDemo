package com.twigcodes.ui.util

import android.content.Context

object SystemUtil {
    fun getStatusBarHeight(context: Context): Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android").let { resId ->
                context.resources.getDimensionPixelSize(resId).takeIf { resId > 0 } ?: 72
            }
}