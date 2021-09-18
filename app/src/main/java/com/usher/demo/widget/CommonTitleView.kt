package com.usher.demo.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.globalLayouts
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil
import com.usher.demo.R
import kotlinx.android.synthetic.main.common_title_layout.view.*

class CommonTitleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        orientation = VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.item_background))

        inflate(context, R.layout.common_title_layout, this)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CommonTitleView, defStyleAttr, defStyleRes)
        center_textview.text = a.getString(R.styleable.CommonTitleView_title)

        a.recycle()

        globalLayouts().take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { title_statusbar_view.updateLayoutParams { height = SystemUtil.getStatusBarHeight(context) } }

        start_imageview.clicks()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { (context as Activity).finish() }
    }
}