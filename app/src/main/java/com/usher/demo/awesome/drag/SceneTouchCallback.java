package com.usher.demo.awesome.drag;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SceneTouchCallback extends ItemTouchHelper.Callback {
    private PublishSubject<DragStart> mDragStartSubject = PublishSubject.create();
    private PublishSubject<DragMoving> mDragMovingSubject = PublishSubject.create();
    private PublishSubject<DragEnd> mDragEndSubject = PublishSubject.create();
    private PublishSubject<SwipeStart> mSwipeStartSubject = PublishSubject.create();
    private PublishSubject<SwipeMoving> mSwipeMovingSubject = PublishSubject.create();
    private PublishSubject<SwipeEnd> mSwipeEndSubject = PublishSubject.create();

    private boolean mSwipeEnabled = true;
    private boolean mDragEnabled = true;

    private int mPreActionState = ItemTouchHelper.ACTION_STATE_IDLE; // drag or swipe

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 10.0f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
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

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && null != viewHolder) {
            mPreActionState = ItemTouchHelper.ACTION_STATE_DRAG;
            mDragStartSubject.onNext(new DragStart(viewHolder, viewHolder.getAdapterPosition()));
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && null != viewHolder) {
            mPreActionState = ItemTouchHelper.ACTION_STATE_SWIPE;
            mSwipeStartSubject.onNext(new SwipeStart(viewHolder, viewHolder.getAdapterPosition()));
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        Log.i("zzh", "clearView");

        if (mPreActionState == ItemTouchHelper.ACTION_STATE_DRAG)
            mDragEndSubject.onNext(new DragEnd(viewHolder, viewHolder.getAdapterPosition()));

        if (mPreActionState == ItemTouchHelper.ACTION_STATE_SWIPE)
            mSwipeEndSubject.onNext(new SwipeEnd(viewHolder, viewHolder.getAdapterPosition()));
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.i("zzh", "dx: " + dX + " " + isCurrentlyActive);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            mSwipeMovingSubject.onNext(new SwipeMoving(viewHolder, viewHolder.getAdapterPosition(), dX, isCurrentlyActive));
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mSwipeEnabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mDragEnabled;
    }

    public void setItemViewSwipeEnabled(boolean enabled) {
        mSwipeEnabled = enabled;
    }

    public void setItemViewDragEnabled(boolean enabled) {
        mDragEnabled = enabled;
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

    public Observable<SwipeStart> swipeStarts() {
        return mSwipeStartSubject;
    }

    public Observable<SwipeMoving> swipeMoving() {
        return mSwipeMovingSubject;
    }

    public Observable<SwipeEnd> swipeEnds() {
        return mSwipeEndSubject;
    }

    public class DragStart {
        public RecyclerView.ViewHolder viewHolder;
        public int position;

        DragStart(RecyclerView.ViewHolder viewHolder, int position) {
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

        DragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to) {
            this.current = current;
            this.from = from;
            this.target = target;
            this.to = to;
        }
    }

    public class SwipeStart extends DragStart {
        SwipeStart(RecyclerView.ViewHolder viewHolder, int position) {
            super(viewHolder, position);
        }
    }

    public class SwipeEnd extends SwipeStart {
        public SwipeEnd(RecyclerView.ViewHolder viewHolder, int position) {
            super(viewHolder, position);
        }
    }

    public class SwipeMoving extends SwipeStart {
        public float dX;
        public boolean active;

        SwipeMoving(RecyclerView.ViewHolder viewHolder, int position, float dX, boolean active) {
            super(viewHolder, position);
            this.dX = dX;
            this.active = active;
        }
    }
}