package com.usher.demo.awesome.drag;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class DragActiity extends AppCompatActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private int mSwipeThreshold;

    private final List<ActionInfo> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        mSwipeThreshold = getResources().getDimensionPixelSize(R.dimen.drag_delete_width);
    }

    private void initView() {
        ActionAdapter mAdapter = new ActionAdapter(this, mData);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        SceneDragCallback callback = new SceneDragCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        callback.dragMoving()
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
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

                    mAdapter.notifyItemMoved(dragMoving.from, dragMoving.to);
                });

        RxView.scrollChangeEvents(mRecyclerView)
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> Log.i("zzh", " " + v.toString()));

        callback.swipeMoving()
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
                .subscribe(swipeMoving -> {
                    View item = swipeMoving.viewHolder.itemView.findViewById(R.id.name_textview);
                    mSwipeView = item;
                    float translationX = Math.max(-mSwipeThreshold, swipeMoving.dX);
                    if (swipeMoving.active) {
                        //滑动
                        //如果删除已经完全显示的情况下, 也就是说translationX已经达到了-mSwipeThreshold这个最小值, 则不再继续向左滑动
                        mPreSwipeDx = translationX;
                        item.setTranslationX(translationX);
                    } else {
                        //松手
                        //如果删除已经完全显示的情况下, 也就是说translationX已经达到了-mSwipeThreshold这个最小值, 松手后不会向右
                        //滑动回去, 否则向右滑动回去
                        if (mPreSwipeDx > -mSwipeThreshold) {
                            mPreSwipeDx = translationX;
                            item.setTranslationX(translationX);
                        } else {
                        }
                    }

                    mPreSwipePosition = swipeMoving.position;
//                    mPreActive = swipeMoving.active;
                });

        mData.add(new ActionInfo("智能插座"));
        mData.add(new ActionInfo("空调"));
        mData.add(new ActionInfo(10));
        mData.add(new ActionInfo("空气盒子"));

        mAdapter.notifyDataSetChanged();

        ValueAnimator recoverAnimator = ValueAnimator.ofFloat(-mSwipeThreshold, 0);
        recoverAnimator.setDuration(150);
        recoverAnimator.setInterpolator(new AccelerateInterpolator());
        recoverAnimator.addUpdateListener(animation -> mSwipeView.setTranslationX((Float) animation.getAnimatedValue()));

        Observable.timer(5000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> {
                    recoverAnimator.start();
                });
    }

    private float mPreSwipeDx = 0;
    private int mPreSwipePosition = -1;
    private View mSwipeView;
}
