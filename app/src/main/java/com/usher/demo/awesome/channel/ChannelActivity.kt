package com.usher.demo.awesome.channel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_channel.*

class ChannelActivity : BaseActivity(Theme.LIGHT_AUTO) {

    private var mMoveAnimatorSet: AnimatorSet? = null
    private var mCacheAnimatorSet: AnimatorSet? = null
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
        val mSelectedChannelList = arrayListOf(*resources.getStringArray(R.array.selected_channels))
        val mRecommendedChannelList = arrayListOf(*resources.getStringArray(R.array.recommended_channels))

        val mAdapter = ChannelAdapter(this, mSelectedChannelList, mRecommendedChannelList)

        val mGridLayoutManager = GridLayoutManager(this, 4, RecyclerView.VERTICAL, false)
        mGridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) {
                    4
                } else if (position <= mSelectedChannelList.size) {
                    1
                } else if (position == mSelectedChannelList.size + 1) {
                    4
                } else {
                    1
                }
            }
        }

        recyclerview.layoutManager = mGridLayoutManager
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
                mMoveAnimatorSet!!.playTogether(xAnimator, yAnimator)
                mMoveAnimatorSet!!.start()
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, position: Int) {
                placeholder_imageview.setVisibility(View.INVISIBLE)
            }
        })
        mAdapter.setOnChannelRemoveListener { view, location, isAdd ->
            val targetView = mGridLayoutManager.findViewByPosition(if (isAdd) mSelectedChannelList.size else mSelectedChannelList.size + 2)
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
            mCacheAnimatorSet!!.playTogether(xAnimator, yAnimator)
            mCacheAnimatorSet!!.start()
        }
        mMoveAnimatorSet = AnimatorSet()
        mMoveAnimatorSet!!.interpolator = LinearInterpolator()
        mMoveAnimatorSet!!.duration = 200
        mMoveAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mMoveStartLocation = mMoveEndLocation
                val params = placeholder_imageview.getLayoutParams() as RelativeLayout.LayoutParams
                params.leftMargin += placeholder_imageview.getTranslationX().toInt()
                params.topMargin += placeholder_imageview.getTranslationY().toInt()
                placeholder_imageview.setLayoutParams(params)
                placeholder_imageview.setTranslationX(0f)
                placeholder_imageview.setTranslationY(0f)
            }
        })
        mCacheAnimatorSet = AnimatorSet()
        mCacheAnimatorSet!!.interpolator = LinearInterpolator()
        mCacheAnimatorSet!!.duration = 300
        mCacheAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                cache_imageview.setVisibility(View.INVISIBLE)
            }
        })
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