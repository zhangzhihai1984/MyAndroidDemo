package com.usher.demo.awesome.channel

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_channel.*

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

        val mAdapter = ChannelAdapter(this, selectedChannels, recommendedChannels)

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

        val mItemTouchHelper = ItemTouchHelper(ItemDragHelperCallback(mAdapter))
        mItemTouchHelper.attachToRecyclerView(recyclerview)

        mAdapter.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, position: Int) {
                placeholder_imageview.visibility = View.VISIBLE
                val params = placeholder_imageview.getLayoutParams() as RelativeLayout.LayoutParams
                params.width = viewHolder.itemView.width
                mMoveStartLocation = getLocation(viewHolder.itemView)
                val parentLocation = getLocation(root_layout)
                params.leftMargin = mMoveStartLocation[0] //parent's margin left is 0
                params.topMargin = mMoveStartLocation[1] - parentLocation[1]
                placeholder_imageview.setLayoutParams(params)
            }

            override fun onItemDragMoving(current: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {
                mMoveEndLocation = getLocation(target.itemView)
                val xAnimator = ValueAnimator.ofFloat(0f, mMoveEndLocation[0] - mMoveStartLocation[0].toFloat())
                xAnimator.addUpdateListener { animation -> placeholder_imageview.setTranslationX((animation.animatedValue as Float)) }
                val yAnimator = ValueAnimator.ofFloat(0f, mMoveEndLocation[1] - mMoveStartLocation[1].toFloat())
                yAnimator.addUpdateListener { animation -> placeholder_imageview.setTranslationY((animation.animatedValue as Float)) }
                dragAnimatorSet.playTogether(xAnimator, yAnimator)
                dragAnimatorSet.start()
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, position: Int) {
                placeholder_imageview.setVisibility(View.INVISIBLE)
            }
        })
        mAdapter.setOnChannelRemoveListener { view, location, isAdd ->
            val targetView = (recyclerview.layoutManager as GridLayoutManager).findViewByPosition(if (isAdd) selectedChannels.size else selectedChannels.size + 2)
            cache_imageview.setVisibility(View.VISIBLE)
            cache_imageview.setImageBitmap(getCacheBitmap(view))
            val params = cache_imageview.getLayoutParams() as RelativeLayout.LayoutParams
            params.width = view.width

            //Now the view's locaiton is [0, 0], so we should use the location which recorded in advance.
//                mCacheStartLocation = getLocation(view);
            mCacheStartLocation = location
            mCacheEndLocation = getLocation(targetView)
            val parentLocation = getLocation(root_layout)
            params.leftMargin = mCacheStartLocation[0]
            params.topMargin = mCacheStartLocation[1] - parentLocation[1]
            cache_imageview.setLayoutParams(params)
            val xAnimator = ValueAnimator.ofFloat(0f, mCacheEndLocation[0] - mCacheStartLocation[0].toFloat())
            xAnimator.addUpdateListener { animation -> cache_imageview.setTranslationX((animation.animatedValue as Float)) }
            val yAnimator = ValueAnimator.ofFloat(0f, mCacheEndLocation[1] - mCacheStartLocation[1].toFloat())
            yAnimator.addUpdateListener { animation -> cache_imageview.setTranslationY((animation.animatedValue as Float)) }
            removeAnimatorSet.playTogether(xAnimator, yAnimator)
            removeAnimatorSet.start()
        }
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
}