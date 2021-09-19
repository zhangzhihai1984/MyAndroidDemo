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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.common_title_layout.view.*

class CommonTitleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val mEndTextSubject = PublishSubject.create<Unit>()

    init {
        orientation = VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.item_background))

        inflate(context, R.layout.common_title_layout, this)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CommonTitleView, defStyleAttr, defStyleRes)
        val centerText = a.getString(R.styleable.CommonTitleView_centerText)
        val endText = a.getString(R.styleable.CommonTitleView_endText)

        a.recycle()

        center_textview.text = centerText

        endText?.run {
            end_textview.text = this
            end_textview.visibility = VISIBLE
        }

        globalLayouts().take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { title_statusbar_view.updateLayoutParams { height = SystemUtil.getStatusBarHeight(context) } }

        start_imageview.clicks()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { (context as Activity).finish() }

        end_textview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { mEndTextSubject.onNext(Unit) }
    }

    fun endTextClicks(): Observable<Unit> = mEndTextSubject
}