package com.usher.demo.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
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

        val serverData = listOf(
                "A" to listOf(*resources.getStringArray(R.array.sticky_a)),
                "B" to listOf(*resources.getStringArray(R.array.sticky_b)),
                "C" to listOf(*resources.getStringArray(R.array.sticky_c)),
                "D" to listOf(*resources.getStringArray(R.array.sticky_d)),
                "E" to listOf(*resources.getStringArray(R.array.sticky_e)),
                "H" to listOf(*resources.getStringArray(R.array.sticky_h)),
                "W" to listOf(*resources.getStringArray(R.array.sticky_w)),
                "Z" to listOf(*resources.getStringArray(R.array.sticky_z))
        )

        val decorationData = serverData.map { pair ->
            pair.second.map { pair.first }
        }.flatten()

        val adapterData = serverData.map { pair ->
            pair.second
        }.flatten()

        val indexData = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")

        val stickyHeaderDecoration = StickyHeaderDecoration(this, decorationData)

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = StickyHeaderAdapter(adapterData)
        recyclerview.addItemDecoration(stickyHeaderDecoration)

        indexview.setData(indexData)

        val showAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 100
            interpolator = LinearInterpolator()
            addUpdateListener {
                indicator_textview.pivotX = indicator_textview.width.toFloat()
                indicator_textview.scaleX = animatedValue as Float
                indicator_textview.scaleY = animatedValue as Float
            }
        }

        val hideAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 100
            startDelay = 500
            interpolator = LinearInterpolator()
            addUpdateListener {
                indicator_textview.pivotX = indicator_textview.width.toFloat()
                indicator_textview.scaleX = animatedValue as Float
                indicator_textview.scaleY = animatedValue as Float
            }
        }

        /**
         * 滑动IndexView处理
         *
         * 位置处理:
         * 1. 根据IndexView当前的index, 找到组内首个数据的position, 然后将对应位置的item滑动至顶部, 由于该item
         * 是组内的首个item, 因此该组的header自然会出现在顶部.
         * 2. 将indicator对准IndexView当前index对应位置的item. 调整后的margin top值为:
         * indexview_item高度*i + indexview_item高度/2 - indicator高度/2 + indexview.top
         *
         * 注意:
         * 1. 当滑动IndexView时, 会出现对应的索引所在组没有数据(比如说联系人中没有以U或V开头的数据)或是所在组及之后
         * 所有组的数据不足一屏(比如联系人中以Z开头的数据只有两三个, 那么固定在顶部的header其实是Y组的, Z组的header
         * 无法固定在顶部), 但是这不妨碍indicator"对准"IndexView对应索引的位置.
         * (1) 如果所在组没有数据: position为-1, [LinearLayoutManager.scrollToPositionWithOffset]中对
         * position的要求是"starting at 0", 尽管不会看到任何滑动发生, 但是我们依然要调用该方法, 因为这样会触发
         * [RecyclerView.requestLayout], 进而触发[RecyclerView.ItemDecoration.onDrawOver], 这样我们才有
         * 机会对IndexView当前的index进行"纠错".
         * (2) 如果所在组数据不足无法滑动到顶端: 与上面类似, 虽然RecyclerView发生了滑动, 但是没有滑动到预期的位置,
         * 我们依然需要对IndexView当前的index进行"纠错".
         *
         * 动画处理:
         * 1. MOVE: 不处理, 也就是说让indicator一直显示.
         * 2. UP: 开始indicator"消失"动画.
         * 3. DOWN: 正常情况下开始indicator"显示"动画. 但是要考虑一种情况, 就是刚刚经历了UP, 此时的"消失"动画刚刚
         * 开始, 也就是说indicator并没有消失, 此时如果开始indicator"显示"动画, 那么会让已经出现的indicator重新
         * 再出现一遍, 如果频繁的进行索引切换操作的话, 用户体验就变得非常糟糕. 在这种情况下我们需要做的是, 结束"消失"
         * 动画, 让indicator继续显示. 考虑到[ValueAnimator.end]会让indicator的scale直接变为动画结尾的状态,
         * 也就是0, 因此我们需要将indicator的scale调整至1.
         */
        indexview.touches()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { action ->
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (hideAnimator.isStarted) {
                                hideAnimator.end()
                                indicator_textview.scaleX = 1f
                                indicator_textview.scaleY = 1f
                            } else {
                                showAnimator.start()
                            }
                        }
                        MotionEvent.ACTION_UP -> hideAnimator.start()
                    }

                    val index = indexview.index
                    val value = indexData[index]
                    val position = decorationData.indexOfFirst { it == value }
                    (recyclerview.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)

                    val top = (index + 0.5f) * (indexview.height.toFloat() / indexData.size) - indicator_textview.height * 0.5f + indexview.top
                    indicator_textview.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = top.toInt() }
                    indicator_textview.text = value
                }

        /**
         * 滑动RecyclerView处理
         * 1. 更新IndexView当前的index.
         * 2. 将indicator对准IndexView当前index对应位置的item(参见对IndexView.touches的处理).
         *
         * 注意:
         * 在处理IndexView.touches的过程中会调用[LinearLayoutManager.scrollToPositionWithOffset],
         * 但这是"被动"滑动, 与"主动"滑动的区别在于, "被动"滑动的dy为0, 这样我们就可以将"被动"滑动filter掉.
         */
        recyclerview.scrollEvents()
                .filter { it.dy != 0 }
                .filter { recyclerview.isNotEmpty() }
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    val position = recyclerview.getChildAdapterPosition(recyclerview[0])
                    val value = decorationData[position]
                    val index = indexData.indexOf(value)
                    indexview.index = index

                    val top = (index + 0.5f) * (indexview.height.toFloat() / indexData.size) - indicator_textview.height * 0.5f + indexview.top
                    indicator_textview.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = top.toInt() }
                    indicator_textview.text = value
                }

        /**
         * "纠错"处理
         *
         * 当滑动IndexView时, 会出现对应的索引所在组及之后所有组的数据不足一屏(比如联系人中以Z开头的数据只有两三个,
         * 那么固定在顶部的header其实是Y组的, Z组的header无法固定在顶部), 这时需要对IndexView当前的index进行"纠错".
         *
         * 过程如下:
         * 1. 滑动IndexView: 滑动IndexView至"Z"时, IndexView当前index变为"Z".
         * 2. RecyclerView滑动: 处理IndexView.touches, 调用[LinearLayoutManager.scrollToPositionWithOffset].
         * 3. Decoration反馈: RecyclerView的滑动触发了[RecyclerView.requestLayout], 进而触发
         * [RecyclerView.ItemDecoration.onDrawOver], Decoration将该事件emit出来.
         * 4. 索引纠错: RecyclerView顶部item数据所在的组为"Y", 将该索引重新赋给IndexView.
         *
         * 注意: 虽然是"纠错", 一般来说只有在数据"缺失"的情况下才真正需要该操作, 即便IndexView当前的index没有错,
         * 更新一次也不会有任何副作用. Just in case.
         */
        stickyHeaderDecoration.scrolls()
                .filter { recyclerview.isNotEmpty() }
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    val position = recyclerview.getChildAdapterPosition(recyclerview[0])
                    val value = decorationData[position]
                    val index = indexData.indexOf(value)
                    indexview.index = index
                }
    }

    private class StickyHeaderAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_index, data) {
        override fun convert(helper: BaseViewHolder, content: String) {
            (helper.itemView as TextView).text = content
        }
    }

    private class StickyHeaderDecoration(private val context: Context, private val data: List<String>) : RecyclerView.ItemDecoration() {
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
            c.drawText(data[position], HEADER_LEFT.toFloat(), rect.exactCenterY() - metrics.top / 2 - metrics.bottom / 2, mTextPaint)
        }

        private fun isFirstViewInGroup(position: Int) =
                position == 0 || data[position] != data[position - 1]

        private fun isLastViewInGroup(position: Int) =
                position == data.size - 1 || data[position] != data[position + 1]

        fun scrolls() = mScrollSubject
    }
}