package com.twigcodes.ui.pager

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.twigcodes.ui.R

class LoopViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    companion object {
        private const val DEFAULT_AUTO_PAGER_PERIOD = 1000
    }

    private val mAutoPageEnabled: Boolean
    private val mAutoPagePeriod: Int
    private val mOnPageChangeListeners = arrayListOf<OnPageChangeListener>()

    private var mRawAdapter: PagerAdapter? = null
    private var mLoopAdapter: LoopPagerAdapter? = null

    private var mPreviousPosition = -1


    private val mOnPageChangeListener = object : OnPageChangeListener {

        /**
         * position指的是屏幕中间可见的最左侧item的index.
         * positionOffset为position对应item的偏移比例, 取值[0, 1).
         * positionOffsetPixels为position对应item的偏移的像素值.
         *
         * 以[0,1,2,3]为例, 如果当前为"1", 那么position为1, positionOffset为0.
         * 如果向右滑动, "1"向右移动, "0"从左侧进入, 此时position变为0, positionOffset由1逐渐减小.
         * 如果向左滑动, "1"向左移动, "2"从右侧进入, 此时position仍为1, positionOffset由0逐渐增大.
         *
         * 鉴于循环效果的设计, 以[3,0,1,2,3,0]为例:
         * 由于在首尾添加了两个item, 我们需要传递"修正"position, 这样外部的indictor就可以显示在正确的位置.
         * 但是我们需要考虑两种情况:
         * 1. 当在首部的"0"时, indicator已经处于最左侧, 如果此时继续向右滑动.
         * 2. 当在尾部的"3"时, indicator已经处于最右侧, 如果此时继续向左滑动.
         * 我们需要看到的效果是:
         * 1. "3"从左侧进入, indicator保持不动, 当"3"进入50%后, indicator移动至最右侧.
         * 2. "0"从右侧进入, indicator保持不动, 当"0"进入50%后, indicator移动至最左侧.
         * 实现方案:
         * 1. "3"从左侧进入意味着"修正"position变为"3", "0"从右侧进入意味着"修正"position仍为"3", 也就是说当
         * "修正"position为3的时候就需要引起我们的注意了.
         * 2. indicator保持不动意味着positionOffset和positionOffsetPixels两个参数传0.
         * 3. positionOffset小于0.5意味着"3"从左侧进入并且超过50%或是"0"从右侧进入没超过50%, 此时position传3, 否则传0.
         */
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val revisedPosition = getRevisedPosition(position)

            mOnPageChangeListeners.forEach { listener ->
                mRawAdapter?.run {
                    if (revisedPosition == count - 1) {
                        if (positionOffset < .5f)
                            listener.onPageScrolled(revisedPosition, 0f, 0)
                        else
                            listener.onPageScrolled(0, 0f, 0)

                    } else
                        listener.onPageScrolled(revisedPosition, positionOffset, positionOffsetPixels)
                }
            }
        }

        /**
         * 鉴于循环效果的设计, 为了防止在首尾滑动item后indicator的动画出现两次, 这里需要判断一下相邻两次的"修正"position是否相同.
         */
        override fun onPageSelected(position: Int) {
            val revisedPosition = getRevisedPosition(position)
            if (mPreviousPosition != revisedPosition) {
                mPreviousPosition = revisedPosition

                mOnPageChangeListeners.forEach { listener -> listener.onPageSelected(revisedPosition) }
            }
        }

        /**
         * 当滑动停止, 我们需要根据当前的index进行对应的处理, 以[3,0,1,2,3,0]为例:
         * 如果当前的index为0, 说明已经滑动至最左侧的"3", 为了营造出循环的效果, 这时需要跳转到index为4的"3".
         * 如果当前的index为5, 说明已经滑动至最右侧的"0", 为了营造出循环的效果, 这时需要跳转到index为1的"0".
         */
        override fun onPageScrollStateChanged(state: Int) {
            if (state == SCROLL_STATE_IDLE) {
                mRawAdapter?.let { rawAdapter ->
                    currentItem
                    when (super@LoopViewPager.getCurrentItem()) {
                        0 -> setCurrentItem(rawAdapter.count - 1, false)
                        rawAdapter.count + 1 -> setCurrentItem(0, false)
                    }
                }

                mOnPageChangeListeners.forEach { listener -> listener.onPageScrollStateChanged(state) }
            }
        }
    }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoopViewPager, 0, 0)
        mAutoPageEnabled = a.getBoolean(R.styleable.LoopViewPager_pager_autopage_enabled, false)
        mAutoPagePeriod = a.getInt(R.styleable.LoopViewPager_pager_autopage_period, DEFAULT_AUTO_PAGER_PERIOD)
        a.recycle()

        super.addOnPageChangeListener(mOnPageChangeListener)
    }

    fun getRevisedPosition(position: Int): Int =
            mRawAdapter?.run {
                if (count <= 0)
                    throw Exception("Adapter is empty")
                (position - 1 + count) % count
            } ?: throw Exception("Adapter is NULL")

    /**
     * 这里需要将currentItem置为0, 否则, 以[3,0,1,2,3,0]为例, 会默认显示"3"而不是"0".
     */
    override fun setAdapter(adapter: PagerAdapter?) {
        mRawAdapter = adapter

        mRawAdapter?.let { rawAdapter ->
            rawAdapter.registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    mLoopAdapter?.notifyDataSetChanged()
                }
            })

            mLoopAdapter = LoopPagerAdapter(rawAdapter)

            currentItem = 0
        }

        super.setAdapter(mLoopAdapter)
    }

    override fun getAdapter(): PagerAdapter? = mRawAdapter

    /**
     * [ViewPager.getCurrentItem]获得的是"real"的index, 由于在首尾添加了两个item, 因此此处需要"revise"一下.
     */
    override fun getCurrentItem(): Int {
        val currentItem = super.getCurrentItem()

        mRawAdapter?.let { rawAdapter ->
            if (rawAdapter.count > 0) {
                mLoopAdapter?.let { loopAdapter ->
                    if (currentItem < loopAdapter.count - 1)
                        return getRevisedPosition(currentItem)
                }
            }
        }

        return 0
    }

    override fun setCurrentItem(item: Int) = setCurrentItem(item, false)

    /**
     * 由于在首尾添加了两个item, 因此此处的index应做+1处理, 也就是传递"real"的index.
     */
    override fun setCurrentItem(item: Int, smoothScroll: Boolean) = super.setCurrentItem(item + 1, smoothScroll)

    override fun setOnPageChangeListener(listener: OnPageChangeListener) = addOnPageChangeListener(listener)

    override fun addOnPageChangeListener(listener: OnPageChangeListener) = mOnPageChangeListeners.add(listener).run { Unit }

    override fun removeOnPageChangeListener(listener: OnPageChangeListener) = mOnPageChangeListeners.remove(listener).run { Unit }

    override fun clearOnPageChangeListeners() = mOnPageChangeListeners.clear()

}