package com.usher.demo.awesome.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.usher.demo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShortcutAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<ShortcutInfo> mShortcutList = new ArrayList<>();

    public ShortcutAdapter(Context context) {
        mContext = context;

        initData();
    }

    private void initData() {
        mShortcutList.add(new ShortcutInfo("扫码开门", R.drawable.smart_shortcut_hammer, R.drawable.smart_shortcut_repair_background));
        mShortcutList.add(new ShortcutInfo("物业报修", R.drawable.smart_shortcut_hammer, R.drawable.smart_shortcut_repair_background));
        mShortcutList.add(new ShortcutInfo("访客预约", R.drawable.smart_shortcut_hammer, R.drawable.smart_shortcut_repair_background));
        mShortcutList.add(new ShortcutInfo("致电物业", R.drawable.smart_shortcut_hammer, R.drawable.smart_shortcut_repair_background));
        mShortcutList.add(new ShortcutInfo("更多", R.drawable.smart_shortcut_hammer, R.drawable.smart_shortcut_repair_background));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.smarthome_shortcut_item, parent, false);
        return new ShortcutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindShortcutViewHolder((ShortcutViewHolder) holder, mShortcutList.get(position));

    }

    private void bindShortcutViewHolder(ShortcutViewHolder holder, ShortcutInfo info) {
        holder.nameTextView.setText(info.name);
        Picasso.get().load(info.iconRes).into(holder.shortcutImageView);
        holder.shortcutImageView.setBackgroundResource(info.backgroundRes);
    }

    @Override
    public int getItemCount() {
        return mShortcutList.size();
    }

    class ShortcutViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shortcut_textview)
        TextView nameTextView;

        @BindView(R.id.shortcut_imageview)
        ImageView shortcutImageView;

        ShortcutViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ShortcutInfo {
        String name;
        int iconRes;
        int backgroundRes;

        ShortcutInfo(String name, int iconRes, int backgroundRes) {
            this.name = name;
            this.iconRes = iconRes;
            this.backgroundRes = backgroundRes;
        }
    }
}
