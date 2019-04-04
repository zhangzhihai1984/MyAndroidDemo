package com.usher.demo.selection;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter {
    private final List<SelectionInfo> mList;
    private final Context mContext;

    private OnSelectedListener mListener;

    SelectionAdapter(Context context, List<SelectionInfo> list) {
        mContext = context;
        mList = list;
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.selection_item_layout, parent, false);

        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindSelectionViewHolder((SelectionViewHolder) holder, position);
    }

    private void bindSelectionViewHolder(SelectionViewHolder holder, int position) {
        holder.itemView.setTag(mList.get(position));
        switch (mList.get(position).getStatus()) {
            case SELECTED:
//                holder.itemView.setBackgroundResource(R.drawable.selection_selected_background);
                break;
            case DISABLED:
//                holder.itemView.setBackgroundResource(R.drawable.selection_disabled_background);
                break;
            case DEFAULT:
//                holder.itemView.setBackgroundResource(R.drawable.selection_default_background);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SelectionViewHolder extends RecyclerView.ViewHolder {

        SelectionViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectionInfo info = (SelectionInfo) v.getTag();

                    switch (info.getStatus()) {
                        case SELECTED:
                            info.setStatus(SelectionInfo.Status.DEFAULT);
                            notifyDataSetChanged();
                            if (null != mListener) {
                                mListener.onSelected();
                            }
                            break;
                        case DEFAULT:
                            info.setStatus(SelectionInfo.Status.SELECTED);
                            notifyDataSetChanged();
                            if (null != mListener) {
                                mListener.onSelected();
                            }
                            break;
                    }
                }
            });
        }
    }
}
