package com.usher.demo.view.seat

import android.animation.ValueAnimator
import android.view.animation.OvershootInterpolator
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.SeatSelectionView2
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_seat_selection2.*

class SeatSelection2Activity : BaseActivity(R.layout.activity_seat_selection2, Theme.DARK_ONLY) {

    override fun initView() {
        var seatData: ArrayList<ArrayList<SeatSelectionView2.Status>> = arrayListOf()

        val countAnimator = ValueAnimator.ofFloat(1f, 1.5f, 1f).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            addUpdateListener {
                count_textview.scaleX = animatedValue as Float
                count_textview.scaleY = animatedValue as Float
            }
        }

        seatselectionview2.dataChanges()
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    val count = seatData.flatten().filter { status -> status == SeatSelectionView2.Status.PENDING }.size
                    count_textview.text = "$count"
                    countAnimator.start()
                }

        refresh_imageview.clicks()
                .startWithItem(Unit)
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    seatData = DataUtil.makeSeatData()
                    seatselectionview2.setData(seatData, (90..180).random(), (90..180).random())
                }
    }
}