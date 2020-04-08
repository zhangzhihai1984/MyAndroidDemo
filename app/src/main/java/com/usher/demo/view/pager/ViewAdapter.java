package com.usher.demo.view.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Random;

public class ViewAdapter extends PagerAdapter {
    private int mSize = 5;

    ViewAdapter(Context context) {
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        Log.i("zzh", "instantiateItem " + position);
        /*View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_pager, container, false);

        ((TextView) view.findViewById(R.id.textview)).setText(String.valueOf(position + 1));

        ImageView imageView = view.findViewById(R.id.imageview);
        Picasso.get().load(mUrlList.get(position)).into(imageView);
//        imageView.postDelayed(() -> Picasso.get().load(mUrlList.get(position)).into(imageView), 1000);

        container.addView(view);

        return view;*/

        TextView textView = new TextView(container.getContext());
        textView.setText(String.valueOf(position + 1));
        textView.setBackgroundColor(0xff000000 | new Random().nextInt(0x00ffffff));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(48);
        container.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return textView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        Log.i("zzh", "destroyItem " + position);
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void addItem() {
        mSize++;
        notifyDataSetChanged();
    }

    public void removeItem() {
        mSize--;
        mSize = mSize < 0 ? 0 : mSize;

        notifyDataSetChanged();
    }
}
