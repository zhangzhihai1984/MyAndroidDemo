package com.usher.demo.awesome.drag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.twigcodes.ui.util.RxUtil;
import com.usher.demo.R;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActionAdapter extends RecyclerView.Adapter {
    private final int ITEM_VIEW_TYPE_DEVICE = 0;
    private final int ITEM_VIEW_TYPE_DELAY = 1;

    private final Context mContext;
    private final List<ActionInfo> mData;
    private final SceneTouchCallback mTouchCallback;

    private float mPreSwipeDx = 0;
    private int mPreSwipePosition = -1;
    private View mPreSwipeView;
    private ValueAnimator mSwipeRecoverAnimator;

    public ActionAdapter(Context context, List<ActionInfo> data, SceneTouchCallback touchCallback) {
        mContext = context;
        mData = data;
        mTouchCallback = touchCallback;

        init();
    }

    private void init() {
        int mSwipeThreshold = mContext.getResources().getDimensionPixelSize(R.dimen.drag_delete_width);

        mSwipeRecoverAnimator = ValueAnimator.ofFloat(-mSwipeThreshold, 0);
        mSwipeRecoverAnimator.setDuration(150);
        mSwipeRecoverAnimator.setInterpolator(new AccelerateInterpolator());
        mSwipeRecoverAnimator.addUpdateListener(animation -> mPreSwipeView.setTranslationX((Float) animation.getAnimatedValue()));
        mSwipeRecoverAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPreSwipePosition = -1;
            }
        });

        mTouchCallback.dragStarts()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose((LifecycleOwner) mContext))
                .subscribe(dragStart -> recoverSwipeView());

        mTouchCallback.dragMoving()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose((LifecycleOwner) mContext))
                .subscribe(dragMoving -> {
                    int fromPos = dragMoving.from;
                    int toPos = dragMoving.to;

                    if (fromPos < toPos) {
                        for (int i = fromPos; i < toPos; i++) {
                            Collections.swap(mData, i, i + 1);
                        }
                    } else {
                        for (int i = fromPos; i > toPos; i--) {
                            Collections.swap(mData, i, i - 1);
                        }
                    }

                    notifyItemMoved(dragMoving.from, dragMoving.to);
//                    notifyItemRangeChanged(fromPos, 1);
//                    notifyItemRangeChanged(toPos, 1);
//                    notifyItemChanged(fromPos);
//                    notifyItemChanged(toPos);
                });

        mTouchCallback.swipeMoving()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose((LifecycleOwner) mContext))
                .subscribe(swipeMoving -> {
                    View swipeLayout = ((BaseViewHolder) swipeMoving.viewHolder).swipeLayout;
                    float translationX = Math.max(-mSwipeThreshold, swipeMoving.dX);

                    if (swipeMoving.active) {
                        //滑动
                        //如果删除已经完全显示的情况下, 也就是说translationX已经达到了-mSwipeThreshold这个最小值, 则不再继续向左滑动
                        mPreSwipeDx = translationX;
                        swipeLayout.setTranslationX(translationX);
                    } else {
                        //松手
                        //如果删除已经完全显示的情况下, 也就是说translationX已经达到了-mSwipeThreshold这个最小值, 松手后不会向右
                        //滑动回去, 否则向右滑动回去
                        if (mPreSwipeDx > -mSwipeThreshold) {
                            mPreSwipeDx = translationX;
                            swipeLayout.setTranslationX(translationX);
                        } else {
                            mPreSwipePosition = swipeMoving.position;
                            mPreSwipeView = swipeLayout;
                        }
                    }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == ITEM_VIEW_TYPE_DEVICE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_smart_scene_device, parent, false);
            return new DeviceViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_smart_scene_delay, parent, false);
            return new DelayViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).deleteLayout.setTag(mData.get(position));

        if (getItemViewType(position) == ITEM_VIEW_TYPE_DEVICE)
            bindDevieViewHolder((DeviceViewHolder) holder, position);
        else
            bindDelayViewHolder((DelayViewHolder) holder, position);
    }

    private void bindDevieViewHolder(DeviceViewHolder holder, int position) {
        holder.nameTextView.setText(mData.get(position).name);
    }

    private void bindDelayViewHolder(DelayViewHolder holder, int position) {
        holder.delayTextView.setText("间隔时间" + mData.get(position).delay + "秒");
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).delay > 0 ? ITEM_VIEW_TYPE_DELAY : ITEM_VIEW_TYPE_DEVICE;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void recoverSwipeView() {
        if (mPreSwipePosition >= 0 && !mSwipeRecoverAnimator.isRunning())
            mSwipeRecoverAnimator.start();
    }

    class DeviceViewHolder extends BaseViewHolder {
        final TextView nameTextView;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_textview);
        }
    }

    class DelayViewHolder extends BaseViewHolder {
        final TextView delayTextView;

        DelayViewHolder(@NonNull View itemView) {
            super(itemView);

            delayTextView = itemView.findViewById(R.id.delay_textview);
        }
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        final View swipeLayout;
        final View deleteLayout;

        BaseViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);

            RxView.touches(swipeLayout)
                    .filter(motionEvent -> motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    .to(RxUtil.autoDispose((LifecycleOwner) mContext))
                    .subscribe(motionEvent -> {
                        recoverSwipeView();
                    });

            RxView.clicks(deleteLayout)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .to(RxUtil.autoDispose((LifecycleOwner) mContext))
                    .subscribe(v -> {
                        ActionInfo info = (ActionInfo) deleteLayout.getTag();
                        int position = mData.indexOf(info);
                        mData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mData.size() - position);
                    });
        }
    }
}