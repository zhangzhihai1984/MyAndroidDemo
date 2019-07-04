package com.usher.demo.awesome.channel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.usher.demo.R;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemDragListener {
    private final int ITEM_VIEW_TYPE_SELECTED_TITLE = 0;
    private final int ITEM_VIEW_TYPE_SELECTED_CHANNEL = 1;
    private final int ITEM_VIEW_TYPE_RECOMMENDED_TITLE = 2;
    private final int ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL = 3;

    private final List<String> mSelectedList;
    private final List<String> mRecommendedList;
    private final Context mContext;

    private OnItemDragListener mOnItemDragListener;
    private OnChannelRemoveListener mOnChannelRemoveListener;

    /**
     * When you click one selected channel it will be removed fromm the selected list and added into recommeded list, and vice versa.
     * In this case, you may need an animation to make this process looks awesome.
     */
    public interface OnChannelRemoveListener {
        /**
         * @param view     The ViewHolder's itemView you have clicked which will be removed from the channel list.
         * @param location We will call {@link RecyclerView.Adapter#notifyItemRemoved(int)} to remove the ViewHolder you have clicked,
         *                 and the view's location will be changed to [0, 0], so you should record the location in advance
         *                 to make sure the start position of translation animation is correct.
         * @param isAdd    If this value is true, that means you click the recommended channel which will be moved to the selected channel list, and vice versa.
         */
        void onChannelRemoved(View view, int[] location, boolean isAdd);
    }

    public ChannelAdapter(Context context, List<String> list1, List<String> list2) {
        mContext = context;
        mSelectedList = list1;
        mRecommendedList = list2;
    }

    public void setOnItemDragListener(OnItemDragListener listener) {
        this.mOnItemDragListener = listener;
    }

    public void setOnChannelRemoveListener(OnChannelRemoveListener listener) {
        this.mOnChannelRemoveListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case ITEM_VIEW_TYPE_SELECTED_TITLE:
            case ITEM_VIEW_TYPE_RECOMMENDED_TITLE:
                view = LayoutInflater.from(mContext).inflate(R.layout.title_item_layout, parent, false);
                return new TitleViewHolder(view);
            case ITEM_VIEW_TYPE_SELECTED_CHANNEL:
                view = LayoutInflater.from(mContext).inflate(R.layout.channel_item_layout, parent, false);
                return new SelectedChannelViewHolder(view);
            case ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL:
                view = LayoutInflater.from(mContext).inflate(R.layout.channel_item_layout, parent, false);
                return new RecommendedChannelViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_SELECTED_TITLE:
                bindSelectedTitleViewHolder((TitleViewHolder) holder);
                break;
            case ITEM_VIEW_TYPE_SELECTED_CHANNEL:
                bindSelectedChannelViewHolder((SelectedChannelViewHolder) holder, position - 1);
                break;
            case ITEM_VIEW_TYPE_RECOMMENDED_TITLE:
                bindRecommendedTitleViewHolder((TitleViewHolder) holder);
                break;
            case ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL:
                bindRecommendedChannelViewHolder((RecommendedChannelViewHolder) holder, position - mSelectedList.size() - 2);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSelectedList.size() + mRecommendedList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_VIEW_TYPE_SELECTED_TITLE;
        } else if (position <= mSelectedList.size()) {
            return ITEM_VIEW_TYPE_SELECTED_CHANNEL;
        } else if (position == mSelectedList.size() + 1) {
            return ITEM_VIEW_TYPE_RECOMMENDED_TITLE;
        } else {
            return ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL;
        }
    }

    @Override
    public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int position) {
        ((SelectedChannelViewHolder) viewHolder).mNameTextView.setBackground(mContext.getDrawable(R.drawable.channel_item_selected_background));
        ((SelectedChannelViewHolder) viewHolder).mNameTextView.setElevation(10);
        ((SelectedChannelViewHolder) viewHolder).mDeleteImageView.setVisibility(View.INVISIBLE);

        if (null != mOnItemDragListener) {
            //Selected Header
            mOnItemDragListener.onItemDragStart(viewHolder, position - 1);
        }
    }

    @Override
    public void onItemDragMoving(RecyclerView.ViewHolder current, int from, RecyclerView.ViewHolder target, int to) {
        //Selected Header
        int fromPos = from - 1;
        int toPos = to - 1;

        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(mSelectedList, i, i + 1);
            }
        } else {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(mSelectedList, i, i - 1);
            }
        }

        notifyItemMoved(from, to);

        if (null != mOnItemDragListener) {
            mOnItemDragListener.onItemDragMoving(current, fromPos, target, toPos);
        }
    }

    @Override
    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int position) {
        ((SelectedChannelViewHolder) viewHolder).mNameTextView.setBackground(mContext.getDrawable(R.drawable.channel_item_background));
        ((SelectedChannelViewHolder) viewHolder).mNameTextView.setElevation(0);
        ((SelectedChannelViewHolder) viewHolder).mDeleteImageView.setVisibility(View.VISIBLE);

        if (null != mOnItemDragListener) {
            //Selected Header
            mOnItemDragListener.onItemDragEnd(viewHolder, position - 1);
        }
    }

    private void bindSelectedTitleViewHolder(TitleViewHolder holder) {
        holder.mTitleTextView.setText("已选频道");
        holder.mDescTextView.setText("按住拖动调整排序");
    }

    private void bindRecommendedTitleViewHolder(TitleViewHolder holder) {
        holder.mTitleTextView.setText("推荐频道");
        holder.mDescTextView.setText("点击添加频道");
    }

    private void bindSelectedChannelViewHolder(SelectedChannelViewHolder holder, int position) {
        holder.itemView.setTag(mSelectedList.get(position));
        holder.mNameTextView.setText(mSelectedList.get(position));

        if (position == 0) {
            holder.mNameTextView.setTextColor(Color.parseColor("#888888"));
            holder.mDeleteImageView.setVisibility(View.GONE);
        } else {
            holder.mNameTextView.setTextColor(Color.parseColor("#000000"));
            holder.mDeleteImageView.setVisibility(View.VISIBLE);
        }
    }

    private void bindRecommendedChannelViewHolder(RecommendedChannelViewHolder holder, int position) {
        holder.itemView.setTag(mRecommendedList.get(position));
        holder.mNameTextView.setText(mRecommendedList.get(position));
        holder.mNameTextView.setTextColor(Color.parseColor("#000000"));
        holder.mDeleteImageView.setVisibility(View.GONE);
    }

    class TitleViewHolder extends RecyclerView.ViewHolder implements FixedViewHolder {
        final TextView mTitleTextView;
        final TextView mDescTextView;

        TitleViewHolder(View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.title_textview);
            mDescTextView = itemView.findViewById(R.id.desc_textview);
        }
    }

    class SelectedChannelViewHolder extends RecyclerView.ViewHolder {
        final TextView mNameTextView;
        final ImageView mDeleteImageView;

        SelectedChannelViewHolder(final View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(R.id.name_textview);
            mDeleteImageView = itemView.findViewById(R.id.delete_imageview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int index = mSelectedList.indexOf(v.getTag());

                    //Not first selected channel (first selected channel is a fixed item)
                    if (index > 0) {
                        //Swap position of current selected channel and recommended header (Notice: NOT the first recommended channel)

                        //Selected header
                        int from = index + 1;

                        //Selected header & all selected channels
                        int to = mSelectedList.size() + 1;

                        mRecommendedList.add(0, mSelectedList.remove(index));

                        notifyItemMoved(from, to);

                        if (null != mOnChannelRemoveListener) {
                            final int[] location = new int[2];
                            v.getLocationOnScreen(location);

                            /* We will find the target view through GridLayoutManager.findViewByPosition and get the location,
                            so we have to wait until the swap process is DONE to make sure the position is correct */
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    v.findViewById(R.id.delete_imageview).setVisibility(View.INVISIBLE);

                                    mOnChannelRemoveListener.onChannelRemoved(v, location, false);
                                }
                            }, 50);
                        }
                    }
                }
            });
        }
    }

    class RecommendedChannelViewHolder extends SelectedChannelViewHolder implements FixedViewHolder {
        RecommendedChannelViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //Swap position of current recommended channel and recommended header

                    int index = mRecommendedList.indexOf(v.getTag());
                    int from = mSelectedList.size() + index + 2;
                    int to = mSelectedList.size() + 1;

                    mSelectedList.add(mRecommendedList.remove(index));

                    notifyItemMoved(from, to);

                    if (null != mOnChannelRemoveListener) {
                        final int[] location = new int[2];
                        v.getLocationOnScreen(location);

                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOnChannelRemoveListener.onChannelRemoved(v, location, true);
                            }
                        }, 50);
                    }
                }
            });
        }
    }
}
