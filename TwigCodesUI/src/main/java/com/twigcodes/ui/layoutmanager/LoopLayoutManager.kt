package com.twigcodes.ui.layoutmanager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LoopLayoutManager(@RecyclerView.Orientation private val mOrientation: Int) : RecyclerView.LayoutManager() {
    companion object {
        const val VERTICAL = RecyclerView.VERTICAL
        const val HORIZONTAL = RecyclerView.HORIZONTAL
    }

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

        return dx
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || dy == 0)
            return 0

        fillVertical(dy, recycler)
        offsetChildrenVertical(-dy)
        recycleChildrenVertical(dy, recycler)

        return dy
    }

    /**
     * dx>0 向左滑动
     * dx<0 向右滑动
     */
    private fun fillHorizontal(dx: Int, recycler: RecyclerView.Recycler) {
        if (dx > 0) {
            while (true) {
                val lastView = getChildAt(childCount - 1) ?: return

                val lastViewEnd = getDecoratedRight(lastView) + (lastView.layoutParams as RecyclerView.LayoutParams).rightMargin
                if (lastViewEnd - dx < width) {

                }
            }
        } else {

        }
    }

    /**
     * dy>0 向上滑动
     * dy<0 向下滑动
     */
    private fun fillVertical(dy: Int, recycler: RecyclerView.Recycler) {
        if (dy > 0) {
            while (true) {
                val lastView = getChildAt(childCount - 1) ?: return

                val lastViewEnd = getDecoratedBottom(lastView) + (lastView.layoutParams as RecyclerView.LayoutParams).bottomMargin
                if (lastViewEnd - dy < height - paddingBottom) {
                    val lastViewPosition = getPosition(lastView)
                    val scrap = recycler.getViewForPosition(if (lastViewPosition == itemCount - 1) 0 else lastViewPosition + 1)
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
                    val scrap = recycler.getViewForPosition(if (firstViewPosition == 0) itemCount - 1 else firstViewPosition - 1)
                    addView(scrap, 0)

                    measureChildWithMargins(scrap, 0, 0)

                    val params = scrap.layoutParams as RecyclerView.LayoutParams
                    val left = paddingStart
                    val right = left + getDecoratedMeasuredWidth(scrap) + params.leftMargin + params.rightMargin
                    val bottom = firstViewStart
                    val top = bottom - getDecoratedMeasuredHeight(scrap) - params.topMargin - params.bottomMargin
                    // top = bottom - child.measuredHeight - marginBottom - insetsBottom - marginTop - insetsTop
                    layoutDecoratedWithMargins(scrap, left, top, right, bottom)
                } else {
                    break
                }
            }
        }
    }

    private fun recycleChildrenHorizontal(dx: Int, recycler: RecyclerView.Recycler) {

    }

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
}