package com.usher.demo.awesome.channel;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ItemDragHelperCallback extends ItemTouchHelper.Callback {
    private final OnItemDragListener mOnItemDragListener;

    public ItemDragHelperCallback(OnItemDragListener listener) {
        mOnItemDragListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        int swipeFlags = 0;

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }

        //Two headers & all recommended channels
        if (viewHolder instanceof FixedViewHolder) {
            return makeMovementFlags(0, 0);
        }

        //First selected channel
        if (viewHolder.getLayoutPosition() == 1) {
            return makeMovementFlags(0, 0);
        }

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //We have avoided this case in canDropOver
        /*if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }*/

        mOnItemDragListener.onItemDragMoving(viewHolder, viewHolder.getAdapterPosition(), target, target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        //Two headers & all recommended channels
        if (current.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        if (target instanceof FixedViewHolder) {
            return false;
        }

        //First selected channel
        if (target.getAdapterPosition() == 1) {
            return false;
        }

        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            mOnItemDragListener.onItemDragStart(viewHolder, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        mOnItemDragListener.onItemDragEnd(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        //It DOES NOT work when the threshold is lower than 1.0f
        return 0;
    }

}
