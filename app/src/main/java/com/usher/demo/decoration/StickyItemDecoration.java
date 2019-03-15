package com.usher.demo.decoration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.usher.demo.R;

import java.util.List;

public class StickyItemDecoration extends RecyclerView.ItemDecoration {
    private final float DIVIDER_HEIGHT = 5;
    private final float HEADER_HEIGHT = 80;
    private final float OFFSET_LEFT = 120;
    private final float SIDE_LENGTH = 80;
    private final float CIRCLE_RADIUS = 45;

    private final Context mContext;

    private final List<ItemInfo> mList;

    StickyItemDecoration(Context context, List<ItemInfo> list) {
        mContext = context;
        mList = list;
    }

    private boolean isFirstViewInGroup(int position) {
        return position == 0 || !mList.get(position).getGroupId().equals(mList.get(position - 1).getGroupId());
    }

    private boolean isLastViewInGroup(int position) {
        return position == mList.size() - 1 || !mList.get(position).getGroupId().equals(mList.get(position + 1).getGroupId());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isFirstViewInGroup(parent.getChildAdapterPosition(view))) {
            outRect.top = (int) HEADER_HEIGHT;
        } else {
            outRect.top = (int) DIVIDER_HEIGHT;
        }

        outRect.left = (int) OFFSET_LEFT;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);

            float centerX = parent.getPaddingLeft() + OFFSET_LEFT / 2;
            float centerY = view.getTop() + view.getHeight() / 2;

            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePaint.setColor(Color.parseColor("#26A69A"));
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeWidth(5);
            c.drawCircle(centerX, centerY, CIRCLE_RADIUS, circlePaint);

            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_round);

            RectF dst = new RectF();
            dst.top = centerY - SIDE_LENGTH / 2;
            dst.bottom = centerY + SIDE_LENGTH / 2;
            dst.left = centerX - SIDE_LENGTH / 2;
            dst.right = centerX + SIDE_LENGTH / 2;

            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            c.drawBitmap(bitmap, src, dst, new Paint(Paint.ANTI_ALIAS_FLAG));

            float upLineStartX = centerX;
            float upLineStartY = view.getTop();
            float upLineStopX = centerX;
            float upLineStopY = centerY - CIRCLE_RADIUS;

            Paint linePaint = new Paint(circlePaint);
            c.drawLine(upLineStartX, upLineStartY, upLineStopX, upLineStopY, linePaint);

            float downLineStartX = centerX;
            float downLineStartY = centerY + CIRCLE_RADIUS;
            float downLineStopX = centerX;
            float downLineStopY = view.getTop() + view.getHeight();

            c.drawLine(downLineStartX, downLineStartY, downLineStopX, downLineStopY, linePaint);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);

            //The first VISIBLE Item
            if (i == 0) {
                //Fix in RecyclerView's top
                float headerTop = parent.getPaddingTop();
                float headerBottom = headerTop + HEADER_HEIGHT;

                //Align bottom of current group's last view if the header's bottom is greater than last view's bottom，
                //or there will be position overlap between the current and next header.
                if (isLastViewInGroup(parent.getChildAdapterPosition(view))) {
                    float itemBottom = view.getBottom();

                    if (headerBottom > itemBottom) {
                        headerTop = itemBottom - HEADER_HEIGHT;
                        headerBottom = itemBottom;
                    }
                }

                float headerLeft = parent.getPaddingLeft();
                float headerRight = parent.getWidth() - parent.getPaddingRight();

                RectF headerRect = new RectF(headerLeft, headerTop, headerRight, headerBottom);

                drawHeader(c, headerRect, parent.getChildAdapterPosition(view));

            } else {
                //Above the first view in group
                if (isFirstViewInGroup(parent.getChildAdapterPosition(view))) {
                    float headerTop = view.getTop() - HEADER_HEIGHT;
                    float headerBottom = view.getTop();

                    float headerLeft = parent.getPaddingLeft();
                    float headerRight = parent.getWidth() - parent.getPaddingRight();

                    RectF headerRect = new RectF(headerLeft, headerTop, headerRight, headerBottom);

                    drawHeader(c, headerRect, parent.getChildAdapterPosition(view));
                }
            }
        }

            /*String text = "Hello";

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(100);

            Paint.FontMetricsInt metricsInt = textPaint.getFontMetricsInt();

//            int width = (int) textPaint.measureText(text);

            Rect rect = new Rect(200, 200, 800, 800);
            Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRectPaint.setColor(Color.MAGENTA);

//            Rect boundRect = new Rect();
//            textPaint.getTextBounds(text, 0, text.length(), boundRect);

            int baselineY = rect.centerY() - metricsInt.top / 2 - metricsInt.bottom / 2;

            int top = baselineY + metricsInt.top;
            int bottom = baselineY + metricsInt.bottom;
            int ascent = baselineY + metricsInt.ascent;
            int descent = baselineY + metricsInt.descent;

            c.drawRect(rect, mRectPaint);
            c.drawText(text, rect.centerX(), rect.centerY() - metricsInt.top / 2 - metricsInt.bottom / 2, textPaint);

            Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setColor(Color.RED);
            c.drawLine(0, baselineY, parent.getWidth(), baselineY, mLinePaint);

            mLinePaint.setColor(Color.BLUE);
            c.drawLine(0, top, parent.getWidth(), top, mLinePaint);

            mLinePaint.setColor(Color.GREEN);
            c.drawLine(0, ascent, parent.getWidth(), ascent, mLinePaint);

            mLinePaint.setColor(Color.CYAN);
            c.drawLine(0, descent, parent.getWidth(), descent, mLinePaint);

            mLinePaint.setColor(Color.LTGRAY);
            c.drawLine(0, bottom, parent.getWidth(), bottom, mLinePaint);*/
    }

    private void drawHeader(Canvas c, RectF rect, int index) {
        Paint headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerPaint.setColor(Color.parseColor("#26A69A"));
        headerPaint.setStyle(Paint.Style.FILL);
        c.drawRect(rect, headerPaint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        Paint.FontMetricsInt metricsInt = textPaint.getFontMetricsInt();
        c.drawText(mList.get(index).getGroupTitle(), rect.centerX(), rect.centerY() - metricsInt.top / 2 - metricsInt.bottom / 2, textPaint);
    }
}
