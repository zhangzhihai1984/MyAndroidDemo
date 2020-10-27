package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.zipcode_layout.view.*

class ZipCodeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val mZipCodeSubject = BehaviorSubject.create<String>()
    private val mImm by lazy { context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    private val mDisplayViews = arrayListOf<TextView>()

    init {

        inflate(context, R.layout.zipcode_layout, this)

        IntRange(0, 5).forEach { index ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_zipcode_display, this, false) as TextView
            val margin = when (index) {
                0 -> 0
                4 -> SystemUtil.dip2px(context, 30f)
                else -> SystemUtil.dip2px(context, 10f)
            }
            zicode_container.addView(view, LinearLayout.LayoutParams(view.layoutParams).apply { marginStart = margin })
            mDisplayViews.add(view)
        }

        mDisplayViews.forEachIndexed { index, view ->
            view.clicks()
                    .compose(RxUtil.singleClick())
                    .to(RxUtil.autoDispose(context as LifecycleOwner))
                    .subscribe {
                        zipcode_edittext.requestFocus()
                        mImm.showSoftInput(zipcode_edittext, 0)
                        mDisplayViews.forEach { view -> view.background = ContextCompat.getDrawable(context, R.drawable.zipcode_default_background) }
                        view.background = ContextCompat.getDrawable(context, R.drawable.zipcode_selected_background)
                    }
        }

        zipcode_edittext.textChanges()
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mZipCodeSubject.onNext(it.toString())
                    update(it.toList().map { c -> "$c" })
                }
    }

    fun update(codes: List<String>) {
        mDisplayViews.forEach { it.text = "" }
        mDisplayViews.zip(codes).forEach { it.first.text = it.second }
    }

    fun zipCodeChanges(): Observable<String> = mZipCodeSubject
}