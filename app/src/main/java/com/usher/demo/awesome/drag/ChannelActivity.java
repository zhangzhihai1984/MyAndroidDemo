package com.usher.demo.awesome.drag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.usher.demo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


public class ChannelActivity extends AppCompatActivity {
    private final List<String> mSelectedChannelList = new ArrayList<>();
    private final List<String> mRecommendedChannelList = new ArrayList<>();

    private Context mContext;

    private RelativeLayout mParentLayout;
    private ImageView mPlaceholderImageview;
    private ImageView mCacheImageView;

    private ChannelAdapter mAdapter;

    private AnimatorSet mMoveAnimatorSet;
    private AnimatorSet mCacheAnimatorSet;

    private int[] mMoveStartLocation = new int[2];
    private int[] mMoveEndLocation = new int[2];

    private int[] mCacheStartLocation = new int[2];
    private int[] mCacheEndLocation = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mContext = this;

        initData();
        initView();
    }

    private void initData() {
        mSelectedChannelList.addAll(Arrays.asList(getResources().getStringArray(R.array.selected_channels)));
        mRecommendedChannelList.addAll(Arrays.asList(getResources().getStringArray(R.array.recommended_channels)));

        mAdapter = new ChannelAdapter(mContext, mSelectedChannelList, mRecommendedChannelList);
    }

    private void initView() {
        mParentLayout = findViewById(R.id.root_layout);

        mPlaceholderImageview = findViewById(R.id.placeholder_imageview);
        mCacheImageView = findViewById(R.id.cache_imageview);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerview);

        final GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 4, RecyclerView.VERTICAL, false);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 4;
                } else if (position <= mSelectedChannelList.size()) {
                    return 1;
                } else if (position == mSelectedChannelList.size() + 1) {
                    return 4;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setNestedScrollingEnabled(false);

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemDragHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int position) {
                mPlaceholderImageview.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlaceholderImageview.getLayoutParams();
                params.width = viewHolder.itemView.getWidth();

                mMoveStartLocation = getLocation(viewHolder.itemView);
                int[] parentLocation = getLocation(mParentLayout);

                params.leftMargin = mMoveStartLocation[0]; //parent's margin left is 0
                params.topMargin = mMoveStartLocation[1] - parentLocation[1];
                mPlaceholderImageview.setLayoutParams(params);
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to) {
                mMoveEndLocation = getLocation(target.itemView);

                ValueAnimator xAnimator = ValueAnimator.ofFloat(0, mMoveEndLocation[0] - mMoveStartLocation[0]);
                xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mPlaceholderImageview.setTranslationX((Float) animation.getAnimatedValue());
                    }
                });

                ValueAnimator yAnimator = ValueAnimator.ofFloat(0, mMoveEndLocation[1] - mMoveStartLocation[1]);
                yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mPlaceholderImageview.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });

                mMoveAnimatorSet.playTogether(xAnimator, yAnimator);
                mMoveAnimatorSet.start();
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int position) {
                mPlaceholderImageview.setVisibility(View.INVISIBLE);
            }
        });

        mAdapter.setOnChannelRemoveListener(new ChannelAdapter.OnChannelRemoveListener() {
            @Override
            public void onChannelRemoved(View view, int[] location, boolean isAdd) {
                View targetView = mGridLayoutManager.findViewByPosition(isAdd ? mSelectedChannelList.size() : mSelectedChannelList.size() + 2);

                mCacheImageView.setVisibility(View.VISIBLE);
                mCacheImageView.setImageBitmap(getCacheBitmap(view));

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCacheImageView.getLayoutParams();
                params.width = view.getWidth();

                //Now the view's locaiton is [0, 0], so we should use the location which recorded in advance.
//                mCacheStartLocation = getLocation(view);
                mCacheStartLocation = location;
                mCacheEndLocation = getLocation(targetView);
                int[] parentLocation = getLocation(mParentLayout);

                params.leftMargin = mCacheStartLocation[0];
                params.topMargin = mCacheStartLocation[1] - parentLocation[1];

                mCacheImageView.setLayoutParams(params);

                ValueAnimator xAnimator = ValueAnimator.ofFloat(0, mCacheEndLocation[0] - mCacheStartLocation[0]);
                xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mCacheImageView.setTranslationX((Float) animation.getAnimatedValue());
                    }
                });

                ValueAnimator yAnimator = ValueAnimator.ofFloat(0, mCacheEndLocation[1] - mCacheStartLocation[1]);
                yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mCacheImageView.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });

                mCacheAnimatorSet.playTogether(xAnimator, yAnimator);
                mCacheAnimatorSet.start();
            }
        });

        mMoveAnimatorSet = new AnimatorSet();
        mMoveAnimatorSet.setInterpolator(new LinearInterpolator());
        mMoveAnimatorSet.setDuration(200);
        mMoveAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMoveStartLocation = mMoveEndLocation;

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlaceholderImageview.getLayoutParams();
                params.leftMargin += mPlaceholderImageview.getTranslationX();
                params.topMargin += mPlaceholderImageview.getTranslationY();

                mPlaceholderImageview.setLayoutParams(params);
                mPlaceholderImageview.setTranslationX(0);
                mPlaceholderImageview.setTranslationY(0);
            }
        });

        mCacheAnimatorSet = new AnimatorSet();
        mCacheAnimatorSet.setInterpolator(new LinearInterpolator());
        mCacheAnimatorSet.setDuration(300);
        mCacheAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCacheImageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private int[] getLocation(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        return location;
    }

    private Bitmap getCacheBitmap(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

        view.setDrawingCacheEnabled(false);

        return bitmap;
    }
}
