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
import kotlin.math.roundToInt

class BitmapMeshView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        const val DEFAULT_MESH_WIDTH = 3
        const val DEFAULT_MESH_HEIGHT = 4
        const val DEFAULT_GRID_COLOR = Color.BLACK
        const val DEFAULT_GRID_WIDTH = 10
        const val DEFAULT_MASK_COLOR = Color.WHITE
    }

    private val mBitmap: Bitmap
    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mMaskColor: Int
    private val mRowMajorCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private var mColors: List<Int>? = null

    private var mIntervalX = 0f
    private var mIntervalY = 0f
    private var mTouchRow = 0
    private var mTouchColumn = 0

    private val mBitmapPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    private val mGridPaint = Paint()
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapMeshView, defStyleAttr, defStyleRes)
        mBitmap = a.getDrawableOrThrow(R.styleable.BitmapMeshView_android_src).toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapMeshView_meshWidth, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapMeshView_meshHeight, DEFAULT_MESH_HEIGHT)
        mMaskColor = a.getColor(R.styleable.BitmapMeshView_maskColor, DEFAULT_MASK_COLOR)

        mGridPaint.run {
            color = a.getColor(R.styleable.BitmapMeshView_gridColor, DEFAULT_GRID_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.BitmapMeshView_gridWidth, DEFAULT_GRID_WIDTH).toFloat().apply {
                mIntersectionRadius = this * 2f
            }
        }

        mIntersectionPaint.run {
            color = a.getColor(R.styleable.BitmapMeshView_gridColor, DEFAULT_GRID_COLOR)
        }

        mColors = (1..(mMeshWidth + 1) * (mMeshHeight + 1)).map { Color.argb(255, (0..255).random(), (0..255).random(), (0..255).random()) }

        a.recycle()

        initView()
    }

    private fun initView() {
        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mIntervalX = (width - paddingStart - paddingEnd) / mMeshWidth.toFloat()
                    mIntervalY = (height - paddingTop - paddingBottom) / mMeshHeight.toFloat()

                    makeCoordinates()
                    invalidate()
                }

        touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mTouchRow = (event.x / mIntervalX).roundToInt()
                            mTouchColumn = (event.y / mIntervalY).roundToInt()

                            mRowMajorCoordinates[mTouchColumn][mTouchRow] = event.x to event.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            mRowMajorCoordinates[mTouchColumn][mTouchRow] = event.x to event.y
                        }
                    }

                    invalidate()
                }
    }

    private fun makeCoordinates() {
        (0..mMeshHeight).forEach { y ->
            val rowCoordinates = arrayListOf<Pair<Float, Float>>()
            (0..mMeshWidth).forEach { x ->
                rowCoordinates.add(Pair(paddingStart + x * mIntervalX, paddingTop + y * mIntervalY))
            }

            mRowMajorCoordinates.add(rowCoordinates)
        }
    }

    private fun drawBitmapMesh(canvas: Canvas) {
        val verts = mRowMajorCoordinates.flatten()
                .flatMap { coordinate -> listOf(coordinate.first, coordinate.second) }
                .toFloatArray()

        canvas.drawBitmapMesh(mBitmap, mMeshWidth, mMeshHeight, verts, 0, mColors?.toIntArray(), 0, mBitmapPaint)
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