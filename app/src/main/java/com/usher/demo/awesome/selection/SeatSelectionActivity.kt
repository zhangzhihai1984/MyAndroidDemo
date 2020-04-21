package com.usher.demo.awesome.selection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import kotlinx.android.synthetic.main.activity_seat_selection.*

class SeatSelectionActivity : AppCompatActivity() {
    companion object {
        private const val SEAT_COUNT = 8
    }

    private val mFrontList = arrayListOf(
            SeatStatus.SELECTED, SeatStatus.SELECTED,
            SeatStatus.DISABLED,
            SeatStatus.IDLE, SeatStatus.IDLE, SeatStatus.IDLE,
            SeatStatus.DISABLED, SeatStatus.DISABLED
    )
    private val mBehindList = arrayListOf<Seat>()
    private lateinit var mFrontAdapter: FrontAdapter
    private lateinit var mBehindAdapter: BehindAdapter

    enum class SeatStatus {
        IDLE,
        SELECTED,
        DISABLED
    }

    data class Seat(var status: SeatStatus, var spanSize: Int = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)
        initView()
    }

    private fun initView() {
        mFrontAdapter = FrontAdapter(mFrontList)
        mFrontAdapter.itemClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { position ->
                    mFrontList[position] = when (mFrontList[position]) {
                        SeatStatus.IDLE -> SeatStatus.SELECTED
                        SeatStatus.SELECTED -> SeatStatus.IDLE
                        else -> SeatStatus.DISABLED
                    }

                    updatePresentationList()
                }


        front_recyclerview.layoutManager = GridLayoutManager(this, SEAT_COUNT, RecyclerView.VERTICAL, false)
        front_recyclerview.adapter = mFrontAdapter
        front_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    front_recyclerview.updateLayoutParams { height = front_recyclerview.width / SEAT_COUNT - resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2 }
                }

        mBehindAdapter = BehindAdapter(mBehindList)
        mBehindAdapter.setSpanSizeLookup { _, i -> mBehindList[i].spanSize }


        behind_recyclerview.layoutManager = GridLayoutManager(this, SEAT_COUNT, RecyclerView.VERTICAL, false)
        behind_recyclerview.adapter = mBehindAdapter
        behind_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    behind_recyclerview.updateLayoutParams { height = behind_recyclerview.width / SEAT_COUNT - resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2 }
                }

        updatePresentationList()
    }

    private class FrontAdapter(data: List<SeatStatus>) : RxBaseQuickAdapter<SeatStatus, BaseViewHolder>(R.layout.item_seat_selection_behind, data) {
        override fun convert(helper: BaseViewHolder, item: SeatStatus) {
        }

    }

    private class BehindAdapter(data: List<Seat>) : RxBaseQuickAdapter<Seat, BaseViewHolder>(R.layout.item_seat_selection_behind, data) {
        override fun convert(helper: BaseViewHolder, seat: Seat) {
            val res = when (seat.status) {
                SeatStatus.IDLE -> R.drawable.selection_default_background
                SeatStatus.SELECTED -> R.drawable.selection_selected_background
                else -> R.drawable.selection_disabled_background
            }
            helper.itemView.setBackgroundResource(res)
        }

    }

    private fun updatePresentationList() {
        mBehindList.clear()
        var spanSize: Int
        var j: Int
        var i = 0
        while (i < mFrontList.size) {
            spanSize = 1
            val status = mFrontList[i]
            j = i + 1
            while (j < mFrontList.size) {
                spanSize += if (mFrontList[j] == status) {
                    1
                } else {
                    mBehindList.add(Seat(status, spanSize))
                    break
                }
                j++
            }
            if (j == mFrontList.size) {
                mBehindList.add(Seat(status, spanSize))
            }
            i += spanSize
        }
        mBehindAdapter.notifyDataSetChanged()
    }
}