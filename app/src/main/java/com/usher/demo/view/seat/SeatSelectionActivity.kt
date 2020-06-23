package com.usher.demo.view.seat

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.SeatSelectionView
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_seat_selection.*

class SeatSelectionActivity : BaseActivity(Theme.DARK_ONLY) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)
        initView()
    }

    private fun initView() {
        var seatData: ArrayList<ArrayList<SeatSelectionView.Status>> = arrayListOf()

        val countAnimator = ValueAnimator.ofFloat(1f, 1.5f, 1f).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            addUpdateListener {
                count_textview.scaleX = animatedValue as Float
                count_textview.scaleY = animatedValue as Float
            }
        }

        seatselectionview.dataChanges()
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    val count = seatData.flatten().filter { status -> status == SeatSelectionView.Status.PENDING }.size
                    count_textview.text = "$count"
                    countAnimator.start()
                }

        refresh_imageview.clicks()
                .startWithItem(Unit)
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    val config = DataUtil.makeSeatConfig()
                    seatData = config.data
                    seatselectionview.setData(seatData, config.columnCount)
                }
    }
}