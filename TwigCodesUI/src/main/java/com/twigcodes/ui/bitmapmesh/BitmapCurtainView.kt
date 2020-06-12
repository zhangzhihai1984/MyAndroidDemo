package com.twigcodes.ui.bitmapmesh

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import kotlin.math.max
import kotlin.math.min

class BitmapCurtainView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        const val DEFAULT_MESH_WIDTH = 20
        const val DEFAULT_MESH_HEIGHT = 20
        const val DEFAULT_GRID_COLOR = Color.BLACK
        const val DEFAULT_GRID_WIDTH = 3
        const val DEFAULT_MASK_COLOR = Color.WHITE
        private const val PI2 = 2 * Math.PI
    }

    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mMaskColor: Int
    private val mRowMajorOriginalCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mRowMajorWarpCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private var mColors: IntArray? = null

    private val mBitmapPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // ω
    private var omega = 0.0

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    var percent: Float = 1f
        set(value) {
            field = min(max(value, 0f), 1f)
            makeWarpCoordinates()
            invalidate()
        }

    var debug: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var vertexCount: Int = 0
        get() = (mMeshWidth + 1) * (mMeshHeight + 1)
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapCurtainView, defStyleAttr, defStyleRes)
        bitmap = a.getDrawable(R.styleable.BitmapCurtainView_android_src)?.toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapCurtainView_meshRow, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapCurtainView_meshColumn, DEFAULT_MESH_HEIGHT)
        mMaskColor = a.getColor(R.styleable.BitmapCurtainView_meshMaskColor, DEFAULT_MASK_COLOR)
        debug = a.getBoolean(R.styleable.BitmapCurtainView_debug, false)

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

    private fun initView() {
        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    makeCoordinates(mRowMajorWarpCoordinates)
                    makeCoordinates(mRowMajorOriginalCoordinates)
                    omega = PI2 / (width.toFloat() / 2.5)

//                    mRowMajorWarpCoordinates.forEachIndexed { row, rowCoordinates ->
//                        rowCoordinates.forEachIndexed { column, coordinate ->
//                            val x = coordinate.first
//                            val y = coordinate.second - 20 * sin(omega * x).toFloat()
//                            mRowMajorWarpCoordinates[row][column] = x to y
//                        }
//                    }

                    invalidate()
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

    private fun makeWarpCoordinates() {
        mRowMajorOriginalCoordinates.forEachIndexed { row, rowCoordinates ->
            rowCoordinates.forEachIndexed { column, coordinate ->
                val x = coordinate.first + (width - paddingEnd - coordinate.first) * percent
                val y = coordinate.second

                mRowMajorWarpCoordinates[row][column] = x to y
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