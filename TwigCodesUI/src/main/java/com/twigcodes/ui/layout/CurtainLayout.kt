package com.twigcodes.ui.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.R
import com.twigcodes.ui.bitmapmesh.BitmapCurtainView
import com.twigcodes.ui.util.RxUtil

class CurtainLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mCurtainView: BitmapCurtainView = BitmapCurtainView(context).apply { elevation = 100f }

    var debug: Boolean = false
        set(value) {
            field = value
            mCurtainView.debug = value
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CurtainLayout, defStyleAttr, defStyleRes)
        val bitmap = a.getDrawable(R.styleable.CurtainLayout_android_src)?.toBitmap()
        val meshWidth = a.getInteger(R.styleable.CurtainLayout_meshRow, BitmapCurtainView.DEFAULT_MESH_WIDTH)
        val meshHeight = a.getInteger(R.styleable.CurtainLayout_meshColumn, BitmapCurtainView.DEFAULT_MESH_HEIGHT)
        val touchable = a.getBoolean(R.styleable.CurtainLayout_meshTouchable, true)
        val debug = a.getBoolean(R.styleable.CurtainLayout_debug, false)
        val gridColor = a.getColor(R.styleable.CurtainLayout_meshGridColor, BitmapCurtainView.DEFAULT_GRID_COLOR)
        val gridWidth = a.getDimensionPixelSize(R.styleable.CurtainLayout_meshGridWidth, BitmapCurtainView.DEFAULT_GRID_WIDTH)
        a.recycle()

        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mCurtainView.config(meshWidth, meshHeight, bitmap, touchable, debug, gridColor, gridWidth)
                    addView(mCurtainView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
                }
    }

    fun open() {
        mCurtainView.open()
    }

    fun close() {
        mCurtainView.close()
    }
}