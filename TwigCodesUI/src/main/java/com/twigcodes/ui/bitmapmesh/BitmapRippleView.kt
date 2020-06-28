package com.twigcodes.ui.bitmapmesh

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class BitmapRippleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_MESH_WIDTH = 20
        private const val DEFAULT_MESH_HEIGHT = 20
        private const val DEFAULT_GRID_COLOR = Color.BLACK
        private const val DEFAULT_GRID_WIDTH = 3
        private const val DEFAULT_MASK_COLOR = Color.WHITE
        private const val DEFAULT_RIPPLE_WIDTH = 60f
        private const val INIT_RIPPLE_RADIUS = 30f
        private const val OFFSET_PER_PERIOD = 15f
    }

    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mMaskColor: Int
    private val mRowMajorOriginalCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mRowMajorWarpCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mTouchDownSubject = PublishSubject.create<Unit>()

    private val mBitmapPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mColors: IntArray? = null

    private var mIntervalX = 0f
    private var mIntervalY = 0f

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    var debug: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapRippleView, defStyleAttr, defStyleRes)
        bitmap = a.getDrawable(R.styleable.BitmapRippleView_android_src)?.toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapRippleView_meshColumn, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapRippleView_meshRow, DEFAULT_MESH_HEIGHT)
        mMaskColor = a.getColor(R.styleable.BitmapRippleView_meshMaskColor, DEFAULT_MASK_COLOR)
        debug = a.getBoolean(R.styleable.BitmapRippleView_meshDebug, false)

        mGridPaint.run {
            color = a.getColor(R.styleable.BitmapRippleView_meshGridColor, DEFAULT_GRID_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.BitmapRippleView_meshGridWidth, DEFAULT_GRID_WIDTH).toFloat().apply {
                mIntersectionRadius = this * 2f
            }
        }

        mIntersectionPaint.run {
            color = a.getColor(R.styleable.BitmapRippleView_meshGridColor, DEFAULT_GRID_COLOR)
        }

        a.recycle()

        initView()
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mIntervalX = (width - paddingStart - paddingEnd) / mMeshWidth.toFloat()
                    mIntervalY = (height - paddingTop - paddingBottom) / mMeshHeight.toFloat()
                    makeCoordinates(mRowMajorWarpCoordinates)
                    makeCoordinates(mRowMajorOriginalCoordinates)
                }

        touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mTouchDownSubject.onNext(Unit)
                            startRipple(event.x to event.y)
                        }
                    }
                }
    }

    private fun makeCoordinates(coordinates: ArrayList<ArrayList<Pair<Float, Float>>>) {
        (0..mMeshHeight).forEach { y ->
            val rowCoordinates = arrayListOf<Pair<Float, Float>>()
            (0..mMeshWidth).forEach { x ->
                rowCoordinates.add(Pair(paddingStart + x * mIntervalX, paddingTop + y * mIntervalY))
            }

            coordinates.add(rowCoordinates)
        }
    }

    /**
     *
     * 移动次数 = (对角线长度 - 初始选取半径 + 选取宽度/2) / 每20ms向外移动距离
     */
    private fun startRipple(p0: Pair<Float, Float>) {
        val w = width - paddingStart - paddingEnd
        val h = height - paddingTop - paddingBottom
        val diagonal = sqrt(w.toFloat().pow(2) + h.toFloat().pow(2))
        val count = (diagonal - INIT_RIPPLE_RADIUS + DEFAULT_RIPPLE_WIDTH / 2) / OFFSET_PER_PERIOD

        Observable.interval(20, TimeUnit.MILLISECONDS)
                .take(count.toLong())
                .takeUntil(mTouchDownSubject)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe({
                    val radius = INIT_RIPPLE_RADIUS + OFFSET_PER_PERIOD * it

                    mRowMajorOriginalCoordinates.forEachIndexed { row, rowCoordinates ->
                        rowCoordinates.forEachIndexed { column, p1 ->
                            mRowMajorWarpCoordinates[row][column] = getWarpCoordinate(p0, p1, radius)
                        }
                    }
                    invalidate()
                }, {}, {
                    mRowMajorOriginalCoordinates.forEachIndexed { row, rowCoordinates ->
                        rowCoordinates.forEachIndexed { column, coordinate ->
                            mRowMajorWarpCoordinates[row][column] = coordinate
                        }
                    }
                    invalidate()
                })
    }

    /**
     * 以radius为中心, 以[radius - DEFAULT_RIPPLE_WIDTH/2, radius + DEFAULT_RIPPLE_WIDTH/2]作为选取宽度,
     * 落在这个区间的顶点做"扭曲"处理, 否则恢复原状.
     * 关于"扭曲"的处理, 一种实现方案是"向中间靠拢", 其中ΔL是顶点"扭曲"的距离, 取值为一个mesh宽高较小值的一半.
     * p0为touch down的坐标, p1为落入区间顶点的坐标, p2为"扭曲"后的坐标.
     *
     * in:  y1-y0/y2-y1 = x1-x0/x2-x1 = L/ΔL
     *      x2 = x1 + (x1-x0)ΔL/L
     *      y2 = y1 + (y1-y0)ΔL/L
     * out: y1-y0/y1-y2 = x1-x0/x1-x2 = L/ΔL
     *      x2 = x1 - (x1-x0)ΔL/L
     *      y2 = y1 - (y1-y0)ΔL/L
     *
     * 这种实现方式有一个弊端, 就是由于落入"out"的顶点是向内"扭曲", 当在边界处时, "底色"就会漏出来.
     * 这里选择的实现方案是全部向外"扭曲".
     *      y1-y0/y2-y1 = x1-x0/x2-x1 = L/ΔL
     *      x2 = x1 + (x1-x0)ΔL/L
     *      y2 = y1 + (y1-y0)ΔL/L
     */
    private fun getWarpCoordinate(p0: Pair<Float, Float>, p1: Pair<Float, Float>, radius: Float): Pair<Float, Float> {
        val l = sqrt((p0.first - p1.first).pow(2) + (p0.second - p1.second).pow(2))
        return when {
            l >= radius - DEFAULT_RIPPLE_WIDTH / 2 && l <= radius + DEFAULT_RIPPLE_WIDTH / 2 -> {
                val deltaL = min(mIntervalX, mIntervalY) / 2
                val x = p1.first + (p1.first - p0.first) * deltaL / l
                val y = p1.second + (p1.second - p0.second) * deltaL / l
                x to y
            }
            else -> p1
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

        canvas.drawColor(mMaskColor)
        drawBitmapMesh(canvas)

        if (debug) {
            drawGrid(canvas)
            drawIntersection(canvas)
        }
    }

    fun colorVertex(colors: IntArray) {
        mColors = colors
        invalidate()
    }

    fun colorVertex(color: Int?) {
        mColors = color?.run {
            (1..(mMeshWidth + 1) * (mMeshHeight + 1)).map { this }.toIntArray()
        }
        invalidate()
    }
}