package com.twigcodes.ui.layoutmanager;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class LoopLayoutManager2 extends RecyclerView.LayoutManager {
    private View mFirstView;
    private Orientaion mOrientation;

    public enum Orientaion {
        VERTICAL,
        HORIZONTAL
    }

    public LoopLayoutManager2() {
        mOrientation = Orientaion.VERTICAL;
    }

    public LoopLayoutManager2(Orientaion orientation) {
        mOrientation = orientation;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == Orientaion.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == Orientaion.VERTICAL;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0) {
            return;
        }
        //preLayout主要支持动画，直接跳过
        if (state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);

        if (mOrientation == Orientaion.HORIZONTAL)
            layoutHorizontalChildren(recycler);
        else
            layoutVerticalChildren(recycler);
    }

    private void layoutHorizontalChildren(RecyclerView.Recycler recycler) {
        int left = getPaddingStart();
        for (int i = 0; i < getItemCount(); i++) {
            View itemView = recycler.getViewForPosition(i);
            addView(itemView);

            measureChildWithMargins(itemView, 0, 0);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            int right = left + getDecoratedMeasuredWidth(itemView) + params.leftMargin + params.rightMargin;
            int top = getPaddingTop();
            int bottom = top + getDecoratedMeasuredHeight(itemView) + params.topMargin + params.bottomMargin;
            layoutDecoratedWithMargins(itemView, left, top, right, bottom);

            left = getDecoratedRight(itemView) + params.rightMargin;
            if (left > getWidth()) {
                break;
            }
        }
    }

    private void layoutVerticalChildren(RecyclerView.Recycler recycler) {
        int top = getPaddingTop();
        for (int i = 0; i < getItemCount(); i++) {
            View itemView = recycler.getViewForPosition(i);
            addView(itemView);

            measureChildWithMargins(itemView, 0, 0);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            int left = getPaddingStart();
            int right = left + getDecoratedMeasuredWidth(itemView) + params.leftMargin + params.rightMargin;
            int bottom = top + getDecoratedMeasuredHeight(itemView) + params.topMargin + params.bottomMargin;
            layoutDecoratedWithMargins(itemView, left, top, right, bottom);

            top = getDecoratedBottom(itemView) + params.bottomMargin;
            if (top > getHeight()) {
                break;
            }
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        fillHorizontal(dx, recycler);
        offsetChildrenHorizontal(dx * -1);
        reycleHorizontalOutView(dx, recycler);

        mFirstView = getChildAt(0);

        return dx;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        fillVertical(dy, recycler);
        offsetChildrenVertical(dy * -1);
        reycleVeriticalOutView(dy, recycler);

        mFirstView = getChildAt(0);

        return dy;
    }

    /**
     * dy > 0为向上滑动
     * 如果lastView.getBottom() - dy < getHeight(), 也就是说最后一个可见的item在向上移动dy后
     * 它的bottom小于了RecyclerView的高度, 说明底部有"空间"漏出来了, 需要补充新的item进来.
     * 此时需要判断一下lastView在
     * <p>
     * dy < 0为向下滑动
     */
    private void fillVertical(int dy, RecyclerView.Recycler recycler) {
        if (dy > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            if (null == lastView)
                return;

            if (lastView.getBottom() - dy < getHeight()) {
                int lastPos = getPosition(lastView);
                View scrap;

                if (lastPos == getItemCount() - 1) {
                    scrap = recycler.getViewForPosition(0);
                } else {
                    scrap = recycler.getViewForPosition(lastPos + 1);
                }

                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, 0, lastView.getBottom(), width, lastView.getBottom() + height);
            }
        } else {
            View firstView = getChildAt(0);

            if (null == firstView)
                return;

            if (firstView.getTop() - dy > 0) {
                int firstPos = getPosition(firstView);
                View scrap;

                if (firstPos == 0) {
                    scrap = recycler.getViewForPosition(getItemCount() - 1);
                } else {
                    scrap = recycler.getViewForPosition(firstPos - 1);
                }

                addView(scrap, 0);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, 0, firstView.getTop() - height, width, firstView.getTop());
            }
        }
    }

    /**
     * dx > 0 为向左滑动
     * dx < 0 为向右滑动
     */
    private void fillHorizontal(int dx, RecyclerView.Recycler recycler) {
        if (dx > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            if (null == lastView) {
                return;
            }

            if (lastView.getRight() - dx < getWidth()) {
                int lastPos = getPosition(lastView);
                View scrap;

                if (lastPos == getItemCount() - 1) {
                    scrap = recycler.getViewForPosition(0);
                } else {
                    scrap = recycler.getViewForPosition(lastPos + 1);
                }

                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, lastView.getRight(), 0, lastView.getRight() + width, height);
            }
        } else {
            View firstView = getChildAt(0);
            if (null == firstView)
                return;

            int firstPos = getPosition(firstView);

            if (firstView.getLeft() - dx > 0) {
                View scrap;

                if (firstPos == 0) {
                    scrap = recycler.getViewForPosition(getItemCount() - 1);
                } else {
                    scrap = recycler.getViewForPosition(firstPos - 1);
                }

                addView(scrap, 0);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, firstView.getLeft() - width, 0, firstView.getLeft(), height);
            }
        }
    }

    private void reycleHorizontalOutView(int dx, RecyclerView.Recycler recycler) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            if (view == null) {
                continue;
            }
            if (dx > 0) {
                if (view.getRight() < 0) {
                    removeAndRecycleView(view, recycler);
                }
            } else {
                if (view.getLeft() > getWidth()) {
                    removeAndRecycleView(view, recycler);
                }
            }
        }

    }

    private void reycleVeriticalOutView(int dy, RecyclerView.Recycler recycler) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            if (null == view)
                continue;

            if (dy > 0) {
                if (view.getBottom() < 0) {
                    removeAndRecycleView(view, recycler);
                }
            } else {
                if (view.getTop() > getHeight()) {
                    removeAndRecycleView(view, recycler);
                }
            }
        }
    }

    public int reviseOffset() {
        if (mOrientation == Orientaion.HORIZONTAL)
            return reviseHorizontalOffset();
        else
            return reviseVerticalOffset();
    }

    private int reviseHorizontalOffset() {
        int position = -1;
        if (null != mFirstView) {
            position = getPosition(mFirstView);

            if (mFirstView.getRight() < mFirstView.getWidth() / 2) {
                //left -> show next
                offsetChildrenHorizontal(-mFirstView.getRight());
                position = position == getItemCount() - 1 ? 0 : position + 1;
            } else {
                //right -> show current
                offsetChildrenHorizontal(mFirstView.getWidth() - mFirstView.getRight());
            }
        }

        return position;
    }

    private int reviseVerticalOffset() {
        int position = -1;
        if (null != mFirstView) {
            position = getPosition(mFirstView);

            if (mFirstView.getBottom() < mFirstView.getHeight() / 2) {
                //up -> show next
                offsetChildrenVertical(-mFirstView.getBottom());
                position = position == getItemCount() - 1 ? 0 : position + 1;
            } else {
                //down -> show current
                offsetChildrenVertical(mFirstView.getHeight() - mFirstView.getBottom());
            }
        }

        return position;
    }
}