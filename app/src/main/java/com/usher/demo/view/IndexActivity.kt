package com.usher.demo.view

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_index.*

class IndexActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        initView()
    }

    private fun initView() {
        statusbar_view.updateLayoutParams { height = SystemUtil.getStatusBarHeight(this@IndexActivity) }

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

        val stickyHeaderDecoration = StickyHeaderDecoration(this, decorationData)

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = StickyHeaderAdapter(data.flatten())
        recyclerview.addItemDecoration(stickyHeaderDecoration)

        indexview.setData(data.mapIndexed { i, _ -> "${i + 1}" })

        /**
         * TODO: 当滑动IndexView时, 如果该索引没有数据如何处理?
         * TODO: 当滑动IndexView时, 如果该索引如法滑动到顶端如何处理?
         *
         * 当滑动IndexView时, 会出现对应的索引所在的组没有数据(比如说联系人中没有以U或V开头的数据)或是所在组的数据不足
         * 导致该组header无法固定在顶部(比如联系人中以Z开头的数据只有两三个, 那么固定在顶部的header其实是X组的).
         * 但是这不妨碍
         */

        /*
        * 当滑动IndexView引发索引发生变化时:
        * 1. 找到该组的第一个数据对应的索引值, 然后将该item滑动至顶部, 由于该item是组内的第一个item, 因此header自然
        * 会出现在顶部.
        * 2. 将indicator的margin top调整至IndexView对应索引所在的位置, 该值为:
        * indexview_item高度*i + indexview_item高度/2 - indicator高度/2 + indexview.top
        *
        * 注意, 不要使用distinctUntilChanged:
        * 1. IndexView已经做了处理, 当index没有发生变化时是不会emit的.
        * 2. IndexView的indexChanges是滑动IndexView时被动接收index的变化, 同时也可以滑动RecyclerView主动调用
        * IndexView的changeIndex, 因此, 如果两次接收到了相同的index, 这只能说明.
        */
        indexview.indexChanges()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { index ->
                    val position = decorationData.indexOfFirst { it.first == index }
                    (recyclerview.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)

                    val top = (index + 0.5) * (indexview.height.toFloat() / data.size) - indicator_textview.height * 0.5f + indexview.top
                    indicator_textview.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = top.toInt() }
                    val content = "${index + 1}"
                    indicator_textview.text = content
                }

        stickyHeaderDecoration.indexChanges()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { index ->
                    indexview.changeIndex(index)

                    val top = (index + 0.5) * (indexview.height.toFloat() / data.size) - indicator_textview.height * 0.5f + indexview.top
                    indicator_textview.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = top.toInt() }
                    val content = "${index + 1}"
                    indicator_textview.text = content
                }
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

        private val mIndexChangeSubject = PublishSubject.create<Int>()

        private val mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.colorPrimary)
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }

        private val mHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.colorPrimary)
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
         * 我们首先需要做的是遍历[RecyclerView]的item view, 需要注意的是, [RecyclerView]的item view是复用的,
         * 所以我们得到的索引值是可见item view的索引值, 如果要获取该item view所对应的"真正的"索引值的话, 需要调用
         * [RecyclerView.getChildAdapterPosition]获取.
         *
         * 我们对header的绘制从普遍到特殊分三步进行处理:
         * 1. 在[getItemOffsets]中我们已经在每组item顶部将header的位置预留出来, 首先需要做的
         * 就是把header绘制在给他们预留的应地方. 此时看到的效果就是所有的header和item的滑动是同步的.
         * 2. 考虑到sticky header的效果, 我们需要将[RecyclerView]顶部item(第一个可见的, 即索引为0的item)
         * 所在组的header固定绘制在[RecyclerView]的顶部, 不随着item的滑动而滑动. 此时已经有sticky header的效果了,
         * 但是随着滑动的继续, 下一组header会覆盖在上一组header的上面, 而不是有一种"推上去"的效果.
         * 3. 为了实现"下一个header将上一个header推上去"的效果, 当固定在顶部的header的bottom与组内最后
         * 一个item的bottom重合时, 也就是下一组的header已经"顶到了"固定在顶部的header时, 那么该header
         * 便不再固定在顶部, 而是随着最后一个item一起滑走.
         *
         * 需要注意的是, [RecyclerView]的item view是复用的, 所以我们进行遍历得到的索引值是可见item view的索引值,
         * 如果要获取该item view所对应的"真正的"索引值的话, 需要调用[RecyclerView.getChildAdapterPosition]获取.
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

            if (parent.isNotEmpty()) {
                val index = data[parent.getChildAdapterPosition(parent[0])].first
                mIndexChangeSubject.onNext(index)
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

        fun indexChanges() = mIndexChangeSubject
    }
}