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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_channel.*
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_channel_header.view.*
import java.util.*

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
        val fixedChannels = listOf("关注", "推荐").map { it to ChannelAdapter.ITEM_VIEW_TYPE_FIXED_CHANNEL }
        val selectedChannels = listOf(*resources.getStringArray(R.array.selected_channels)).map { it to ChannelAdapter.ITEM_VIEW_TYPE_SELECTED_CHANNEL }
        val recommendedChannels = listOf(*resources.getStringArray(R.array.recommended_channels)).map { it to ChannelAdapter.ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL }
        val data = listOf(listOf("" to ChannelAdapter.ITEM_VIEW_TYPE_SELECTED_HEADER), fixedChannels, selectedChannels, listOf("" to ChannelAdapter.ITEM_VIEW_TYPE_RECOMMENDED_HEADER), recommendedChannels).flatten()

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

        val mAdapter = ChannelAdapter(this, data)

        recyclerview.layoutManager = GridLayoutManager(this, 4, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                        when (position) {
                            0, fixedChannels.size + selectedChannels.size + 1 -> 4
                            else -> 1
                        }
            }
        }
        recyclerview.adapter = mAdapter

        val touchCallback = ChannelTouchCallback()
        ItemTouchHelper(touchCallback).attachToRecyclerView(recyclerview)

        touchCallback.dragStarts()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    mAdapter.onDragStart(it.viewHolder)
                }

        touchCallback.dragMoving()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    mAdapter.onDragMoving(it.from, it.to)
                }

        touchCallback.dragEnds()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    mAdapter.onDragEnd(it.viewHolder)
                }

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

    private class ChannelAdapter(private val context: Context, private val data: List<Pair<String, Int>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            const val ITEM_VIEW_TYPE_SELECTED_HEADER = 0
            const val ITEM_VIEW_TYPE_FIXED_CHANNEL = 1
            const val ITEM_VIEW_TYPE_SELECTED_CHANNEL = 2
            const val ITEM_VIEW_TYPE_RECOMMENDED_HEADER = 3
            const val ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL = 4
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                when (viewType) {
                    ITEM_VIEW_TYPE_FIXED_CHANNEL -> LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false).run { FixedChannelViewHolder(this) }
                    ITEM_VIEW_TYPE_SELECTED_CHANNEL -> LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false).run { SelectedChannelViewHolder(this) }
                    ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL -> LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false).run { RecommendedChannelViewHolder(this) }
                    else -> LayoutInflater.from(context).inflate(R.layout.item_channel_header, parent, false).run { HeaderViewHolder(this) }
                }

        override fun getItemCount(): Int = data.size

        override fun getItemViewType(position: Int): Int = data[position].second

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
                ITEM_VIEW_TYPE_FIXED_CHANNEL ->
                    holder.itemView.run {
                        name_textview.text = data[position].first
                        name_textview.setTextColor(context.getColor(R.color.text_secondary))
                        delete_imageview.visibility = View.GONE
                    }
                ITEM_VIEW_TYPE_SELECTED_CHANNEL ->
                    holder.itemView.run {
                        name_textview.text = data[position].first
                        name_textview.setTextColor(context.getColor(R.color.text_primary))
                        delete_imageview.visibility = View.VISIBLE
                    }
                ITEM_VIEW_TYPE_RECOMMENDED_CHANNEL ->
                    holder.itemView.run {
                        name_textview.text = data[position].first
                        name_textview.setTextColor(context.getColor(R.color.text_primary))
                        delete_imageview.visibility = View.GONE
                    }
            }
        }

        fun onDragStart(holder: RecyclerView.ViewHolder) {
            holder.itemView.run {
                name_textview.elevation = 10f
                delete_imageview.visibility = View.INVISIBLE
            }
        }

        fun onDragMoving(from: Int, to: Int) {
            if (from < to) {
                for (i in from until to)
                    Collections.swap(data, i, i + 1)
            } else {
                for (i in from downTo to + 1)
                    Collections.swap(data, i, i - 1)
            }

            notifyItemMoved(from, to)
        }

        fun onDragEnd(holder: RecyclerView.ViewHolder) {
            holder.itemView.run {
                name_textview.elevation = 0f
                delete_imageview.visibility = View.VISIBLE
            }
        }

        private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        private inner class FixedChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        private inner class SelectedChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

        private inner class RecommendedChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }

    private class ChannelTouchCallback : ItemTouchHelper.Callback() {
        private val mDragStartSubject = PublishSubject.create<DragStart>()
        private val mDragMovingSubject = PublishSubject.create<DragMoving>()
        private val mDragEndSubject = PublishSubject.create<DragEnd>()

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
                when (viewHolder.itemViewType) {
                    ChannelAdapter.ITEM_VIEW_TYPE_SELECTED_CHANNEL -> {
                        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        makeMovementFlags(dragFlags, 0)
                    }
                    else -> makeMovementFlags(0, 0)
                }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            mDragMovingSubject.onNext(DragMoving(viewHolder, viewHolder.adapterPosition, target, target.adapterPosition))
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)

            viewHolder?.run {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    mDragStartSubject.onNext(DragStart(viewHolder, viewHolder.adapterPosition))
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            mDragEndSubject.onNext(DragEnd(viewHolder, viewHolder.adapterPosition))
        }

        override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean =
                current.itemViewType == target.itemViewType

        fun dragStarts() = mDragStartSubject

        fun dragMoving() = mDragMovingSubject

        fun dragEnds() = mDragEndSubject

        data class DragStart(val viewHolder: RecyclerView.ViewHolder, val position: Int)
        data class DragEnd(val viewHolder: RecyclerView.ViewHolder, val position: Int)
        data class DragMoving(val current: RecyclerView.ViewHolder, val from: Int, val target: RecyclerView.ViewHolder, val to: Int)
    }
}