package com.usher.demo.loading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.usher.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadingView extends View {
    private static final float X_SPACE = 20;
    private static final double PI2 = Math.PI * 2;

    private final Paint mPaint = new Paint();

    private Bitmap mBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private Rect mSrcRect = new Rect();

//    private WaveObj mObj = new WaveObj();

    private final List<WaveObj> mObjs = new ArrayList<>();

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private boolean mInitFlag = false;

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        initBitmap();
        initObjs();
    }

    private void initBitmap() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.leaf);
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        mSrcRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);

        Log.i("zzh", "Bitmap Width: " + mBitmapWidth);
        Log.i("zzh", "Bitmap Height: " + mBitmapHeight);
    }

    private void initObjs() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (hasWindowFocus) {
            if (!mInitFlag) {
                mInitFlag = true;
                Log.i("zzh", "View Width: " + getWidth());
                Log.i("zzh", "View Height: " + getHeight());
                startWave();
            }
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            mObjs.clear();
            for (int i = 0; i < 10; i++) {
                WaveObj obj = new WaveObj();
                obj.height = getHeight() / 2 - mBitmapHeight / 2;
//                obj.phi = Math.random() * PI2;
                obj.phi = 0;
                obj.omega = PI2 / getWidth();
                obj.x = getWidth() + i * X_SPACE * 5;
                obj.angle = new Random().nextInt(360);
                mObjs.add(obj);

                Log.i("zzh", "phi: " + obj.phi);
            }

            if (null == mRefreshProgressRunnable) {
                mRefreshProgressRunnable = new RefreshProgressRunnable();
            } else {
                removeCallbacks(mRefreshProgressRunnable);
            }

            postDelayed(mRefreshProgressRunnable, 100);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (WaveObj obj : mObjs) {
            if (obj.x < 0) {
                continue;
            }

            if (obj.x > getWidth()) {
                obj.x -= X_SPACE;
                continue;
            }

            float y = (float) (obj.height * Math.sin(obj.omega * obj.x + obj.phi) + getHeight() / 2);

            obj.dest.top = y - mBitmap.getHeight() / 2;
            obj.dest.bottom = y + mBitmap.getHeight() / 2;
            obj.dest.left = obj.x - mBitmapWidth / 2;
            obj.dest.right = obj.x + mBitmap.getWidth() / 2;

            canvas.drawBitmap(mBitmap, mSrcRect, obj.dest, mPaint);


            obj.x -= X_SPACE;
        }
    }

    private class RefreshProgressRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                invalidate();

                postDelayed(this, 100);
            }
        }
    }

    // y=Asin(ωx+φ)+k
    private class WaveObj {
        float x;
        float height;
        double phi; //φ: 0 - 2π
        double omega; // 2π/length
        int angle; // 0 - 360
        RectF dest = new RectF();
    }
}
