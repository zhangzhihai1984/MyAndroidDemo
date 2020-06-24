package com.twigcodes.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.bitmapmesh.BitmapCurtainView
import com.twigcodes.ui.util.ImageUtil
import com.twigcodes.ui.util.RxUtil

class CurtainView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val CURTAIN_BITMAP_ELEVATION = 100f
        private const val CURTAIN_TEXTURE_ELECATION = CURTAIN_BITMAP_ELEVATION + 1f
    }

    private val mCurtainView = BitmapCurtainView(context).apply { elevation = CURTAIN_BITMAP_ELEVATION }

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            mCurtainView.bitmap = value
        }

    var debug = false
        set(value) {
            field = value
            mCurtainView.debug = value
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CurtainView, defStyleAttr, defStyleRes)
        val bitmap = a.getDrawable(R.styleable.CurtainView_android_src)?.toBitmap()
        val meshWidth = a.getInteger(R.styleable.CurtainView_meshRow, BitmapCurtainView.DEFAULT_MESH_WIDTH)
        val meshHeight = a.getInteger(R.styleable.CurtainView_meshColumn, BitmapCurtainView.DEFAULT_MESH_HEIGHT)
        val maxPercent = a.getFloat(R.styleable.CurtainView_meshCurtainMaxPercent, BitmapCurtainView.DEFAULT_MAX_PERCENT)
        val touchable = a.getBoolean(R.styleable.CurtainView_meshCurtainTouchable, true)
        val debug = a.getBoolean(R.styleable.CurtainView_meshDebug, false)
        val gridColor = a.getColor(R.styleable.CurtainView_meshGridColor, BitmapCurtainView.DEFAULT_GRID_COLOR)
        val gridWidth = a.getDimensionPixelSize(R.styleable.CurtainView_meshGridWidth, BitmapCurtainView.DEFAULT_GRID_WIDTH)
        val curtainTextureLayoutId = a.getResourceId(R.styleable.CurtainView_curtainTextureLayout, -1).also { resourceId ->
            if (resourceId < 0)
                throw Exception("curtain layout not found")
        }
        a.recycle()

        val textureView = LayoutInflater.from(context).inflate(curtainTextureLayoutId, null, false).apply { elevation = CURTAIN_TEXTURE_ELECATION }

        addView(textureView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        textureView.touches { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mCurtainView.bitmap = ImageUtil.getViewBitmap(textureView)
                    textureView.visibility = View.GONE
                }
            }
            false
        }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                }

        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mCurtainView.config(meshWidth, meshHeight, bitmap, maxPercent, touchable, debug, gridColor, gridWidth)
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