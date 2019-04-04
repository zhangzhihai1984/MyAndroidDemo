package com.usher.demo.pager;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewAdapter extends PagerAdapter {
    private final List<String> mUrlList = new ArrayList<>();
    private final Context mContext;

    private int mSize = 5;

    public ViewAdapter(Context context) {
        mContext = context;

        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534943035723&di=52cd92a75bc7939284a1180d1ad45e16&imgtype=0&src=http%3A%2F%2Fattachments.gfan.com%2Fforum%2F201707%2F21%2F1751135o5ta40wolwottw5.png");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534943236734&di=aedb53d84eb4fb75695a1c415a4b59cd&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201502%2F17%2F20150217093254_acRPa.jpeg");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534942509719&di=3248b1e2c4ab810cc71453086b5e579f&imgtype=jpg&src=http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D3748600783%2C1996173154%26fm%3D214%26gp%3D0.jpg");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534938793944&di=aca46413eba1f53a91a7518153940f37&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201603%2F03%2F20160303024913_ivGtf.thumb.700_0.jpeg");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534943171294&di=a4902e10a79b7aaed3e75c4b898d335f&imgtype=0&src=http%3A%2F%2Fimg16.3lian.com%2Fgif2016%2Fq25%2F58%2F44.jpg");
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
//        return mUrlList.size();
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
