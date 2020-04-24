package com.usher.demo.awesome.selection

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import com.twigcodes.ui.SeatSelectionView
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_seat_selection.*

class SeatSelectionActivity : BaseActivity(Theme.DARK_ONLY) {
    companion object {
        private const val ROW_COUNT = 8
        private const val COLUMN_COUNT = 16
    }

    private lateinit var mSeatData: ArrayList<ArrayList<SeatSelectionView.Status>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)
        initData()
        initView()
    }

    private fun initData() {
        val mockServerData = Array(ROW_COUNT) {
            Array(COLUMN_COUNT) {
                when ((0..6).random()) {
                    in 0..3 -> "unsold"
                    4, 5 -> "sold"
                    else -> "disabled"
                }
            }.toList()
        }.toList()

        /*
        * 1. 把mockServerData[i][j]数据由服务器定义的字段转为客户端定义的枚举
        * 2. 把mockServerData内部的List转为ArrayList
        * 3. 把mockServerData这个List转为ArrayList
        */
        mSeatData = ArrayList(
                mockServerData.map { row ->
                    row.map { column ->
                        when (column) {
                            SeatSelectionView.Status.IDLE.value -> SeatSelectionView.Status.IDLE
                            SeatSelectionView.Status.SELECTED.value -> SeatSelectionView.Status.SELECTED
                            else -> SeatSelectionView.Status.DISABLED
                        }
                    }
                }.map { row -> ArrayList(row) }
        )
    }

    private fun initView() {
        val countAnimator = ValueAnimator.ofFloat(1f, 1.5f, 1f).apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            addUpdateListener {
                count_textview.scaleX = animatedValue as Float
                count_textview.scaleY = animatedValue as Float
            }
        }

        seatselectionview.dataChanges()
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    val count = mSeatData.flatten().filter { status -> status == SeatSelectionView.Status.PENDING }.size
                    count_textview.text = "$count"
                    countAnimator.start()
                }

        seatselectionview.setData(mSeatData)
    }
}