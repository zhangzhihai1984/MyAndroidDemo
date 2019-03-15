package com.usher.demo.selection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import java.util.List;

public class PresentationAdapter extends RecyclerView.Adapter {
    private final List<SelectionInfo> mList;
    private final Context mContext;

    PresentationAdapter(Context context, List<SelectionInfo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.selection_item_layout, parent, false);

        return new PresentationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindPresentationViewHolder((PresentationViewHolder) holder, position);
    }

    private void bindPresentationViewHolder(PresentationViewHolder holder, int position) {
        switch (mList.get(position).getStatus()) {
            case SELECTED:
                holder.itemView.setBackgroundResource(R.drawable.selection_selected_background);
                break;
            case DISABLED:
                holder.itemView.setBackgroundResource(R.drawable.selection_disabled_background);
                break;
            case DEFAULT:
                holder.itemView.setBackgroundResource(R.drawable.selection_default_background);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PresentationViewHolder extends RecyclerView.ViewHolder {

        PresentationViewHolder(View itemView) {
            super(itemView);
        }
    }
}
