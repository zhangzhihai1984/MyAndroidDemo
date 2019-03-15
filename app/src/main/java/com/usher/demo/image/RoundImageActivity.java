package com.usher.demo.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.usher.demo.R;

public class RoundImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_image);

        initView();
    }

    private void initView() {
        Picasso.get().load(R.mipmap.homework).transform(new SquareTransformation()).into((ImageView) findViewById(R.id.square_imageview));
        Picasso.get().load(R.mipmap.homework).transform(new CircleTransformation()).into((ImageView) findViewById(R.id.circle_imageview));
        Picasso.get().load(R.mipmap.homework).transform(new RoundTransformation()).into((ImageView) findViewById(R.id.round_imageview));
    }

    private class SquareTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "SquareTransformation";
        }
    }

    private class CircleTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            //返回一个正方形的Bitmap对象
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

            //提供指定宽高的canvas
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //背景：在画布上绘制一个圆
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);

            //设置图片相交情况下的处理方式
            //setXfermode：设置当绘制的图像出现相交情况时候的处理方式的,它包含的常用模式有哪几种
            //PorterDuff.Mode.SRC_IN 取两层图像交集部门,只显示上层图像,注意这里是指取相交叉的部分,然后显示上层图像
            //PorterDuff.Mode.DST_IN 取两层图像交集部门,只显示下层图像
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            //前景：在画布上绘制一个bitmap
            canvas.drawBitmap(source, 0, 0, paint);

            source.recycle();

            return bitmap;
        }

        @Override
        public String key() {
            return "CircleTransformation";
        }
    }

    private class RoundTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int width = source.getWidth();
            int height = source.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
//            paint.setColor(Color.BLUE);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(5);

            canvas.drawRoundRect(0, 0, width, height, 50, 50, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, 0, 0, paint);

            source.recycle();

            return bitmap;
        }

        @Override
        public String key() {
            return "RoundTransformation";
        }
    }
}
