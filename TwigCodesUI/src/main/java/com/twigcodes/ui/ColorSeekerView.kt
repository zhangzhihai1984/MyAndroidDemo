package com.twigcodes.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.widget.changes
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.color_seeker_layout.view.*

class ColorSeekerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val mColorSeekSubject = PublishSubject.create<Int>()

    init {
        inflate(context, R.layout.color_seeker_layout, this)
        orientation = VERTICAL

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorSeekerView, defStyleAttr, defStyleRes)
        val colorInit = a.getColor(R.styleable.ColorSeekerView_colorInit, Color.BLACK)
        val showAlpha = a.getBoolean(R.styleable.ColorSeekerView_colorShowAlpha, true)

        a.recycle()

        alpha_seekbar.visibility = if (showAlpha) View.VISIBLE else View.GONE

        initView()
        updateColor(colorInit)
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .switchMap {
                    Observables.combineLatest(alpha_seekbar.changes(), red_seekbar.changes(), green_seekbar.changes(), blue_seekbar.changes()) { alpha, red, green, blue ->
                        Color.argb(alpha, red, green, blue)
                    }
                }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { color ->
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        alpha_seekbar.progressTintList = ColorStateList.valueOf(color)
                    }
                    mColorSeekSubject.onNext(color)
                }

    }

    fun updateColor(color: Int) {
        val alpha = Color.alpha(color)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        updateColor(alpha, red, green, blue)
    }

    private fun updateColor(alpha: Int, red: Int, green: Int, blue: Int) {
        alpha_seekbar.progress = alpha
        red_seekbar.progress = red
        green_seekbar.progress = green
        blue_seekbar.progress = blue
    }

    fun colorSeeks(): Observable<Int> = mColorSeekSubject
}