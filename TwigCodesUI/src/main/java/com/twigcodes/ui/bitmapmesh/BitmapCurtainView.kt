package com.twigcodes.ui.bitmapmesh

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class BitmapCurtainView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        internal const val DEFAULT_MESH_WIDTH = 20
        internal const val DEFAULT_MESH_HEIGHT = 20
        internal const val DEFAULT_GRID_COLOR = Color.BLACK
        internal const val DEFAULT_GRID_WIDTH = 3
        internal const val DEFAULT_MAX_PERCENT = 0.8f

        private const val PI2 = 2 * Math.PI
        private const val WAVE_MAX_HEIGHT = 30
        private const val WAVE_MAX_WIDHT = 120
    }

    private var mMeshWidth: Int
    private var mMeshHeight: Int
    private var mIntersectionRadius: Float
    private var mMaxPercent: Float
    private var mColors: IntArray
    private var mTouchable: Boolean

    private val mRowMajorOriginalCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mRowMajorWarpCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()

    private val mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mScroller by lazy { Scroller(context, LinearInterpolator()) }
    private val mVelocityTracker by lazy { VelocityTracker.obtain() }

    // ω
    private var omegaV = 0.0
    private var omegaH = 0.0

    private var mCenterY = 0f
    private var mDownX = 0f

    private val mPercentChangeSubject = PublishSubject.create<Float>()

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    var percent: Float = 0f
        set(value) {
            field = min(max(value, 0f), mMaxPercent)
            makeWarpCoordinates()
            invalidate()

            mPercentChangeSubject.onNext(field)
        }

    var debug: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapCurtainView, defStyleAttr, defStyleRes)
        bitmap = a.getDrawable(R.styleable.BitmapCurtainView_android_src)?.toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapCurtainView_meshColumn, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapCurtainView_meshRow, DEFAULT_MESH_HEIGHT)
        mMaxPercent = min(max(a.getFloat(R.styleable.BitmapCurtainView_curtainMaxPercent, DEFAULT_MAX_PERCENT), 0f), 1f)
        mTouchable = a.getBoolean(R.styleable.BitmapCurtainView_curtainTouchable, true)
        debug = a.getBoolean(R.styleable.BitmapCurtainView_meshDebug, false)

        mColors = IntArray((mMeshWidth + 1) * (mMeshHeight + 1)) { Color.WHITE }

        mGridPaint.run {
            color = a.getColor(R.styleable.BitmapCurtainView_meshGridColor, DEFAULT_GRID_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.BitmapCurtainView_meshGridWidth, DEFAULT_GRID_WIDTH).toFloat().apply {
                mIntersectionRadius = this * 2f
            }
        }

        mIntersectionPaint.run {
            color = a.getColor(R.styleable.BitmapCurtainView_meshGridColor, DEFAULT_GRID_COLOR)
        }

        a.recycle()

        initView()
    }

    internal fun config(meshWidth: Int, meshHeight: Int, maxPercent: Float, touchable: Boolean, debug: Boolean, gridColor: Int, gridWidth: Int) {
        mMeshWidth = meshWidth
        mMeshHeight = meshHeight
        mMaxPercent = min(max(maxPercent, 0f), 1f)
        mTouchable = touchable
        this.debug = debug

        mColors = IntArray((mMeshWidth + 1) * (mMeshHeight + 1)) { Color.WHITE }

        mGridPaint.run {
            color = gridColor
            strokeWidth = gridWidth.toFloat()
            mIntersectionRadius = gridWidth.toFloat() * 2
        }

        mIntersectionPaint.run {
            color = gridColor
        }
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    makeCoordinates(mRowMajorWarpCoordinates)
                    makeCoordinates(mRowMajorOriginalCoordinates)
                    omegaV = PI2 * 2.5 / (width.toFloat() - paddingStart - paddingEnd)
                    omegaH = PI2 * 0.7 / (height.toFloat() - paddingTop - paddingBottom)

                    mCenterY = (height.toFloat() - paddingTop - paddingBottom) / 2

                    invalidate()
                }

        touches { event ->
            if (mTouchable) {
                when (event.action) {
                    /**
                     * 在手指落入窗帘区域的情况下处理滑动, 否则在空白处也可以滑来滑去比较奇怪.
                     */
                    MotionEvent.ACTION_DOWN -> (event.x - paddingStart) / (width - paddingStart - paddingEnd) >= percent
                    else -> true
                }
            } else
                false
        }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    mVelocityTracker.addMovement(event)
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mScroller.abortAnimation()
                            mDownX = event.x
                        }
                        MotionEvent.ACTION_MOVE -> {
                            percent += (event.x - mDownX) / (width - paddingStart - paddingEnd)
                            mDownX = event.x
                        }
                        MotionEvent.ACTION_UP -> {
                            mDownX = 0f
                            mVelocityTracker.computeCurrentVelocity(1000)
                            val startX = (width - paddingStart - paddingEnd) * percent + paddingStart
                            val velocityX = mVelocityTracker.xVelocity
                            mScroller.fling(startX.toInt(), 0, velocityX.toInt(), 0, -2000, 2000, 0, 0)
                        }
                    }
                }
    }

    private fun makeCoordinates(coordinates: ArrayList<ArrayList<Pair<Float, Float>>>) {
        val intervalX = (width - paddingStart - paddingEnd) / mMeshWidth.toFloat()
        val intervalY = (height - paddingTop - paddingBottom) / mMeshHeight.toFloat()

        (0..mMeshHeight).forEach { y ->
            val rowCoordinates = arrayListOf<Pair<Float, Float>>()
            (0..mMeshWidth).forEach { x ->
                rowCoordinates.add(Pair(paddingStart + x * intervalX, paddingTop + y * intervalY))
            }

            coordinates.add(rowCoordinates)
        }
    }

    /**
     * 横坐标:
     * 对于每个原始x来讲, 横向可移动最大距离ΔXmax = width - paddingEnd - 原始x,
     * x = 原始x + ΔXmax * percent
     *
     * 纵坐标:
     * 对于每个原始x来讲, 纵向可移动最大距离ΔYmax = waveHeight * sin(ω_v * (原始x - paddingStart)),
     * y = 原始y - ΔYmax * percent
     *
     * 关于ω_v:
     * 对于sinx来说, 一个周期的长度为2π, 对于sin0.5x来说, 一个周期的长度为4π, 那么对于sinωx来说, 一个周期的长度为2π/ω.
     * ω_v = 2π/waveLength
     *     = 2π / ((width - paddingStart - paddingEnd) / multiple)
     *     = 2π * multiple / (width - paddingStart - paddingEnd)
     *
     * 上述实现的效果与现实中的效果其实有差距的. 因为在现实中, 当我们用手从左向右收起窗帘的时候, 纵向有折叠的效果的同时,
     * 横向也会因手的拉动有从左向右扭曲的效果, 这就需要相同的原始x移动后的x连接起来是一条有弧度的曲线, 而不是一条直线.
     * 因此需要把原始y这个因素也考虑在内.
     *
     * 于是, 原始x需要"二次移动":
     * x = 原始x + Δx1 + Δx2
     * 其中,
     * ΔX1max = width - paddingEnd - 原始x
     * Δx1 = ΔX1max * percent
     * Δx1是正常的线性移动, Δx2实现"扭曲".
     *
     *   0 -----> x
     *   |
     *   | y
     *   ∨
     *
     * "横向扭曲"由sin函数实现, 只不过x与y互换位置, 变成了已知y求x.
     * 参照上面对于ω的说明, 这里的ω:
     * ω_h = 2π * multiple / (height - paddingTop - paddingBottom)
     * ΔX2max = waveWidth * sin(ω_h * (原始y - paddingTop))
     *
     * 分析一下现实中从左向右收起窗帘过程, 最左侧的部分开始向右侧扭曲, 可以将其想象为是一种"力的传递", 同时这种"力的传递"
     * 是逐渐衰减的, 也就是说, 某一时刻, 最左侧始终是扭曲程度最大的, 向右逐渐减小, "靠墙的"最右侧为0.
     * 之所以强调"某一时刻", 是因为这个比较范畴是"相互比较", 还有整个过程中的"自我比较", 也就是对于某一条曲线来说,
     * 在开始和结束时刻的扭曲程度都是0, 中间的某个时刻达到最大值, 当然这个最大值还是相对自己, 只有最左侧的曲线会达到ΔX2max.
     * 总结一下, 就是Δx2需要满足"自我比较"和"相互比较"两个原则.
     *
     * 我们先来看"自我比较"原则: 变量∈[0,1], 0和1值为0, 中间0.5达到最大值, 我们首先能想到的就是y=x(1-x)这样的函数,
     * 0.5时达到最大值0.25, 因此我们需要将其变成y=4x(1-x), 于是
     * Δx2 = ΔX2max * 4 * (1 - percent） * percent
     *
     * 它似乎并不符合"相互比较"原则, "相互比较"原则是在某一时刻, 从左至右曲线扭曲程度逐渐减小, 最右为0. 但是目前的Δx2在
     * 某一时刻的所有的曲线的的扭曲程度是一样的.
     * 考虑到原始x是从左至右逐渐变大的, 既然需要从左至右曲线扭曲程度逐渐减小, 那么就将"原始x距离最右侧的百分比"这个因素
     * 考虑在内:
     * x原Proportion = (原始x - paddingStart) / (width - paddingStart - paddingEnd)
     * 于是:
     * Δx2 = ΔX2max * 4 * (1 - percent） * percent * (1 - x原Proportion)
     *
     * 目前实现的效果已经比较自然了, 但是在滑动过程中可以发现, 在三角函数的作用下, 整个图片实际上是逐渐被"拉高了", 这样就
     * 会使顶部和底部的部分区域被"挤出去了". 这就需要以centerY为中心, 让上下两部分的y同时按距离比例向中间靠拢, 让所有的
     * 部分均可见.
     *
     * 于是, 原始y需要"二次移动":
     * y = 原始y - Δy1 + Δy2
     * 其中,
     * ΔY1max = waveHeight * sin(ω_v * (原始x - paddingStart))
     * Δy1 = ΔY1max * percent
     * ΔY2max = ((centerY - 原始y) / centerY) * waveHeight
     * Δy2 = ΔY2max * percent
     * Δy1实现"折叠", Δy2实现"靠拢".
     * 需要解释一下, 之所以"减去"Δy1, 可以看一下上面的坐标系, 手机的纵坐标与数学计算的坐标是相反的, 而横坐标没有这个问题.
     *
     * 关于阴影:
     * 如果希望看上去更有立体的效果, 我们还需要在"视觉凹进去"的区域加上"阴影".
     * 所谓"视觉凹进去"的区域就是Δy1为负数的情况, 所谓的"阴影"就是将顶点颜色变暗.
     * 对于"凸起"的部分, 也就是Δy1为正数的情况, 顶点的颜色为白色.
     * 总之, 原则就如果Δy1为负数, Δy1越小, 顶点颜色越深, 当然不能为黑色, 因为太暗了也不美观, 我们需要加个系数控制一下.
     * 如果Δy1为正数, 顶点为白色.
     * 当Δy1为负数时:
     * ratio = -Δy1 / waveHeight * 0.2
     * 其中, -Δy1 / waveHeight ∈ [0, 1], 越接近1越黑, 上面说了, 为了不影响美观, 不能太黑, 经过测试, 乘了一个0.2.
     * 简单来说, 就是最暗也就暗到20%的黑. 由于全部255表示白色, 因此:
     * component = 255 * (1 - ratio)
     * color = argb(255, component, component, component)
     *
     */
    private fun makeWarpCoordinates() {
        mRowMajorOriginalCoordinates.forEachIndexed { row, rowCoordinates ->
            rowCoordinates.forEachIndexed { column, coordinate ->
                val deltaX1Max = width - paddingEnd - coordinate.first
                val deltaX1 = deltaX1Max * percent
                val xProportion = (coordinate.first - paddingStart) / (width - paddingStart - paddingEnd)
                val deltaX2Max = WAVE_MAX_WIDHT * sin(omegaH * (coordinate.second - paddingTop)).toFloat()
                val deltaX2 = deltaX2Max * 4 * (1 - percent) * percent * (1 - xProportion)
                val x = coordinate.first + deltaX1 + deltaX2

                val deltaY1Max = WAVE_MAX_HEIGHT * cos(omegaV * (coordinate.first - paddingStart)).toFloat()
                val deltaY1 = deltaY1Max * percent
                val deltaY2Max = (mCenterY - coordinate.second) / mCenterY * WAVE_MAX_HEIGHT
                val deltaY2 = deltaY2Max * percent
                val y = coordinate.second - deltaY1 + deltaY2

                mRowMajorWarpCoordinates[row][column] = x to y

                mColors[(mMeshWidth + 1) * row + column] = when {
                    deltaY1 < 0 -> {
                        val ratio = -deltaY1 / WAVE_MAX_HEIGHT * 0.2f
                        val component = (255 * (1 - ratio)).toInt()
                        Color.argb(255, component, component, component)
                    }
                    else -> Color.WHITE
                }
            }
        }
    }

    private fun drawBitmapMesh(canvas: Canvas) {
        val verts = mRowMajorWarpCoordinates.flatten()
                .flatMap { coordinate -> coordinate.toList() }
                .toFloatArray()

        bitmap?.run {
            canvas.drawBitmapMesh(this, mMeshWidth, mMeshHeight, verts, 0, mColors, 0, mBitmapPaint)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        /**
         * draw horizontal lines.
         *
         * 遍历获取"行坐标"List, 进而对"行坐标"List进行[zipWithNext], 获取每行临近两个点的坐标.
         */
        mRowMajorWarpCoordinates.forEach { rowCoordinates ->
            rowCoordinates.zipWithNext { start, end ->
                canvas.drawLine(start.first, start.second, end.first, end.second, mGridPaint)
            }
        }

        /**
         * draw vertical lines.
         *
         * 通过[zipWithNext]获取临近两列"行坐标"List, 进而对临近两列"行坐标"List进行[zip], 获取每列临近两个点的坐标.
         */
        mRowMajorWarpCoordinates.zipWithNext { startRowCoordinates, endRowCoordinates ->
            startRowCoordinates.zip(endRowCoordinates) { start, end ->
                canvas.drawLine(start.first, start.second, end.first, end.second, mGridPaint)
            }
        }
    }

    private fun drawIntersection(canvas: Canvas) {
        mRowMajorWarpCoordinates.forEach { rowCoordinates ->
            rowCoordinates.forEach { coordinate ->
                canvas.drawCircle(coordinate.first, coordinate.second, mIntersectionRadius, mIntersectionPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBitmapMesh(canvas)

        if (debug) {
            drawGrid(canvas)
            drawIntersection(canvas)
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            percent = (mScroller.currX - paddingStart).toFloat() / (width - paddingStart - paddingEnd)
            when (percent) {
                0f, mMaxPercent -> mScroller.abortAnimation()
            }
        }
    }

    fun open() {
        ValueAnimator.ofFloat(percent, 1f).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addUpdateListener { percent = animatedValue as Float }
        }.start()
    }

    fun close() {
        ValueAnimator.ofFloat(percent, 0f).apply {
            interpolator = DecelerateInterpolator()
            duration = 300
            addUpdateListener { percent = animatedValue as Float }
        }.start()
    }

    fun percentChanges(): Observable<Float> = mPercentChangeSubject
}