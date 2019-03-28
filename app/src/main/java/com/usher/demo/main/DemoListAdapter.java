package com.usher.demo.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usher.demo.R;

public class DemoListAdapter extends RecyclerView.Adapter {
    private final Context mContext;

    public DemoListAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_item_layout, parent, false);

        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        bindDemoViewHolder((DemoViewHolder) viewHolder);
    }

    private void bindDemoViewHolder(DemoViewHolder holder) {
        holder.desc.setText("Rx");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class DemoViewHolder extends RecyclerView.ViewHolder {
        final TextView desc;

        public DemoViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.desc_textview);
        }
    }
}
