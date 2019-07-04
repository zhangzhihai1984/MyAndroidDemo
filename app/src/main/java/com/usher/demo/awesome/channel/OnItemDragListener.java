package com.usher.demo.awesome.channel;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemDragListener {
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int position);
    void onItemDragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to);
    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int position);
}
