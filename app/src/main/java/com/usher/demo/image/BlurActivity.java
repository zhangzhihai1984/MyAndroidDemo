package com.usher.demo.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.twigcodes.ui.indicator.PageIndicatorView;
import com.usher.demo.R;

public class BlurActivity extends AppCompatActivity {
    private static final int[] RES_IDS = {R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4, R.mipmap.banner5};

    private ImageView mBlurImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blur);

        initView();
    }

    private void initView() {
        mBlurImageView = findViewById(R.id.imageview);
//        mBlurImageView.setScaleX(1.5f);
//        mBlurImageView.setScaleY(1.5f);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewAdapter());
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateBlurImage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        PageIndicatorView pageIndicatorView = findViewById(R.id.indicatorview);
        pageIndicatorView.setViewPager(viewPager);

        updateBlurImage(0);
    }

    private void updateBlurImage(int position) {
        Picasso.get().load(RES_IDS[position]).transform(new BlurTransformation(this)).into(mBlurImageView);
    }

    private class ViewAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Picasso.get().load(RES_IDS[position]).into(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return RES_IDS.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    private class BlurTransformation implements Transformation {
        private Context mContext;

        BlurTransformation(Context context) {
            mContext = context;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int width = Math.round(source.getWidth() / 8);
            int height = Math.round(source.getHeight() / 8);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(source, width, height, false);

            RenderScript renderScript = RenderScript.create(mContext);

            final Allocation input = Allocation.createFromBitmap(renderScript, inputBitmap);
            final Allocation output = Allocation.createTyped(renderScript, input.getType());

            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            scriptIntrinsicBlur.setRadius(8);
            scriptIntrinsicBlur.setInput(input);
            scriptIntrinsicBlur.forEach(output);
            output.copyTo(inputBitmap);

            renderScript.destroy();

            source.recycle();

            return inputBitmap;
        }

        @Override
        public String key() {
            return "BlurTransformation";
        }
    }
}
