package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.widget.textChanges
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil

class ZipCodeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private val displayView: ZipCodeDisplayView
    private val editView: EditText

    init {
        displayView = ZipCodeDisplayView(context)
        editView = LayoutInflater.from(context).inflate(R.layout.item_zipcode_edit, this, false) as EditText

        addView(editView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        addView(displayView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        editView.textChanges()
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { displayView.update(it.toList().map { c -> "$c" }) }
    }

    private class ZipCodeDisplayView(context: Context) : LinearLayout(context) {
        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER

            IntRange(0, 5).forEach { index ->
                val view = LayoutInflater.from(context).inflate(R.layout.item_zipcode_display, this, false)
                val margin = when (index) {
                    0 -> 0
                    4 -> SystemUtil.dip2px(context, 30f)
                    else -> SystemUtil.dip2px(context, 10f)
                }
                addView(view, LayoutParams(view.layoutParams).apply { marginStart = margin })
            }
        }

        fun update(codes: List<String>) {
            children.forEach { (it as TextView).text = "" }
            children.toList().zip(codes).forEach { (it.first as TextView).text = it.second }
        }
    }
}