package com.usher.demo.awesome.decoration

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.item_sticky_header.view.*

class StickyHeaderActivity : BaseActivity(R.layout.activity_sticky_header, Theme.DARK_ONLY) {

    override fun initView() {
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
            list.map { i to "Header ${i + 1}" }
        }.flatten()

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = StickyHeaderAdapter(data.flatten())
        recyclerview.addItemDecoration(StickyHeaderDecoration(this, decorationData))
    }

    private class StickyHeaderAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_sticky_header, data) {
        override fun convert(holder: BaseViewHolder, content: String) {
            holder.itemView.content_textview.text = content
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
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }

        private val mHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            alpha = 0x66
            style = Paint.Style.FILL
        }

        private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 40f
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
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

        /**
         * 我们对header的绘制从普遍到特殊分三步进行处理:
         * 1. 在[getItemOffsets]中我们已经在每组item顶部将header的位置预留出来, 首先需要做的
         * 就是把header绘制在给他们预留的应地方. 此时看到的效果就是所有的header和item的滑动是同步的.
         * 2. 考虑到sticky header的效果, 我们需要将[RecyclerView]顶部item(首个可见的, 即索引为0的item)
         * 所在组的header固定绘制在[RecyclerView]的顶部, 不随着item的滑动而滑动. 此时已经有sticky header的效果了,
         * 但是随着滑动的继续, 下一组header会覆盖在上一组header的上面, 而不是有一种"推上去"的效果.
         * 3. 为了实现"下一个header将上一个header推上去"的效果, 当固定在顶部的header的bottom与组内最后
         * 一个item的bottom重合时, 也就是下一组的header已经"顶到了"固定在顶部的header时, 那么该header
         * 便不再固定在顶部, 而是随着最后一个item一起滑走.
         *
         * 需要注意的是, [RecyclerView]的item view是复用的, 所以我们进行遍历得到的索引是可见item view的索引,
         * 如果要获取该item view所对应的"真正的"索引的话, 需要调用[RecyclerView.getChildAdapterPosition]获取.
         */
        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val headerRect = Rect(parent.paddingStart, 0, parent.width - parent.paddingEnd, 0)

            parent.forEachIndexed { i, view ->
                if (i == 0) {
                    headerRect.run {
                        top = parent.paddingTop
                        bottom = parent.paddingTop + HEADER_HEIGHT
                    }

                    if (isLastViewInGroup(parent.getChildAdapterPosition(view))) {
                        val lastItemBottom = view.bottom

                        if (headerRect.bottom > lastItemBottom) {
                            headerRect.run {
                                top = lastItemBottom - HEADER_HEIGHT
                                bottom = lastItemBottom
                            }
                        }
                    }

                    drawHeader(c, headerRect, parent.getChildAdapterPosition(view))

                } else if (isFirstViewInGroup(parent.getChildAdapterPosition(view))) {
                    headerRect.run {
                        top = view.top - HEADER_HEIGHT
                        bottom = view.top
                    }

                    drawHeader(c, headerRect, parent.getChildAdapterPosition(view))
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