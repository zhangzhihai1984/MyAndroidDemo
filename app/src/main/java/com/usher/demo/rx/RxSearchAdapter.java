package com.usher.demo.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usher.demo.R;

import java.util.List;

public class RxSearchAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<String> mCities;

    RxSearchAdapter(Context mContext, List<String> cities) {
        this.mContext = mContext;
        this.mCities = cities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_item_layout, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        bindSearchViewHolder((SearchViewHolder) viewHolder, mCities.get(position));
    }

    private void bindSearchViewHolder(SearchViewHolder holder, String city) {
        holder.city.setText(city);
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        final TextView city;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.name_textview);
        }
    }
}