package com.twigcodes.ui.indicator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.twigcodes.ui.R
import kotlin.math.abs

class PageIndicatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_INDICATOR_VALUE = 15
    }

    private val mIndicatorWidth: Int
    private val mIndicatorHeight: Int
    private val mIndicatorMargin: Int
    private val mInAnimatorResId: Int
    private val mOutAnimatorResId: Int
    private val mIndicatorBackgroundResId: Int
    private val mIndicatorIdleBackgroundResId: Int

    private lateinit var mViewPager: ViewPager
    private var mInAnimator: Animator
    private var mOutAnimator: Animator
    private var mImmediateInAnimator: Animator
    private var mImmediateOutAnimator: Animator
    private var mCurrentPosition = -1

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView)
        mIndicatorWidth = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_width, DEFAULT_INDICATOR_VALUE)
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_height, DEFAULT_INDICATOR_VALUE)
        mIndicatorMargin = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_margin, DEFAULT_INDICATOR_VALUE)
        mInAnimatorResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_animator_in, R.animator.page_indicator_default_animator)
        mOutAnimatorResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_animator_out, -1)
        mIndicatorBackgroundResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_drawable, R.drawable.page_indicator_default_drawable)
        mIndicatorIdleBackgroundResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_idle_drawable, mIndicatorBackgroundResId)

//        val defaultValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_INDICATOR_WIDTH, resources.displayMetrics).run { roundToInt() }

        mInAnimator = createInAnimator()
        mImmediateInAnimator = createInAnimator().setDuration(0)
        mOutAnimator = createOutAnimator()
        mImmediateOutAnimator = createOutAnimator().setDuration(0)

        val o = a.getInt(R.styleable.PageIndicatorView_indicator_orientation, -1)
        orientation = if (o >= 0) o else HORIZONTAL

        val g = a.getInt(R.styleable.PageIndicatorView_indicator_gravity, -1)
        gravity = if (g >= 0) g else Gravity.CENTER

        a.recycle()
    }

    private fun createInAnimator(): Animator =
            AnimatorInflater.loadAnimator(context, mInAnimatorResId)

    private fun createOutAnimator(): Animator =
            if (mOutAnimatorResId <= 0)
                AnimatorInflater.loadAnimator(context, mInAnimatorResId).apply { interpolator = Interpolator { abs(1.0f - it) } }
            else
                AnimatorInflater.loadAnimator(context, mOutAnimatorResId)

    fun setViewPager(viewPager: ViewPager) {
        mViewPager = viewPager
        mViewPager.run {
            adapter?.registerDataSetObserver(mDataSetObserver)
                    ?: throw IllegalStateException("Please set adapter for the ViewPager")
            mCurrentPosition = currentItem
            removeOnPageChangeListener(mPageChangeListener)
            addOnPageChangeListener(mPageChangeListener)
        }

        createIndicators()
    }

    private val mDataSetObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            mCurrentPosition = mViewPager.currentItem

            createIndicators()
        }
    }

    private val mPageChangeListener: OnPageChangeListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            if (mInAnimator.isRunning)
                mInAnimator.end()

            if (mOutAnimator.isRunning)
                mOutAnimator.end()

            getChildAt(mCurrentPosition)?.run {
                setBackgroundResource(mIndicatorIdleBackgroundResId)
                mOutAnimator.setTarget(this)
                mOutAnimator.start()
            }

            getChildAt(position)?.run {
                setBackgroundResource(mIndicatorBackgroundResId)
                mInAnimator.setTarget(this)
                mInAnimator.start()
            }

            mCurrentPosition = position
        }
    }

    private fun createIndicators() {
        removeAllViews()

        val count = mViewPager.adapter?.count ?: 0
        IntRange(0, count - 1).forEach {
            if (mCurrentPosition == it)
                addIndicator(mIndicatorBackgroundResId, mImmediateInAnimator)
            else
                addIndicator(mIndicatorIdleBackgroundResId, mImmediateOutAnimator)
        }
    }

    private fun addIndicator(@DrawableRes backgroundResId: Int, animator: Animator) {
        if (animator.isRunning)
            animator.end()

        val indicator = View(context).apply { setBackgroundResource(backgroundResId) }
        val lp = LayoutParams(mIndicatorWidth, mIndicatorHeight).apply {
            when (orientation) {
                HORIZONTAL -> {
                    leftMargin = mIndicatorMargin
                    rightMargin = mIndicatorMargin
                }
                else -> {
                    topMargin = mIndicatorMargin
                    bottomMargin = mIndicatorMargin
                }
            }
        }
        addView(indicator, lp)
        animator.setTarget(indicator)
        animator.start()
    }
}