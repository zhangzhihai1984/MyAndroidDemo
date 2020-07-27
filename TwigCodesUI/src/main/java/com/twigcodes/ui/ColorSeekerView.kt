package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class ColorSeekerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        inflate(context, R.layout.color_seeker_layout, this)
        orientation = VERTICAL
    }
}