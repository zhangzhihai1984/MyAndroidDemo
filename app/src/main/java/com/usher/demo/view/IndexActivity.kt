package com.usher.demo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
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
         */

        /**
         * 当滑动IndexView导致索引发生变化时:
         * 1. 找到属于该组的数据, 进而找到将组内第一个数据对应的索引, 然后将对应的item滑动至顶部, 由于该item是组内的
         * 第一个item, 因此该组的header自然会出现在顶部.
         * 2. 将indicator的margin top调整至IndexView对应索引所在的位置, 该值为:
         * indexview_item高度*i + indexview_item高度/2 - indicator高度/2 + indexview.top
         *
         * 注意:
         * 1. 不要使用distinctUntilChanged:
         * (1) IndexView已经做了处理, 当index没有发生变化时是不会emit的.
         * (2) IndexView的indexChanges是滑动IndexView时被动接收index的变化, 同时也可以滑动RecyclerView主动调用
         * IndexView的changeIndex, 因此, 如果两次接收到了相同的index, 这只能说是特定手顺操作带来的结果(比如通过滑动
         * IndexView让当前的索引指向"B", 然后向下滑动一下RecyclerView, 这时索引指向了"A", 然后再滑动IndexView,
         * 将索引重新指向"B", 这时indexChanges两次接收的索引均为"B", 但是索引确实发生了B->A->B的变化)
         *
         * 2. 当滑动IndexView时, 会出现对应的索引所在的组没有数据(比如说联系人中没有以U或V开头的数据)或是所在组的数据
         * 不足导致该组header无法固定在顶部(比如联系人中以Z开头的数据只有两三个, 那么固定在顶部的header其实是Y组的).
         * 但是这不妨碍indicator"对准"IndexView对应索引的位置.
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

        /**
         * 当滑动RecyclerView时:
         * 1. 调用IndexView的changeIndex
         * 2. 将indicator的margin top调整至IndexView对应索引所在的位置(参见对IndexView.indexChanges的处理)
         *
         * 注意:
         * 在滑动IndexView的过程中如果索引发生变化, 会调用[LinearLayoutManager.scrollToPositionWithOffset],
         * 也就是说RecyclerView会滑动, 但这是"被动"滑动, 与"主动"滑动的区别在于, "被动"滑动的dy为0.
         */
        recyclerview.scrollEvents()
                .filter { it.dy != 0 }
                .filter { recyclerview.isNotEmpty() }
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    val index = decorationData[recyclerview.getChildAdapterPosition(recyclerview[0])].first
                    indexview.changeIndex(index)

                    val top = (index + 0.5) * (indexview.height.toFloat() / data.size) - indicator_textview.height * 0.5f + indexview.top
                    indicator_textview.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = top.toInt() }
                    val content = "${index + 1}"
                    indicator_textview.text = content
                }

        /**
         * 当滑动IndexView时, 会出现对应的索引所在的组的数据不足导致该组header无法固定在顶部(比如联系人中以Z开头的数据
         * 只有两三个, 那么固定在顶部的header其实是Y组的), 这时候需要一个"纠错"的操作.
         *
         * 过程如下:
         * 1. 索引变化: 滑动IndexView至"Z"时, 触发indexChanges.
         * 2. RecyclerView滑动: 将"Z"组第一个数据对应的item滑动至recyclerview的顶部, 但由于数据不足, 固定在顶部的
         * header其实是Y组的, 顶部item的数据也是Y组的.
         * 3. Decoration反馈: RecyclerView的滑动触发了[RecyclerView.ItemDecoration.onDrawOver],
         * Decoration将该事件emit出来.
         * 4. 索引纠错: RecyclerView顶部item数据所在的组为"Y", 将该索引重新赋给IndexView
         */
        stickyHeaderDecoration.scrolls()
                .filter { recyclerview.isNotEmpty() }
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    val index = decorationData[recyclerview.getChildAdapterPosition(recyclerview[0])].first
                    indexview.changeIndex(index)
                }
    }

    private class StickyHeaderAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_index, data) {
        override fun convert(helper: BaseViewHolder, content: String) {
            (helper.itemView as TextView).text = content
        }
    }

    private class StickyHeaderDecoration(private val context: Context, private val data: List<Pair<Int, String>>) : RecyclerView.ItemDecoration() {
        companion object {
            private const val DIVIDER_HEIGHT = 1 * 3
            private const val HEADER_HEIGHT = 45 * 3
            private const val HEADER_LEFT = 20 * 3
        }

        private val mScrollSubject = PublishSubject.create<Unit>()

        private val mHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.item_background)
            style = Paint.Style.FILL
        }

        private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG).apply {
            color = context.getColor(R.color.text_secondary)
            textAlign = Paint.Align.LEFT
            textSize = 40f
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = if (isFirstViewInGroup(parent.getChildAdapterPosition(view))) HEADER_HEIGHT else 0
            outRect.bottom = if (isLastViewInGroup(parent.getChildAdapterPosition(view))) DIVIDER_HEIGHT else 0
        }

        /**
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
         * 需要注意的是, [RecyclerView]的item view是复用的, 所以我们进行遍历得到的索引是可见item view的索引,
         * 如果要获取该item view所对应的"真正的"索引的话, 需要调用[RecyclerView.getChildAdapterPosition]获取.
         */
        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            mScrollSubject.onNext(Unit)

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
            c.drawText(data[position].second, HEADER_LEFT.toFloat(), rect.exactCenterY() - metrics.top / 2 - metrics.bottom / 2, mTextPaint)
        }

        private fun isFirstViewInGroup(position: Int) =
                position == 0 || data[position].first != data[position - 1].first

        private fun isLastViewInGroup(position: Int) =
                position == data.size - 1 || data[position].first != data[position + 1].first

        fun scrolls() = mScrollSubject
    }
}