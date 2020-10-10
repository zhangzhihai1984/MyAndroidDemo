package com.twigcodes.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.bitmapmesh.BitmapCurtainView
import com.twigcodes.ui.util.ImageUtil
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * CoverView模拟窗帘, ContentView模拟窗帘后的内容, 拉开窗帘后显示.
 * CoverView并不拦截touch事件, 它需要做的是当touch_down时, 将当前View的内容"截取"下来后交给SnapshotView.
 * 真正滑动的其实是SnapshotView, 也就是[BitmapCurtainView], 它处于CoverView的下方, 当percent大于0时,
 * 隐藏CoverView, 露出SnapshotView, 用户可以看到滑动的效果. 当percent为0时, 说明窗帘完全关闭, 此时显示CoverView,
 * 遮挡住SnapshotView, 用户可以正常操作CoverView.
 *
 * 注: CoverView中的内容应以展示为主, 如果有支持点击(Button, TextView)或滚动(ScrollView)的控件都将影响窗帘的滑动,
 * 因为作为child, 他们会先将touch事件截获. 当然也可以根据需求直接调用[open]和[close]进行窗帘的开和关, 只不过没有了
 * 中间状态而已.
 */
class CurtainView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    companion object {
        private const val CURTAIN_SNAPSHOT_ELEVATION = 100f
        private const val CURTAIN_COVER_ELEVATION = CURTAIN_SNAPSHOT_ELEVATION + 1f
        private const val DEFAULT_MASK_COLOR = Color.TRANSPARENT
    }

    private val mPercentChangeSubject = PublishSubject.create<Float>()
    private val mSnapshotView = BitmapCurtainView(context).apply { elevation = CURTAIN_SNAPSHOT_ELEVATION }
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
        val meshWidth = a.getInteger(R.styleable.CurtainView_meshColumn, BitmapCurtainView.DEFAULT_MESH_WIDTH)
        val meshHeight = a.getInteger(R.styleable.CurtainView_meshRow, BitmapCurtainView.DEFAULT_MESH_HEIGHT)
        val maxPercent = a.getFloat(R.styleable.CurtainView_curtainMaxPercent, BitmapCurtainView.DEFAULT_MAX_PERCENT)
        val touchable = a.getBoolean(R.styleable.CurtainView_curtainTouchable, true)
        val debug = a.getBoolean(R.styleable.CurtainView_meshDebug, false)
        val gridColor = a.getColor(R.styleable.CurtainView_meshGridColor, BitmapCurtainView.DEFAULT_GRID_COLOR)
        val gridWidth = a.getDimensionPixelSize(R.styleable.CurtainView_meshGridWidth, BitmapCurtainView.DEFAULT_GRID_WIDTH)
        val maskColor = a.getColor(R.styleable.CurtainView_meshMaskColor, DEFAULT_MASK_COLOR)

        val contentLayoutId = a.getResourceId(R.styleable.CurtainView_curtainContentLayout, -1).also { resourceId ->
            if (resourceId < 0)
                throw Exception("curtainContentLayout attr not found")
        }

        val coverLayoutId = a.getResourceId(R.styleable.CurtainView_curtainCoverLayout, -1).also { resourceId ->
            if (resourceId < 0)
                throw Exception("curtainCoverLayout attr not found")
        }

        a.recycle()

        mContentView = LayoutInflater.from(context).inflate(contentLayoutId, null, false)
        mCoverView = LayoutInflater.from(context).inflate(coverLayoutId, null, false).apply { elevation = CURTAIN_COVER_ELEVATION }

        addView(mContentView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(mCoverView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        val maskView = View(context).apply {
            elevation = CURTAIN_COVER_ELEVATION
            setBackgroundColor(maskColor)
        }
        addView(maskView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

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
                    mPercentChangeSubject.onNext(percent)
                    mCoverView.visibility = if (percent == 0f) View.VISIBLE else View.GONE
                }

        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mSnapshotView.config(meshWidth, meshHeight, maxPercent, touchable, debug, gridColor, gridWidth)
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

    fun percentChanges(): Observable<Float> = mPercentChangeSubject
}