package com.twigcodes.ui.layoutmanager

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LoopLayoutManager(@RecyclerView.Orientation private val mOrientation: Int) : RecyclerView.LayoutManager() {
    companion object {
        const val VERTICAL = RecyclerView.VERTICAL
        const val HORIZONTAL = RecyclerView.HORIZONTAL
    }

    private var mFirstView: View? = null

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun canScrollHorizontally(): Boolean = mOrientation == HORIZONTAL

    override fun canScrollVertically(): Boolean = mOrientation == VERTICAL

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0)
            return

        //动画相关
        if (state.isPreLayout)
            return

        detachAndScrapAttachedViews(recycler)

        when (mOrientation) {
            HORIZONTAL -> layoutHorizontalChildren(recycler)
            else -> layoutVerticalChildren(recycler)
        }
    }

    private fun layoutHorizontalChildren(recycler: RecyclerView.Recycler) {
        var left = paddingStart

        for (i in 0 until itemCount) {
            val itemView = recycler.getViewForPosition(i)

            addView(itemView)
            measureChildWithMargins(itemView, 0, 0)

            val params = itemView.layoutParams as RecyclerView.LayoutParams
            val right = left + getDecoratedMeasuredWidth(itemView) + params.leftMargin + params.rightMargin
            val top = paddingTop
            val bottom = top + getDecoratedMeasuredHeight(itemView) + params.topMargin + params.bottomMargin

            layoutDecoratedWithMargins(itemView, left, top, right, bottom)

            left = getDecoratedRight(itemView) + params.rightMargin

            if (left > width - paddingEnd)
                break
        }
    }

    /**
     * [measureChildWithMargins]将以下均考虑在内:
     * 1. [RecyclerView]的padding.
     * 2. [RecyclerView.ItemDecoration.getItemOffsets]的left/top/right/bottom.
     * 3. itemView的margin.
     * 也就是说child.measuredWidth/Height得到的是"真实"的宽度或高度.
     *
     * 注: 以下将Decoration Offsets简称为insets.
     * [getDecoratedMeasuredWidth]得到的是child.measuredWidth + insets.left + insets.right
     * [getDecoratedMeasuredHeight]得到的是child.measuredHeight + insets.top + insets.bottom
     *
     * [layoutDecorated]
     * child.layout(
     * left + insets.left,
     * top - insets.top,
     * right - insets.right,
     * bottom - insets.bottom)
     *
     * left = paddingStart + lp.leftMargin
     * top = top(初始值为paddingTop) + lp.topMargin
     * right = left + getDecoratedMeasuredWidth
     *       = paddingStart + child.measuredWidth + insets.left + insets.right + lp.leftMargin
     * bottom = top + getDecoratedMeasuredHeight
     *        = top + child.measuredHeight + insets.top + insets.bottom
     * ⬇
     * child.layout(
     * paddingStart + insets.left + lp.leftMargin,
     * top + insets.top,
     * paddingStart + insets.left + lp.leftMargin + child.measuredWidth,
     * top + insets.top + child.measuredHeight)
     * ⬇
     * top = getDecoratedBottom + lp.bottomMargin
     *     = child.bottom + insets.bottom + lp.bottomMargin
     *
     * [layoutDecoratedWithMargins]
     * child.layout(
     * left + insets.left + lp.leftMargin,
     * top + insets.top + lp.topMargin,
     * right - insets.right - lp.rightMargin,
     * bottom - insets.bottom - lp.bottomMargin)
     *
     * left = paddingStart
     * top = top(初始值为paddingTop)
     * right = left + getDecoratedMeasuredWidth + lp.leftMargin + lp.rightMargin
     *       = paddingStart + child.measuredWidth + insets.left + insets.right + lp.leftMargin + lp.rightMargin
     * bottom = top + getDecoratedMeasuredHeight + lp.topMargin + lp.bottomMargin
     *        = top + child.measuredHeight + insets.top + insets.bottom + lp.topMargin + lp.bottomMargin
     * ⬇
     * child.layout(
     * paddingStart + insets.left + lp.leftMargin
     * top + insets.top + lp.topMargin
     * paddingStart + insets.left + lp.leftMargin + child.measuredWidth
     * top + insets.top + lp.topMargin + child.measuredHeight)
     * ⬇
     * top = getDecoratedBottom + lp.bottomMargin
     *     = child.bottom + insets.bottom + lp.bottomMargin
     */
    private fun layoutVerticalChildren(recycler: RecyclerView.Recycler) {
        var top = paddingTop

        for (i in 0 until itemCount) {
            val itemView = recycler.getViewForPosition(i)

            addView(itemView)
            measureChildWithMargins(itemView, 0, 0)

            val params = itemView.layoutParams as RecyclerView.LayoutParams

            /*
            val left = paddingStart + params.leftMargin
            top += params.topMargin
            val right = left + getDecoratedMeasuredWidth(itemView)
            val bottom = top + getDecoratedMeasuredHeight(itemView)

            layoutDecorated(itemView, left, top, right, bottom)
             */

            val left = paddingStart
            val right = left + getDecoratedMeasuredWidth(itemView) + params.leftMargin + params.rightMargin
            val bottom = top + getDecoratedMeasuredHeight(itemView) + params.topMargin + params.bottomMargin

            layoutDecoratedWithMargins(itemView, left, top, right, bottom)

            top = getDecoratedBottom(itemView) + params.bottomMargin

            if (top > height - paddingBottom)
                break
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || dx == 0)
            return 0

        fillHorizontal(dx, recycler)
        offsetChildrenHorizontal(-dx)
        recycleChildrenHorizontal(dx, recycler)

        mFirstView = getChildAt(0)

        return dx
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || dy == 0)
            return 0

        fillVertical(dy, recycler)
        offsetChildrenVertical(-dy)
        recycleChildrenVertical(dy, recycler)

        mFirstView = getChildAt(0)

        return dy
    }

    /**
     * dx>0 向左滑动
     * last_view_end_x = right + insets.right + lp.rightMargin
     * 如果last_view_x在向左滑动dx后小于width - paddingEnd, 说明最右侧会有空间"露出来", 因此需要补充相应的item.
     *
     * dx<0 向右滑动
     * first_view_start_x = left - insets.left - lp.leftMargin
     * 如果first_view_x在向右滑动dx后大于paddingStart, 说明最左侧会有空间"露出来", 因此需要补充相应的item.
     */
    private fun fillHorizontal(dx: Int, recycler: RecyclerView.Recycler) {
        if (dx > 0) {
            while (true) {
                val lastView = getChildAt(childCount - 1) ?: return

                val lastViewEnd = getDecoratedRight(lastView) + (lastView.layoutParams as RecyclerView.LayoutParams).rightMargin
                if (lastViewEnd - dx < width - paddingEnd) {
                    val lastViewPosition = getPosition(lastView)
                    val scrap = recycler.getViewForPosition((lastViewPosition + 1) % itemCount)

                    addView(scrap)
                    measureChildWithMargins(scrap, 0, 0)

                    val params = scrap.layoutParams as RecyclerView.LayoutParams
                    val top = paddingTop
                    val bottom = top + getDecoratedMeasuredHeight(scrap) + params.topMargin + params.bottomMargin
                    val left = lastViewEnd
                    val right = left + getDecoratedMeasuredWidth(scrap) + params.leftMargin + params.rightMargin

                    layoutDecoratedWithMargins(scrap, left, top, right, bottom)
                } else {
                    break
                }
            }
        } else {
            while (true) {
                val firstView = getChildAt(0) ?: return

                val firstViewStart = getDecoratedLeft(firstView) - (firstView.layoutParams as RecyclerView.LayoutParams).leftMargin
                if (firstViewStart - dx > paddingStart) {
                    val firstViewPosition = getPosition(firstView)
                    val scrap = recycler.getViewForPosition((firstViewPosition - 1 + itemCount) % itemCount)

                    addView(scrap, 0)
                    measureChildWithMargins(scrap, 0, 0)

                    val params = scrap.layoutParams as RecyclerView.LayoutParams
                    val top = paddingTop
                    val bottom = top + getDecoratedMeasuredHeight(scrap) + params.topMargin + params.bottomMargin
                    val right = firstViewStart
                    val left = right - getDecoratedMeasuredWidth(scrap) - params.leftMargin - params.rightMargin

                    layoutDecoratedWithMargins(scrap, left, top, right, bottom)
                } else {
                    break
                }
            }
        }
    }

    /**
     * dy>0 向上滑动
     * last_view_end_y = bottom + insets.bottom + lp.bottomMargin
     * 如果last_view_end_y在向上滑动dy后小于height - paddingBottom, 说明底部会有空间"漏出来", 因此需要补充相应的item.
     *
     * dy<0 向下滑动
     * first_view_start_y = top - insets.top - lp.topMargin
     * 如果first_view_start_y在向下滑动dy后大于paddingTop, 说明顶部会有空间"露出来", 因此需要补充相应的item.
     */
    private fun fillVertical(dy: Int, recycler: RecyclerView.Recycler) {
        if (dy > 0) {
            while (true) {
                val lastView = getChildAt(childCount - 1) ?: return

                val lastViewEnd = getDecoratedBottom(lastView) + (lastView.layoutParams as RecyclerView.LayoutParams).bottomMargin
                if (lastViewEnd - dy < height - paddingBottom) {
                    val lastViewPosition = getPosition(lastView)
                    val scrap = recycler.getViewForPosition((lastViewPosition + 1) % itemCount)

                    addView(scrap)
                    measureChildWithMargins(scrap, 0, 0)

                    val params = scrap.layoutParams as RecyclerView.LayoutParams
                    val left = paddingStart
                    val right = left + getDecoratedMeasuredWidth(scrap) + params.leftMargin + params.rightMargin
                    val top = lastViewEnd
                    val bottom = top + getDecoratedMeasuredHeight(scrap) + params.topMargin + params.bottomMargin

                    layoutDecoratedWithMargins(scrap, left, top, right, bottom)
                } else {
                    break
                }
            }
        } else {
            while (true) {
                val firstView = getChildAt(0) ?: return

                val firstViewStart = getDecoratedTop(firstView) - (firstView.layoutParams as RecyclerView.LayoutParams).topMargin
                if (firstViewStart - dy > paddingTop) {
                    val firstViewPosition = getPosition(firstView)
                    val scrap = recycler.getViewForPosition((firstViewPosition - 1 + itemCount) % itemCount)

                    addView(scrap, 0)
                    measureChildWithMargins(scrap, 0, 0)

                    val params = scrap.layoutParams as RecyclerView.LayoutParams
                    val left = paddingStart
                    val right = left + getDecoratedMeasuredWidth(scrap) + params.leftMargin + params.rightMargin
                    val bottom = firstViewStart
                    val top = bottom - getDecoratedMeasuredHeight(scrap) - params.topMargin - params.bottomMargin

                    layoutDecoratedWithMargins(scrap, left, top, right, bottom)
                } else {
                    break
                }
            }
        }
    }

    /**
     * dy>0 向左滑动
     * view_end_x = right + insets.right + lp.rightMargin
     * 如果view_end_x在向左滑动dx后小于paddingStart, 说明该view已经离开可视范围, 因此需要移除并回收.
     *
     * dy<0 向右滑动
     * view_start_x = left - insets.left - lp.leftMargin
     * 如果view_start_x在向右滑动dx后大于width - paddingEnd, 说明该view已经离开可视范围, 因此需要移除并回收.
     */
    private fun recycleChildrenHorizontal(dx: Int, recycler: RecyclerView.Recycler) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val params = view.layoutParams as RecyclerView.LayoutParams

            if (dx > 0) {
                if (getDecoratedRight(view) + params.rightMargin < paddingStart)
                    removeAndRecycleView(view, recycler)
            } else {
                if (getDecoratedLeft(view) + params.leftMargin > width - paddingEnd)
                    removeAndRecycleView(view, recycler)
            }
        }
    }

    /**
     * dy>0 向上滑动
     * view_end_y = bottom + insets.bottom + lp.bottomMargin
     * 如果view_end_y在向上滑动dy后小于paddingTop, 说明该view已经离开可视范围, 因此需要移除并回收.
     *
     * dy<0 向下滑动
     * view_start_y = top - insets.top - lp.topMargin
     * 如果view_start_y在向下滑动dy后大于height - paddingBottom, 说明该view已经离开可视范围, 因此需要移除并回收.
     */
    private fun recycleChildrenVertical(dy: Int, recycler: RecyclerView.Recycler) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val params = view.layoutParams as RecyclerView.LayoutParams

            if (dy > 0) {
                if (getDecoratedBottom(view) + params.bottomMargin < paddingTop)
                    removeAndRecycleView(view, recycler)
            } else {
                if (getDecoratedTop(view) - params.topMargin > height - paddingBottom)
                    removeAndRecycleView(view, recycler)
            }
        }
    }

    fun getFirstViewPosition(correct: Boolean = false): Int =
            when (mOrientation) {
                HORIZONTAL -> getFirstViewPositionHorizontal(correct)
                else -> getFirstViewPositionVertical(correct)
            }

    /**
     * end - start = getDecoratedMeasuredWidth + lp.leftMargin + lp.rightMargin
     * 比较first_view_end_x和(end - start)/2:
     * 如果大于, 说明只有不到一半的view离开可视范围, 需要向右滑动, position不变.
     * 否则, 说明有超过一半的view离开可视范围, 需要向左滑动, position(position + 1) % itemCount.
     */
    private fun getFirstViewPositionHorizontal(correct: Boolean): Int {
        var position = -1

        mFirstView?.let { view ->
            position = getPosition(view)

            if (correct) {
                val params = view.layoutParams as RecyclerView.LayoutParams
                val start = getDecoratedLeft(view) - params.leftMargin
                val end = getDecoratedRight(view) + params.rightMargin

                if (end > (end - start) / 2) {
                    offsetChildrenHorizontal(-start)
                } else {
                    offsetChildrenHorizontal(-end)
                    position = (position + 1) % itemCount
                }
            }
        }

        return position
    }

    /**
     * end - start == getDecoratedMeasuredHeight + lp.topMargin + lp.bottomMargin
     * 比较first_view_end_y和(end - start)/2:
     * 如果大于, 说明只有不到一半的view离开可视范围, 需要向下滑动, position不变.
     * 否则, 说明有超过一半的view离开可视范围, 需要向上滑动, position为(position + 1) % itemCount.
     */
    private fun getFirstViewPositionVertical(correct: Boolean): Int {
        var position = -1

        mFirstView?.let { view ->
            position = getPosition(view)

            if (correct) {
                val params = view.layoutParams as RecyclerView.LayoutParams
                val start = getDecoratedTop(view) - params.topMargin
                val end = getDecoratedBottom(view) + params.bottomMargin

                if (end > (end - start) / 2) {
                    offsetChildrenVertical(-start)
                } else {
                    offsetChildrenVertical(-end)
                    position = (position + 1) % itemCount
                }
            }
        }

        return position
    }
}