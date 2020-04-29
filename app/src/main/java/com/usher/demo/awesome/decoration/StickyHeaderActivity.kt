package com.usher.demo.awesome.decoration

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.SystemUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_sticky_header.*

class StickyHeaderActivity : BaseActivity(Theme.DARK_ONLY) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_header)
        initView()
    }

    private fun initView() {
        statusbar_view.updateLayoutParams { height = SystemUtil.getStatusBarHeight(this@StickyHeaderActivity) }

        val data = listOf(
                listOf(*resources.getStringArray(R.array.sticky_list1)),
                listOf(*resources.getStringArray(R.array.sticky_list2)),
                listOf(*resources.getStringArray(R.array.sticky_list3)),
                listOf(*resources.getStringArray(R.array.sticky_list4)),
                listOf(*resources.getStringArray(R.array.sticky_list5)),
                listOf(*resources.getStringArray(R.array.sticky_list6)),
                listOf(*resources.getStringArray(R.array.sticky_list7))
        )

        val decorationData = data.mapIndexed { i, list ->
            list.map { i to "Header $i" }
        }.flatten()

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = StickyHeaderAdapter(data.flatten())
        recyclerview.addItemDecoration(StickyHeaderDecoration(this, decorationData))
    }

    private class StickyHeaderAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_sticky_header, data) {
        override fun convert(helper: BaseViewHolder, content: String) {
            helper.setText(R.id.content_textview, content)
        }
    }

    private class StickyHeaderDecoration(private val context: Context, private val data: List<Pair<Int, String>>) : RecyclerView.ItemDecoration() {
        companion object {
            private const val DIVIDER_HEIGHT = 3 * 3
            private const val HEADER_HEIGHT = 30 * 3
            private const val OFFSET_LEFT = 45 * 3
            private const val SIDE_LENGTH = 30 * 3
            private const val CIRCLE_RADIUS = 15 * 3f
        }

        private val mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.colorPrimary)
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }

        private val mHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.colorPrimary)
            style = Paint.Style.FILL
        }

        private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 40f
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//            outRect.top = DIVIDER_HEIGHT.takeIf { parent.getChildAdapterPosition(view) > 0 } ?: 0
            outRect.top = HEADER_HEIGHT.takeIf { isFirstViewInGroup(parent.getChildAdapterPosition(view)) }
                    ?: DIVIDER_HEIGHT
            outRect.left = OFFSET_LEFT
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            parent.forEach { view ->
                val centerX = (parent.paddingStart + OFFSET_LEFT / 2).toFloat()
                val centerY = (view.top + view.height / 2).toFloat()

                val bitmap = context.resources.getDrawable(R.drawable.ic_face_primary, null).toBitmap()
                val dst = RectF()
                dst.top = centerY - SIDE_LENGTH / 2
                dst.bottom = centerY + SIDE_LENGTH / 2
                dst.left = centerX - SIDE_LENGTH / 2
                dst.right = centerX + SIDE_LENGTH / 2

                c.drawBitmap(bitmap, null, dst, null)
                c.drawCircle(centerX, centerY, CIRCLE_RADIUS, mStrokePaint)
                c.drawLine(centerX, centerY - CIRCLE_RADIUS, centerX, view.top.toFloat() /*- DIVIDER_HEIGHT*/, mStrokePaint)
                c.drawLine(centerX, centerY + CIRCLE_RADIUS, centerX, view.bottom.toFloat(), mStrokePaint)
            }
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            var top = 0
            var bottom = 0
            val left = parent.paddingStart
            val right = parent.width - parent.paddingEnd

            parent.forEachIndexed { i, view ->
                if (i == 0) {
                    top = parent.paddingTop
                    bottom = top + HEADER_HEIGHT

                    val rect = Rect(left, top, right, bottom)

                    drawHeader(c, rect, parent.getChildAdapterPosition(view))

                } else if (isFirstViewInGroup(parent.getChildAdapterPosition(view))) {
                    top = view.top - HEADER_HEIGHT
                    bottom = view.top

                    val rect = Rect(left, top, right, bottom)

                    drawHeader(c, rect, parent.getChildAdapterPosition(view))
                }
            }
        }

        private fun drawHeader(c: Canvas, rect: Rect, position: Int) {
            c.drawRect(rect, mHeaderPaint)

            val metrics = mTextPaint.fontMetrics
            c.drawText(data[position].second, rect.exactCenterX(), rect.exactCenterY() - metrics.top / 2 - metrics.bottom / 2, mTextPaint)
        }

        private fun isFirstViewInGroup(position: Int) =
                position == 0 || data[position].first != data[position - 1].first

        private fun isLastViewInGroup(position: Int) =
                position == data.size - 1 || data[position].first != data[position + 1].first

    }
}