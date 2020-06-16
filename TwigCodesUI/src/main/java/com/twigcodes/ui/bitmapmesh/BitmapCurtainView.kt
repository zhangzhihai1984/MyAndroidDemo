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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class BitmapCurtainView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_MESH_WIDTH = 20
        private const val DEFAULT_MESH_HEIGHT = 20
        private const val DEFAULT_GRID_COLOR = Color.BLACK
        private const val DEFAULT_GRID_WIDTH = 3
        private const val DEFAULT_MASK_COLOR = Color.WHITE
        private const val PI2 = 2 * Math.PI
        private const val WAVE_MAX_HEIGHT = 50
        private const val WAVE_MAX_WIDHT = 150
    }

    private val mMeshWidth: Int
    private val mMeshHeight: Int
    private val mIntersectionRadius: Float
    private val mMaskColor: Int
    private val mRowMajorOriginalCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private val mRowMajorWarpCoordinates: ArrayList<ArrayList<Pair<Float, Float>>> = arrayListOf()
    private var mColors: IntArray

    private val mBitmapPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }
    private val mGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntersectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // ω
    private var omegaV = 0.0
    private var omegaH = 0.0

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

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BitmapCurtainView, defStyleAttr, defStyleRes)
        bitmap = a.getDrawable(R.styleable.BitmapCurtainView_android_src)?.toBitmap()
        mMeshWidth = a.getInteger(R.styleable.BitmapCurtainView_meshRow, DEFAULT_MESH_WIDTH)
        mMeshHeight = a.getInteger(R.styleable.BitmapCurtainView_meshColumn, DEFAULT_MESH_HEIGHT)
        mMaskColor = a.getColor(R.styleable.BitmapCurtainView_meshMaskColor, DEFAULT_MASK_COLOR)
        debug = a.getBoolean(R.styleable.BitmapCurtainView_debug, false)

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

    private fun initView() {
        globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    makeCoordinates(mRowMajorWarpCoordinates)
                    makeCoordinates(mRowMajorOriginalCoordinates)
                    omegaV = PI2 * 2.5 / (width.toFloat() - paddingStart - paddingEnd)
                    omegaH = PI2 * 0.5 / (height.toFloat() - paddingTop - paddingBottom)

                    invalidate()
                }

        touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                            percent = (event.x - paddingStart) / (width - paddingStart - paddingEnd)
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
     * 其中, Δx1 = ΔX1max * percent = (width - paddingEnd - 原始x) * percent
     * Δx1是正常的线性移动, Δx2实现"扭曲".
     *
     *   0 -----> x
     *   |
     *   | y
     *   ∨
     *
     * "横向扭曲"的实现依然是依赖sin函数, 只不过x与y互换位置, 变成了已知y求x.
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
     * x原Proportion = (x原 - paddingStart) / (width - paddingStart - paddingEnd)
     * 于是:
     * Δx2 = ΔX2max * 4 * (1 - percent） * percent * (1 - x原Proportion)
     *
     *
     *
     * 关于阴影:
     * 如果看上去有立体的效果, 我们还需要在褶皱处加上"阴影".
     *
     * (ΔYmax * percent) / waveHeight ∈ [-1, 1]
     * 其中[-1, 0)从最暗向白色过渡, [0, 1]为白色
     *
     * blackRatio =  min((ΔYmax * percent) / waveHeight, 0)       ∈ [-1, 0]
     * blackRatio = -min((ΔYmax * percent) / waveHeight, 0)       ∈ [1, 0]
     * blackRatio = -min((ΔYmax * percent) / waveHeight, 0) * 0.2 ∈ [0.2, 0]
     * 0.2这个值的目的是不要太暗, 稍微有点就行
     */
    private fun makeWarpCoordinates() {
        mRowMajorOriginalCoordinates.forEachIndexed { row, rowCoordinates ->
            rowCoordinates.forEachIndexed { column, coordinate ->
                val deltaX1Max = width - paddingEnd - coordinate.first
                val deltax1 = deltaX1Max * percent

                val xProportion = (coordinate.first - paddingStart) / (width - paddingStart - paddingEnd)
                val deltaX2Max = WAVE_MAX_WIDHT * sin(omegaH * (coordinate.second - paddingTop)).toFloat()
                val deltax2 = deltaX2Max * 4 * (1 - percent) * percent * (1 - xProportion)

                val x = coordinate.first + deltax1 + deltax2

                val deltaYMax = WAVE_MAX_HEIGHT * sin(omegaV * (coordinate.first - paddingStart)).toFloat()
                val y = coordinate.second - deltaYMax * percent

                mRowMajorWarpCoordinates[row][column] = x to y

                val blackRatio = -min(deltaYMax * percent / WAVE_MAX_HEIGHT, 0f) * 0.2f
                val component = (255 * (1 - blackRatio)).toInt()
                mColors[(mMeshWidth + 1) * row + column] = Color.argb(255, component, component, component)
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
}