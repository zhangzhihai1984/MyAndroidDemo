package com.usher.demo.awesome.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.squareup.picasso.Picasso;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        mShortcutList.add(new ShortcutInfo("扫码开门", R.drawable.smart_shortcut_scan, R.drawable.smart_shortcut_scan_background));
        mShortcutList.add(new ShortcutInfo("物业报修", R.drawable.smart_shortcut_repair, R.drawable.smart_shortcut_repair_background));
        mShortcutList.add(new ShortcutInfo("访客预约", R.drawable.smart_shortcut_visit, R.drawable.smart_shortcut_visit_background));
        mShortcutList.add(new ShortcutInfo("致电物业", R.drawable.smart_shortcut_phone, R.drawable.smart_shortcut_phone_background));
        mShortcutList.add(new ShortcutInfo("更多", R.drawable.smart_shortcut_more, R.drawable.smart_shortcut_more_background));
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
        holder.itemView.setTag(info);
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

            RxView.clicks(itemView)
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .as(RxUtil.autoDispose((LifecycleOwner) mContext))
                    .subscribe(v -> Toast.makeText(mContext, ((ShortcutInfo) itemView.getTag()).name, Toast.LENGTH_SHORT).show());
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
