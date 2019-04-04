package com.usher.demo;

import android.graphics.Canvas;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.usher.demo.drag.ChannelAdapter;
import com.usher.demo.drag.ItemDragHelperCallback;
import com.usher.demo.drag.OnItemDragListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private final List<String> mData = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ChannelAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            mData.add(String.valueOf(i));
        }

        mAdapter = new ChannelAdapter(this, mData, mData);

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            Log.i("zzh", "result1: " + binaryToHexString(digest.digest("123456".getBytes())));
            digest.reset();
            Log.i("zzh", "result2: " + new BigInteger(1, digest.digest("123456".getBytes())));
            digest.reset();
            Log.i("zzh", "result3: " + new BigInteger(digest.digest("123456".getBytes())));
            digest.reset();
            Log.i("zzh", "result4: " + digest.digest("123456".getBytes())[0]);
            digest.reset();

            byte[] bytes1 = digest.digest("123456".getBytes());
            byte[] bytes2 = new byte[bytes1.length + 1];
            bytes2[0] = 0;
            System.arraycopy(bytes1, 0, bytes2, 1, bytes1.length);

            Log.i("zzh", "result5: " + new BigInteger(bytes2).toString(16));

            byte[] bytes = new byte[2];
            bytes[0] = 0;
            bytes[1] = -2;
//            bytes[1] = 0;
//            Log.i("zzh", "1: " + new BigInteger(bytes));
//            Log.i("zzh", "2: " + new BigInteger(1, bytes));
//            Log.i("zzh", "3: " + new BigInteger(-1, bytes));
            for (int i = 0; i < bytes.length; i++) {

//                bytes[i] = (byte) (bytes[i] - 1);
//                bytes[i] = (byte) ~bytes[i];
//                Log.i("zzh", " " + bytes[i]);
            }

//            Log.i("zzh", "1: " + new BigInteger(bytes));
//            Log.i("zzh", "2: " + new BigInteger(1, bytes));
//            Log.i("zzh", "3: " + new BigInteger(-1, bytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String binaryToHexString(byte[] bytes) {
        String hexStr = "0123456789abcdef";
        String result = "";
        String hex;

        for (byte aByte : bytes) {
            hex = String.valueOf(hexStr.charAt((aByte & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(aByte & 0x0F));
            result += hex;
        }

        return result;
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

//        ItemDragCallback mItemDragCallback = new ItemDragCallback(mAdapter);
//        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemDragCallback);

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemDragHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int position) {
                viewHolder.itemView.setBackgroundResource(R.drawable.list_item_background);
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int position) {
                viewHolder.itemView.setBackground(null);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class ItemDragCallback extends ItemTouchHelper.Callback {

        public ItemDragCallback(ChannelAdapter adapter) {
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

            return makeMovementFlags(dragFlags, swipeFlags);
            //00000011 00001100 00001111
//            return 0x030C0F;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.i("zzh", "onMove from " + viewHolder.getAdapterPosition() + " to " + target.getAdapterPosition());
            Log.i("zzh", "onMove from ViewHolder top: " + viewHolder.itemView.getTop());
            Log.i("zzh", "onMove to ViewHolder top: " + target.itemView.getTop());
//            return viewHolder.getAdapterPosition() > 4;

            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();

            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(mData, i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Collections.swap(mData, i, i - 1);
                }
            }
//            Collections.swap(mData, from, to);
            Log.i("zzh", "mData: " + mData.toString());
            Log.i("zzh", "target: " + mData.get(from));
            mAdapter.notifyItemMoved(from, to);

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Log.i("zzh", "onSwiped " + position + " direction: " + direction);
            mData.remove(position);
            mAdapter.notifyItemRemoved(position);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            Log.i("zzh", "onSelectedChanged " + actionState);
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            Log.i("zzh", "clearView " + position);
            super.clearView(recyclerView, viewHolder);
        }

        @Override
        public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);

//            int from = viewHolder.getAdapterPosition();
//            int to = target.getAdapterPosition();

//            Log.i("zzh", "onMoved from " + from + " to " + to + " x: " + x + " y: " + y);

            /*if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(mData, i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Collections.swap(mData, i, i - 1);
                }
            }

            mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());*/
        }

        @Override
        public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
            return super.getMoveThreshold(viewHolder);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            Log.i("zzh", "onChildDraw dx: " + dX + " dy: " + dY + " actionState: " + actionState + " active: " + isCurrentlyActive);

            /*if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.itemView.setAlpha(.5f);
            } else {
                viewHolder.itemView.setAlpha(1);
            }*/
        }

        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            Log.i("zzh", "canDropOver： " + target.getAdapterPosition());
//            return target.getAdapterPosition() >= 4;
            return true;
        }
    }
}
