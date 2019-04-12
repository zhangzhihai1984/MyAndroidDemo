package com.usher.demo.awesome.decoration;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usher.demo.R;

import java.util.List;

public class DecorationAdapter extends RecyclerView.Adapter {
    private final List<ItemInfo> mList;

    private final Context mContext;

    public DecorationAdapter(Context context, List<ItemInfo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.decoration_item_layout, parent, false);

        /*RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.topMargin = 5;
        view.setLayoutParams(params);*/

        return new DecorationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DecorationViewHolder) holder).mNameTextView.setText(mList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class DecorationViewHolder extends RecyclerView.ViewHolder {
        final TextView mNameTextView;

        DecorationViewHolder(View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(R.id.name_textview);
        }
    }
}
