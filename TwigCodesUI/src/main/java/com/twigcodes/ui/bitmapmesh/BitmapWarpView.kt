package com.twigcodes.ui.bitmapmesh

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.R
import com.twigcodes.ui.util.RxUtil
import kotlin.math.pow

class BitmapWarpView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        const val DEFAULT_MESH_WIDTH = 10
        const val DEFAULT_MESH_HEIGHT = 10
        const val DEFAULT_GRID_COLOR = Color.BLACK
        const val DEFAULT_GRID_WIDTH = 3
        const val DEFAULT_MASK_COLOR = Color.WHITE
    }

    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mMaskColor: Int
    private val mRowMajorCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private var mColors: IntArray? = null

    private var mTouchRow = 0
    private var mTouchColumn = 0

    private val mBitmapPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

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

    var vertexCount: Int = 0
        get() = (mMeshWidth + 1) * (mMeshHeight + 1)
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapWarpView, defStyleAttr, defStyleRes)
        bitmap = a.getDrawable(R.styleable.BitmapWarpView_android_src)?.toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapWarpView_meshRow, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapWarpView_meshColumn, DEFAULT_MESH_HEIGHT)
        mMaskColor = a.getColor(R.styleable.BitmapWarpView_meshMaskColor, DEFAULT_MASK_COLOR)
        debug = a.getBoolean(R.styleable.BitmapWarpView_debug, false)

        mGridPaint.run {
            color = a.getColor(R.styleable.BitmapWarpView_meshGridColor, DEFAULT_GRID_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.BitmapWarpView_meshGridWidth, DEFAULT_GRID_WIDTH).toFloat().apply {
                mIntersectionRadius = this * 2f
            }
        }

        mIntersectionPaint.run {
            color = a.getColor(R.styleable.BitmapWarpView_meshGridColor, DEFAULT_GRID_COLOR)
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
                            val closestVertex = mRowMajorCoordinates.flatten()
                                    .sortedBy { coordinate -> (coordinate.first - event.x).pow(2) + (coordinate.second - event.y).pow(2) }[0]

                            mRowMajorCoordinates.forEachIndexed { row, rowCoordinates ->
                                rowCoordinates.forEachIndexed { column, coordinate ->
                                    if (coordinate == closestVertex) {
                                        mTouchRow = row
                                        mTouchColumn = column

                                        mRowMajorCoordinates[mTouchRow][mTouchColumn] = event.x to event.y
                                    }
                                }
                            }
                        }
                        MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                            mRowMajorCoordinates[mTouchRow][mTouchColumn] = event.x to event.y
                        }
                    }

                    invalidate()
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
    }

    private fun drawBitmapMesh(canvas: Canvas) {
        val verts = mRowMajorCoordinates.flatten()
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

        if (debug) {
            drawGrid(canvas)
            drawIntersection(canvas)
        }
    }
}