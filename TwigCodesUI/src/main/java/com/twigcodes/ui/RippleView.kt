package com.twigcodes.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.util.RxUtil
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt

class RippleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        const val DEFAULT_MESH_WIDTH = 10
        const val DEFAULT_MESH_HEIGHT = 10
        const val DEFAULT_GRID_COLOR = Color.BLACK
        const val DEFAULT_GRID_WIDTH = 10
        const val DEFAULT_MASK_COLOR = Color.WHITE
        const val RIPPLE_WIDTH = 60f
        const val INIT_RIPPLE_RADIUS = 30f
        const val RIPPLE_INTEVAL = 15f
    }

    private val mBitmap: Bitmap
    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mMaskColor: Int
    private val mImmutableCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mRowMajorCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mTouchDownSubject = PublishSubject.create<Unit>()

    private val mBitmapPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RippleView, defStyleAttr, defStyleRes)
        mBitmap = a.getDrawableOrThrow(R.styleable.RippleView_android_src).toBitmap()
        mMeshWidth = a.getInteger(R.styleable.RippleView_rippleMeshWidth, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.RippleView_rippleMeshHeight, DEFAULT_MESH_HEIGHT)
        mMaskColor = a.getColor(R.styleable.RippleView_rippleMaskColor, DEFAULT_MASK_COLOR)

        mGridPaint.run {
            color = a.getColor(R.styleable.RippleView_rippleGridColor, DEFAULT_GRID_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.RippleView_rippleGridWidth, DEFAULT_GRID_WIDTH).toFloat().apply {
                mIntersectionRadius = this * 2f
            }
        }

        mIntersectionPaint.run {
            color = a.getColor(R.styleable.RippleView_rippleGridColor, DEFAULT_GRID_COLOR)
        }

        a.recycle()

        initView()
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    makeCoordinates()
                    invalidate()
                }

        touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mTouchDownSubject.onNext(Unit)
                            startRipple(event.x to event.y)
                        }
                    }
                }
    }

    /**
     * in:  y1-y0/y2-y1 = x1-x0/x2-x1 = L/ΔL
     *      x2 = x1 + (x1-x0)ΔL/L
     *      y2 = y1 + (y1-y0)ΔL/L
     * out: y1-y0/y1-y2 = x1-x0/x1-x2 = L/ΔL
     *      x2 = x1 - (x1-x0)ΔL/L
     *      y2 = y1 - (y1-y0)ΔL/L
     */
    private fun startRipple(p0: Pair<Float, Float>) {
        val maxL = sqrt(width.toFloat().pow(2) + height.toFloat().pow(2))
        val count = (maxL - INIT_RIPPLE_RADIUS) / RIPPLE_INTEVAL

        Observable.interval(20, TimeUnit.MILLISECONDS)
                .take(count.toLong())
                .takeUntil(mTouchDownSubject)
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe({
                    val radius = INIT_RIPPLE_RADIUS + RIPPLE_INTEVAL * it

                    mRowMajorCoordinates.forEachIndexed { row, rowCoordinates ->
                        rowCoordinates.forEachIndexed { column, _ ->
                            val p1 = mImmutableCoordinates[row][column]
                            mRowMajorCoordinates[row][column] = makeRippleCoordinate(p0, p1, radius)
                        }
                    }
                    invalidate()
                }, {}, {
                    mImmutableCoordinates.forEachIndexed { row, rowCoordinates ->
                        rowCoordinates.forEachIndexed { column, coordinate ->
                            mRowMajorCoordinates[row][column] = coordinate
                        }
                    }
                    invalidate()
                })
    }

    private fun makeRippleCoordinate(p0: Pair<Float, Float>, p1: Pair<Float, Float>, radius: Float): Pair<Float, Float> {
        val l = sqrt((p0.first - p1.first).pow(2) + (p0.second - p1.second).pow(2))
        return when {
            l > radius - RIPPLE_WIDTH / 2 && l < radius -> {
                val x = p1.first + (p1.first - p0.first) * 10 / l
                val y = p1.second + (p1.second - p0.second) * 10 / l
                x to y
            }
            l >= radius && l < radius + RIPPLE_WIDTH / 2 -> {
                val x = p1.first + (p1.first - p0.first) * 10 / l
                val y = p1.second + (p1.second - p0.second) * 10 / l
                x to y
            }
            else -> p1
        }
    }

    private fun makeCoordinates() {
        val intervalX = (width - paddingStart - paddingEnd) / mMeshWidth.toFloat()
        val intervalY = (height - paddingTop - paddingBottom) / mMeshHeight.toFloat()

        (0..mMeshHeight).forEach { y ->
            val rowCoordinates = arrayListOf<Pair<Float, Float>>()
            (0..mMeshWidth).forEach { x ->
                rowCoordinates.add(Pair(paddingStart + x * intervalX, paddingTop + y * intervalY))
            }

            mRowMajorCoordinates.add(rowCoordinates)
        }

        (0..mMeshHeight).forEach { y ->
            val rowCoordinates = arrayListOf<Pair<Float, Float>>()
            (0..mMeshWidth).forEach { x ->
                rowCoordinates.add(Pair(paddingStart + x * intervalX, paddingTop + y * intervalY))
            }

            mImmutableCoordinates.add(rowCoordinates)
        }

    }

    private fun drawBitmapMesh(canvas: Canvas) {
        val verts = mRowMajorCoordinates.flatten()
                .flatMap { coordinate -> coordinate.toList() }
                .toFloatArray()

        canvas.drawBitmapMesh(mBitmap, mMeshWidth, mMeshHeight, verts, 0, null, 0, mBitmapPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        /**
         * draw horizontal lines.
         *
         * 遍历获取"行坐标"List, 进而对"行坐标"List进行[zipWithNext], 获取每行临近两个点的坐标.
         */
        mRowMajorCoordinates.forEach { rowCoordinates ->
            rowCoordinates.zipWithNext { start, end ->
                canvas.drawLine(start.first, start.second, end.first, end.second, mGridPaint)
            }
        }

        /**
         * draw vertical lines.
         *
         * 通过[zipWithNext]获取临近两列"行坐标"List, 进而对临近两列"行坐标"List进行[zip], 获取每列临近两个点的坐标.
         */
        mRowMajorCoordinates.zipWithNext { startRowCoordinates, endRowCoordinates ->
            startRowCoordinates.zip(endRowCoordinates) { start, end ->
                canvas.drawLine(start.first, start.second, end.first, end.second, mGridPaint)
            }
        }
    }

    private fun drawIntersection(canvas: Canvas) {
        mRowMajorCoordinates.forEach { rowCoordinates ->
            rowCoordinates.forEach { coordinate ->
                canvas.drawCircle(coordinate.first, coordinate.second, mIntersectionRadius, mIntersectionPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(mMaskColor)
        drawBitmapMesh(canvas)
        drawGrid(canvas)
        drawIntersection(canvas)
    }
}