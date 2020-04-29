package com.usher.demo.awesome.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
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
        val data = listOf(
                listOf(*resources.getStringArray(R.array.sticky_list1)),
                listOf(*resources.getStringArray(R.array.sticky_list2)),
                listOf(*resources.getStringArray(R.array.sticky_list3)),
                listOf(*resources.getStringArray(R.array.sticky_list4)),
                listOf(*resources.getStringArray(R.array.sticky_list5)),
                listOf(*resources.getStringArray(R.array.sticky_list6)),
                listOf(*resources.getStringArray(R.array.sticky_list7))
        ).mapIndexed { i, list ->
            list.map { content -> ItemInfo("$i", "GROUP $i", content) }
        }.flatten()

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.addItemDecoration(StickyHeaderDecoration(this, data))
        recyclerview.adapter = StickyHeaderAdapter(data)
    }

    private class StickyHeaderAdapter(data: List<ItemInfo>) : RxBaseQuickAdapter<ItemInfo, BaseViewHolder>(R.layout.item_sticky_header, data) {
        override fun convert(helper: BaseViewHolder, item: ItemInfo) {
            helper.setText(R.id.content_textview, item.content)
        }
    }

    private class StickyHeaderDecoration(private val context: Context, private val data: List<ItemInfo>) : RecyclerView.ItemDecoration() {
        companion object {
            private const val DIVIDER_HEIGHT = 3 * 3
            private const val HEADER_HEIGHT = 40 * 3
            private const val OFFSET_LEFT = 45 * 3
            private const val SIDE_LENGTH = 30 * 3
            private const val CIRCLE_RADIUS = 15 * 3f
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = DIVIDER_HEIGHT.takeIf { parent.getChildAdapterPosition(view) > 0 } ?: 0
            outRect.left = OFFSET_LEFT
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = context.getColor(R.color.colorPrimary)
                style = Paint.Style.STROKE
                strokeWidth = 6f
            }

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
                c.drawCircle(centerX, centerY, CIRCLE_RADIUS, strokePaint)
                c.drawLine(centerX, centerY - CIRCLE_RADIUS, centerX, view.top.toFloat() - DIVIDER_HEIGHT, strokePaint)
                c.drawLine(centerX, centerY + CIRCLE_RADIUS, centerX, view.bottom.toFloat(), strokePaint)

            }
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        }
    }
}