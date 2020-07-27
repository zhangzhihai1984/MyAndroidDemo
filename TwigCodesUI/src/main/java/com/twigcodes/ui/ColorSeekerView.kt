package com.twigcodes.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.widget.changes
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.color_seeker_layout.view.*

class ColorSeekerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val mColorSeekSubject = PublishSubject.create<Int>()

    init {
        inflate(context, R.layout.color_seeker_layout, this)
        orientation = VERTICAL

        initView()
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .switchMap { Observable.merge(red_seekbar.changes(), green_seekbar.changes(), blue_seekbar.changes()) }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { updateColor() }
    }

    fun updateColor(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        updateColor(red, green, blue)
    }

    fun updateColor(red: Int, green: Int, blue: Int) {
        red_seekbar.progress = red
        green_seekbar.progress = green
        blue_seekbar.progress = blue
    }

    private fun updateColor() = mColorSeekSubject.onNext(Color.argb(255, red_seekbar.progress, green_seekbar.progress, blue_seekbar.progress))

    fun colorSeeks(): Observable<Int> = mColorSeekSubject
}