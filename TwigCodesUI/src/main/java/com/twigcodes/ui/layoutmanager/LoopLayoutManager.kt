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

    }

    /**
     * [measureChildWithMargins]将以下均考虑在内:
     * 1. [RecyclerView]的padding.
     * 2. [RecyclerView.ItemDecoration.getItemOffsets]的left/top/right/bottom.
     * 3. itemView的margin.
     * 也就是说child.measureWidth/Height得到的是"真实"的宽度或高度.
     *
     * 注: 以下将Decoration Offsets简称为insets.
     * [getDecoratedMeasuredWidth]得到的是child.measureWidth + insets.left + insets.right
     * [getDecoratedMeasuredHeight]得到的是child.measureHeight + insets.top + insets.bottom
     *
     * [layoutDecorated]
     * child.layout(
     * left + insets.left,
     * top - insets.top,
     * right - insets.right,
     * bottom - insets.bottom)
     *
     * [layoutDecoratedWithMargins]
     * child.layout(
     * left + insets.left + lp.leftMargin,
     * top + insets.top + lp.topMargin,
     * right - insets.right - lp.rightMargin,
     * bottom - insets.bottom - lp.bottomMargin)
     *
     * left = paddingStart
     * top = paddingTop (初始值)
     * right = left + getDecoratedMeasuredWidth + lp.leftMargin + lp.rightMargin
     *       = paddingLeft + child.measureWidth + insets.left + insets.right + lp.leftMargin + lp.rightMargin
     * bottom = top + getDecoratedMeasuredHeight + lp.topMargin + lp.bottomMargin
     *        = lastBottom + child.measureHeight + insets.top + insets.bottom + lp.topMargin + lp.bottomMargin
     * ⬇
     * child.layout(
     * paddingStart + insets.left + lp.leftMargin
     * top + insets.top + lp.topMargin
     * paddingStart + insets.left + lp.leftMargin + measureWidth
     * top + insets.top + lp.topMargin + measureHeight
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
            val left = paddingStart
            val right = left + getDecoratedMeasuredWidth(itemView) + params.leftMargin + params.rightMargin
            val bottom = top + getDecoratedMeasuredHeight(itemView) + params.topMargin + params.bottomMargin

            layoutDecoratedWithMargins(itemView, left, top, right, bottom)

            top = getDecoratedBottom(itemView) + params.bottomMargin
        }
    }
}