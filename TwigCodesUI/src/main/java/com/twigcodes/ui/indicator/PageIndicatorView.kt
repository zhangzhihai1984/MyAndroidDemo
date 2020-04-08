package com.twigcodes.ui.indicator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.AnimatorRes
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.twigcodes.ui.R
import kotlin.math.abs
import kotlin.math.roundToInt

class PageIndicatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_INDICATOR_WIDTH = 5f
    }

    private var mIndicatorMargin: Int = -1
    private var mIndicatorWidth: Int = -1
    private var mIndicatorHeight: Int = -1
    private var mInAnimatorResId: Int = -1
    private var mOutAnimatorResId: Int = -1
    private var mIndicatorBackgroundResId = -1
    private var mIndicatorIdleBackgroundResId = -1

    private lateinit var mViewPager: ViewPager
    private lateinit var mInAnimator: Animator
    private lateinit var mOutAnimator: Animator
    private lateinit var mImmediateInAnimator: Animator
    private lateinit var mImmediateOutAnimator: Animator
    private var mCurrentPosition = -1

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView)
        val indicatorWidth = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_width, -1)
        val indicatorHeight = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_height, -1)
        val indicatorMargin = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_margin, -1)
        val inAnimatorResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_animator_in, -1)
        val outAnimatorResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_animator_out, -1)
        val indicatorBackgroundResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_drawable, -1)
        val indicatorIdleBackgroundResId = a.getResourceId(R.styleable.PageIndicatorView_indicator_idle_drawable, -1)

        config(
                indicatorWidth,
                indicatorHeight,
                indicatorMargin,
                inAnimatorResId,
                outAnimatorResId,
                indicatorBackgroundResId,
                indicatorIdleBackgroundResId
        )

        val o = a.getInt(R.styleable.PageIndicatorView_indicator_orientation, -1)
        orientation = if (o >= 0) o else HORIZONTAL

        val g = a.getInt(R.styleable.PageIndicatorView_indicator_gravity, -1)
        gravity = if (g >= 0) g else Gravity.CENTER

        a.recycle()
    }

    private fun config(
            indicatorWidth: Int,
            indicatorHeight: Int,
            indicatorMargin: Int,
            @AnimatorRes inAnimatorResId: Int = -1,
            @AnimatorRes outAnimatorrResId: Int = -1,
            @DrawableRes indicatorBackgroundResId: Int = -1,
            @DrawableRes indicatorIdleBackgroundResId: Int = -1
    ) {
        val defaultValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_INDICATOR_WIDTH, resources.displayMetrics).run { roundToInt() }
        mIndicatorWidth = indicatorWidth.takeIf { it > 0 } ?: defaultValue
        mIndicatorHeight = indicatorHeight.takeIf { it > 0 } ?: defaultValue
        mIndicatorMargin = indicatorMargin.takeIf { it >= 0 } ?: defaultValue
        mInAnimatorResId = inAnimatorResId.takeIf { it > 0 }
                ?: R.animator.page_indicator_default_animator
        mOutAnimatorResId = outAnimatorrResId.takeIf { it > 0 } ?: -1
        mIndicatorBackgroundResId = indicatorBackgroundResId.takeIf { it > 0 }
                ?: R.drawable.page_indicator_default_drawable
        mIndicatorIdleBackgroundResId = indicatorIdleBackgroundResId.takeIf { it > 0 }
                ?: mIndicatorBackgroundResId

        mInAnimator = createInAnimator()
        mImmediateInAnimator = createInAnimator().setDuration(0)
        mOutAnimator = createOutAnimator()
        mImmediateOutAnimator = createOutAnimator().setDuration(0)
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