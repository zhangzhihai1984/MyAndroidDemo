package com.usher.demo.utils

import android.content.Context
import com.twigcodes.ui.activity.BaseActivity

object CommonUtil {

    fun setNightModeStrategy(context: Context, value: BaseActivity.NightModeStrategy) = context.getSharedPreferences("App", Context.MODE_PRIVATE).edit().putString("nightModeStrategy", value.name).apply()

    fun getNightModeStrategy(context: Context): BaseActivity.NightModeStrategy = context.getSharedPreferences("App", Context.MODE_PRIVATE).getString("nightModeStrategy", null)?.let { name -> BaseActivity.NightModeStrategy.valueOf(name) }
            ?: BaseActivity.NightModeStrategy.AUTO
}