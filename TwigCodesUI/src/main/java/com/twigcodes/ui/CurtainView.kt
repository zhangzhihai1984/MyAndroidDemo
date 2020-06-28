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

    private val mSnapshotView = BitmapCurtainView(context).apply { elevation = CURTAIN_BITMAP_ELEVATION }
    private val mContentView: View
    private val mCoverView: View

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            mSnapshotView.bitmap = value
        }

    var debug = false
        set(value) {
            field = value
            mSnapshotView.debug = value
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CurtainView, defStyleAttr, defStyleRes)
        val bitmap = a.getDrawable(R.styleable.CurtainView_android_src)?.toBitmap()
        val meshWidth = a.getInteger(R.styleable.CurtainView_meshRow, BitmapCurtainView.DEFAULT_MESH_WIDTH)
        val meshHeight = a.getInteger(R.styleable.CurtainView_meshColumn, BitmapCurtainView.DEFAULT_MESH_HEIGHT)
        val maxPercent = a.getFloat(R.styleable.CurtainView_curtainMaxPercent, BitmapCurtainView.DEFAULT_MAX_PERCENT)
        val touchable = a.getBoolean(R.styleable.CurtainView_curtainTouchable, true)
        val debug = a.getBoolean(R.styleable.CurtainView_meshDebug, false)
        val gridColor = a.getColor(R.styleable.CurtainView_meshGridColor, BitmapCurtainView.DEFAULT_GRID_COLOR)
        val gridWidth = a.getDimensionPixelSize(R.styleable.CurtainView_meshGridWidth, BitmapCurtainView.DEFAULT_GRID_WIDTH)

        val contentLayoutId = a.getResourceId(R.styleable.CurtainView_curtainContentLayout, -1).also { resourceId ->
            if (resourceId < 0)
                throw Exception("curtainContentLayout attr not found")
        }

        val curtainTextureLayoutId = a.getResourceId(R.styleable.CurtainView_curtainCoverLayout, -1).also { resourceId ->
            if (resourceId < 0)
                throw Exception("curtainCoverLayout attr not found")
        }
        a.recycle()

        mContentView = LayoutInflater.from(context).inflate(contentLayoutId, null, false)
        mCoverView = LayoutInflater.from(context).inflate(curtainTextureLayoutId, null, false).apply { elevation = CURTAIN_TEXTURE_ELECATION }

        addView(mContentView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(mCoverView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        mCoverView.touches { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> mSnapshotView.bitmap = ImageUtil.getViewBitmap(mCoverView)
            }
            false
        }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {}

        mSnapshotView.percentChanges()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { percent ->
                    mCoverView.visibility = if (percent == 0f) View.VISIBLE else View.GONE
                }

        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mSnapshotView.config(meshWidth, meshHeight, bitmap, maxPercent, touchable, debug, gridColor, gridWidth)
                    addView(mSnapshotView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
                }
    }

    fun open() {
        mSnapshotView.bitmap = ImageUtil.getViewBitmap(mCoverView)
        mSnapshotView.open()
        mCoverView.visibility = View.GONE
    }

    fun close() {
        mSnapshotView.close()
    }
}