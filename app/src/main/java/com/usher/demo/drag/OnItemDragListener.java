package com.usher.demo.drag;

import android.support.v7.widget.RecyclerView;

public interface OnItemDragListener {
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int position);
    void onItemDragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to);
    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int position);
}
