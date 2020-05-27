package com.twigcodes.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.util.RxUtil

class BitmapMeshView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        const val DEFAULT_MESH_WIDTH = 3
        const val DEFAULT_MESH_HEIGHT = 4
        const val DEFAULT_GRID_COLOR = Color.BLACK
        const val DEFAULT_GRID_WIDTH = 10
    }

    private val mBitmap: Bitmap
    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mRowMajorCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mGridPaint = Paint()
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapMeshView, defStyleAttr, defStyleRes)
        mBitmap = a.getDrawableOrThrow(R.styleable.BitmapMeshView_android_src).toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapMeshView_meshWidth, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapMeshView_meshHeight, DEFAULT_MESH_HEIGHT)

        mGridPaint.run {
            color = a.getColor(R.styleable.BitmapMeshView_gridColor, DEFAULT_GRID_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.BitmapMeshView_gridWidth, DEFAULT_GRID_WIDTH).toFloat().apply {
                mIntersectionRadius = this * 2f
            }
        }

        mIntersectionPaint.run {
            color = a.getColor(R.styleable.BitmapMeshView_gridColor, DEFAULT_GRID_COLOR)
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
                .flatMap { coordinate -> listOf(coordinate.first, coordinate.second) }
                .toFloatArray()

        canvas.drawBitmapMesh(mBitmap, mMeshWidth, mMeshHeight, verts, 0, null, 0, null)
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

        drawBitmapMesh(canvas)
        drawGrid(canvas)
        drawIntersection(canvas)
    }
}