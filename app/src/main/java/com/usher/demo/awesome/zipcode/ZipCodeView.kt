package com.usher.demo.awesome.zipcode

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.usher.demo.R

class ZipCodeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        IntRange(0, 5).forEach { _ ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_zipcode, this, false)
            addView(view, LayoutParams(view.layoutParams).apply { marginStart = 30 })
        }
    }

    fun update(codes: List<String>) {
        children.forEach { (it as TextView).text = "" }
        children.toList().zip(codes).forEach { (it.first as TextView).text = it.second }
    }
}