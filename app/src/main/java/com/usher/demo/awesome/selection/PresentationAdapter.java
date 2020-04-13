package com.usher.demo.awesome.selection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PresentationAdapter extends RecyclerView.Adapter {
    private final List<SelectionInfo> mList;
    private final Context mContext;

    PresentationAdapter(Context context, List<SelectionInfo> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_selection, parent, false);

        return new PresentationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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
