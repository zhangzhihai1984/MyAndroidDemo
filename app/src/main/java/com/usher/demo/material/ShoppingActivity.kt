package com.usher.demo.material

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.LogUtil
import kotlinx.android.synthetic.main.activity_shopping.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class ShoppingActivity : BaseActivity(R.layout.activity_shopping, Theme.DARK_ONLY) {

    override fun initView() {
        val topMarginMin = resources.getDimension(R.dimen.shopping_search_margin_top_min)
        val topMarginMax = resources.getDimension(R.dimen.shopping_search_margin_top_max)
        val mOffsetThreshold = topMarginMax - topMarginMin

        val leftMarginMin = resources.getDimension(R.dimen.shopping_search_margin_start_min)
        val leftMarginMax = resources.getDimension(R.dimen.shopping_search_margin_start_max)
        val leftMarginThreshold = leftMarginMax - leftMarginMin

        val rightMarginMin = resources.getDimension(R.dimen.shopping_search_margin_end_min)
        val rightMarginMax = resources.getDimension(R.dimen.shopping_search_margin_end_max)
        val rightMarginThreshold = rightMarginMax - rightMarginMin

        appbarlayout.offsetChanges()
                .distinctUntilChanged()
                .map { offset ->
                    LogUtil.log("offset: $offset")
                    max(0f, (mOffsetThreshold - abs(offset)) / mOffsetThreshold)
                }
                .to(RxUtil.autoDispose(this))
                .subscribe { percent ->
                    val finalTop = topMarginMin + percent * mOffsetThreshold
                    val finalLeft = leftMarginMax - percent * leftMarginThreshold
                    val finalright = rightMarginMax - percent * rightMarginThreshold
                    search_view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        topMargin = finalTop.roundToInt()
                        leftMargin = finalLeft.roundToInt()
                        rightMargin = finalright.roundToInt()
                    }

                    promise_layout.alpha = percent
                    mask_view.alpha = 1 - percent
                }

        statusbar_view.updateLayoutParams { height = SystemUtil.getStatusBarHeight(this@ShoppingActivity) }

        location_imageview.clicks()
                .to(RxUtil.autoDispose(this))
                .subscribe { showToast("Location") }
    }
}