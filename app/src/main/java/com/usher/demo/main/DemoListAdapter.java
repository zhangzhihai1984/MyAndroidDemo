package com.usher.demo.main;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usher.demo.R;

import java.util.List;

public class DemoListAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<DemoItem> mItems;

    DemoListAdapter(Context context, List<DemoItem> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_item_layout, parent, false);

        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        bindDemoViewHolder((DemoViewHolder) viewHolder, mItems.get(position));
    }

    private void bindDemoViewHolder(DemoViewHolder holder, DemoItem demoItem) {
        holder.itemView.setTag(demoItem);
        holder.desc.setText(demoItem.desc);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class DemoViewHolder extends RecyclerView.ViewHolder {
        final TextView desc;

        DemoViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.desc_textview);

            itemView.setOnClickListener(v -> {
                DemoItem demoItem = (DemoItem) itemView.getTag();

                Intent intent = new Intent(mContext, demoItem.aClass);
                intent.putExtra(DemoConfig.TAG_KEY, demoItem.key);
                mContext.startActivity(intent);
            });
        }
    }
}
