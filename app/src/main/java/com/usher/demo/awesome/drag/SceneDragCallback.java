package com.usher.demo.awesome.drag;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.usher.demo.R;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SceneDragCallback extends ItemTouchHelper.Callback {
    private PublishSubject<DragStart> mDragStartSubject = PublishSubject.create();
    private PublishSubject<DragMoving> mDragMovingSubject = PublishSubject.create();
    private PublishSubject<DragEnd> mDragEndSubject = PublishSubject.create();
    private PublishSubject<SwipeMoving> mSwipeMovingSubject = PublishSubject.create();

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
//        return super.getSwipeThreshold(viewHolder);
        return 10.0f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
//        Log.i("zzh", "velocity: " + defaultValue);
        return defaultValue * 10;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mDragMovingSubject.onNext(new DragMoving(viewHolder, viewHolder.getAdapterPosition(), target, target.getAdapterPosition()));
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("zzh", "onSwiped");
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        Log.i("zzh", "onSelectedChanged " + actionState + " " + (null != viewHolder));

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && null != viewHolder)
            mDragStartSubject.onNext(new DragStart(viewHolder, viewHolder.getAdapterPosition()));
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        Log.i("zzh", "clearView");
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        Log.i("zzh", "dx: " + dX + " " + isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            viewHolder.itemView.setAlpha(.5f);
        } else {
            mSwipeMovingSubject.onNext(new SwipeMoving(viewHolder, viewHolder.getAdapterPosition(), dX, isCurrentlyActive));
//            viewHolder.itemView.setAlpha(1);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public Observable<DragStart> dragStarts() {
        return mDragStartSubject;
    }

    public Observable<DragMoving> dragMoving() {
        return mDragMovingSubject;
    }

    public Observable<DragEnd> dragEnds() {
        return mDragEndSubject;
    }

    public Observable<SwipeMoving> swipeMoving() {
        return mSwipeMovingSubject;
    }

    public class DragStart {
        public RecyclerView.ViewHolder viewHolder;
        public int position;

        public DragStart(RecyclerView.ViewHolder viewHolder, int position) {
            this.viewHolder = viewHolder;
            this.position = position;
        }
    }

    public class DragEnd extends DragStart {
        public DragEnd(RecyclerView.ViewHolder viewHolder, int position) {
            super(viewHolder, position);
        }
    }

    public class DragMoving {
        public RecyclerView.ViewHolder current;
        public int from;
        public RecyclerView.ViewHolder target;
        public int to;

        public DragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to) {
            this.current = current;
            this.from = from;
            this.target = target;
            this.to = to;
        }
    }

    public class SwipeMoving extends DragStart {
        public float dX;
        public boolean active;

        public SwipeMoving(RecyclerView.ViewHolder viewHolder, int position, float dX, boolean active) {
            super(viewHolder, position);
            this.dX = dX;
            this.active = active;
        }
    }
}
