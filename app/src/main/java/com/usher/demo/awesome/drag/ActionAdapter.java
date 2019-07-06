package com.usher.demo.awesome.drag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usher.demo.R;

import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter {
    private final int ITEM_VIEW_TYPE_DEVICE = 0;
    private final int ITEM_VIEW_TYPE_DELAY = 1;

    private final Context mContext;
    private final List<ActionInfo> mData;

    public ActionAdapter(Context context, List<ActionInfo> data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == ITEM_VIEW_TYPE_DEVICE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_smart_scene_device, parent, false);
            return new DeviceViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_smart_scene_delay, parent, false);
            return new DelayViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_DEVICE)
            bindDevieViewHolder((DeviceViewHolder) holder, position);
        else
            bindDelayViewHolder((DelayViewHolder) holder, position);
    }

    private void bindDevieViewHolder(DeviceViewHolder holder, int position) {
        holder.nameTextView.setText(mData.get(position).name);
    }

    private void bindDelayViewHolder(DelayViewHolder holder, int position) {
        holder.delayTextView.setText("间隔时间" + mData.get(position).delay + "秒");
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).delay > 0 ? ITEM_VIEW_TYPE_DELAY : ITEM_VIEW_TYPE_DEVICE;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_textview);
        }
    }

    class DelayViewHolder extends RecyclerView.ViewHolder {
        final TextView delayTextView;

        DelayViewHolder(@NonNull View itemView) {
            super(itemView);

            delayTextView = itemView.findViewById(R.id.delay_textview);
        }
    }
}
