package com.usher.demo.awesome.channel

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_channel.*
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_channel_header.view.*

class ChannelActivity : BaseActivity(Theme.LIGHT_AUTO) {

    private var mMoveStartLocation = IntArray(2)
    private var mMoveEndLocation = IntArray(2)
    private var mCacheStartLocation = IntArray(2)
    private var mCacheEndLocation = IntArray(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        initView()
    }

    private fun initView() {
        val selectedChannels = arrayListOf(*resources.getStringArray(R.array.selected_channels))
        val recommendedChannels = arrayListOf(*resources.getStringArray(R.array.recommended_channels))

        val dragAnimatorSet = AnimatorSet().apply {
            interpolator = LinearInterpolator()
            duration = 200
            doOnEnd {
                mMoveStartLocation = mMoveEndLocation
                placeholder_imageview.run {
                    updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        leftMargin += translationX.toInt()
                        topMargin += translationY.toInt()
                    }
                    translationX = 0f
                    translationY = 0f
                }
            }
        }

        val removeAnimatorSet = AnimatorSet().apply {
            interpolator = LinearInterpolator()
            duration = 300
            doOnEnd { cache_imageview.visibility = View.INVISIBLE }
        }

        val mAdapter = ChannelAdapter2(this, selectedChannels, recommendedChannels)

        recyclerview.layoutManager = GridLayoutManager(this, 4, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                        when (position) {
                            0, selectedChannels.size + 1 -> 4
                            else -> 1
                        }
            }
        }
        recyclerview.adapter = mAdapter
        //        mRecyclerView.setNestedScrollingEnabled(false);

//        val mItemTouchHelper = ItemTouchHelper(ItemDragHelperCallback(mAdapter))
//        mItemTouchHelper.attachToRecyclerView(recyclerview)

//        mAdapter.setOnItemDragListener(object : OnItemDragListener {
//            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, position: Int) {
//                placeholder_imageview.visibility = View.VISIBLE
//                val params = placeholder_imageview.getLayoutParams() as RelativeLayout.LayoutParams
//                params.width = viewHolder.itemView.width
//                mMoveStartLocation = getLocation(viewHolder.itemView)
//                val parentLocation = getLocation(root_layout)
//                params.leftMargin = mMoveStartLocation[0] //parent's margin left is 0
//                params.topMargin = mMoveStartLocation[1] - parentLocation[1]
//                placeholder_imageview.setLayoutParams(params)
//            }
//
//            override fun onItemDragMoving(current: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {
//                mMoveEndLocation = getLocation(target.itemView)
//                val xAnimator = ValueAnimator.ofFloat(0f, mMoveEndLocation[0] - mMoveStartLocation[0].toFloat())
//                xAnimator.addUpdateListener { animation -> placeholder_imageview.setTranslationX((animation.animatedValue as Float)) }
//                val yAnimator = ValueAnimator.ofFloat(0f, mMoveEndLocation[1] - mMoveStartLocation[1].toFloat())
//                yAnimator.addUpdateListener { animation -> placeholder_imageview.setTranslationY((animation.animatedValue as Float)) }
//                dragAnimatorSet.playTogether(xAnimator, yAnimator)
//                dragAnimatorSet.start()
//            }
//
//            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, position: Int) {
//                placeholder_imageview.setVisibility(View.INVISIBLE)
//            }
//        })
//        mAdapter.setOnChannelRemoveListener { view, location, isAdd ->
//            val targetView = (recyclerview.layoutManager as GridLayoutManager).findViewByPosition(if (isAdd) selectedChannels.size else selectedChannels.size + 2)
//            cache_imageview.setVisibility(View.VISIBLE)
//            cache_imageview.setImageBitmap(getCacheBitmap(view))
//            val params = cache_imageview.getLayoutParams() as RelativeLayout.LayoutParams
//            params.width = view.width
//
//            //Now the view's locaiton is [0, 0], so we should use the location which recorded in advance.
////                mCacheStartLocation = getLocation(view);
//            mCacheStartLocation = location
//            mCacheEndLocation = getLocation(targetView)
//            val parentLocation = getLocation(root_layout)
//            params.leftMargin = mCacheStartLocation[0]
//            params.topMargin = mCacheStartLocation[1] - parentLocation[1]
//            cache_imageview.setLayoutParams(params)
//            val xAnimator = ValueAnimator.ofFloat(0f, mCacheEndLocation[0] - mCacheStartLocation[0].toFloat())
//            xAnimator.addUpdateListener { animation -> cache_imageview.setTranslationX((animation.animatedValue as Float)) }
//            val yAnimator = ValueAnimator.ofFloat(0f, mCacheEndLocation[1] - mCacheStartLocation[1].toFloat())
//            yAnimator.addUpdateListener { animation -> cache_imageview.setTranslationY((animation.animatedValue as Float)) }
//            removeAnimatorSet.playTogether(xAnimator, yAnimator)
//            removeAnimatorSet.start()
//        }
    }

    private fun getLocation(view: View?): IntArray {
        val location = IntArray(2)
        view!!.getLocationOnScreen(location)
        return location
    }

    private fun getCacheBitmap(view: View): Bitmap {
        view.destroyDrawingCache()
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    private class ChannelAdapter2(private val context: Context, private val selectedChannels: ArrayList<String>, private val recommendedChannels: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            private const val ITEM_VIEW_TYPE_SELECTED_HEADER = 0
            private const val ITEM_VIEW_TYPE_SELECTED_CHANNEL = 1
            private const val ITEM_VIEW_TYPE_RECOMMENDED_HEADER = 2
            private const val ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL = 3
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                when (viewType) {
                    ITEM_VIEW_TYPE_SELECTED_CHANNEL -> LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false).run { SelectedChannelViewHolder(this) }
                    ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL -> LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false).run { RecommendedChannelViewHolder(this) }
                    else -> LayoutInflater.from(context).inflate(R.layout.item_channel_header, parent, false).run { HeaderViewHolder(this) }
                }

        override fun getItemCount(): Int = selectedChannels.size + recommendedChannels.size + 2

        override fun getItemViewType(position: Int): Int =
                when (position) {
                    0 -> ITEM_VIEW_TYPE_SELECTED_HEADER
                    in 1..selectedChannels.size -> ITEM_VIEW_TYPE_SELECTED_CHANNEL
                    selectedChannels.size + 1 -> ITEM_VIEW_TYPE_RECOMMENDED_HEADER
                    else -> ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL
                }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                ITEM_VIEW_TYPE_SELECTED_HEADER ->
                    holder.itemView.run {
                        title_textview.text = "已选频道"
                        desc_textview.text = "按住拖动调整排序"
                    }
                ITEM_VIEW_TYPE_RECOMMENDED_HEADER ->
                    holder.itemView.run {
                        title_textview.text = "推荐频道"
                        desc_textview.text = "点击添加频道"
                    }
                ITEM_VIEW_TYPE_SELECTED_CHANNEL ->
                    holder.itemView.run {
                        val pos = position - 1
                        name_textview.text = selectedChannels[pos]
                        if (pos == 0) {
                            name_textview.setTextColor(context.getColor(R.color.text_secondary))
                            delete_imageview.visibility = View.GONE
                        } else {
                            name_textview.setTextColor(context.getColor(R.color.text_primary))
                            delete_imageview.visibility = View.VISIBLE
                        }
                    }
                ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL ->
                    holder.itemView.run {
                        val pos = position - selectedChannels.size - 2
                        name_textview.text = recommendedChannels[pos]
                        name_textview.setTextColor(context.getColor(R.color.text_primary))
                        delete_imageview.visibility = View.GONE
                    }
            }
        }

        private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        private inner class SelectedChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

        private inner class RecommendedChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }
}