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
            Status.SELECTED, Status.SELECTED,
            Status.DISABLED,
            Status.IDLE, Status.IDLE, Status.IDLE,
            Status.DISABLED, Status.DISABLED
    )
    private val mBehindList = arrayListOf<Seat>()
    private lateinit var mFrontAdapter: FrontAdapter
    private lateinit var mBehindAdapter: BehindAdapter

    enum class Status {
        IDLE,
        SELECTED,
        DISABLED
    }

    data class Seat(var status: Status, var spanSize: Int = 1)

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
                        Status.IDLE -> Status.SELECTED
                        Status.SELECTED -> Status.IDLE
                        else -> Status.DISABLED
                    }

                    updatePresentationList()
                }


        seat_recyclerview.layoutManager = GridLayoutManager(this, SEAT_COUNT, RecyclerView.VERTICAL, false)
        seat_recyclerview.adapter = mFrontAdapter
        seat_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    seat_recyclerview.updateLayoutParams { height = seat_recyclerview.width / SEAT_COUNT - resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2 }
                }

        mBehindAdapter = BehindAdapter(mBehindList)
        mBehindAdapter.setSpanSizeLookup { _, i -> mBehindList[i].spanSize }


        span_recyclerview.layoutManager = GridLayoutManager(this, SEAT_COUNT, RecyclerView.VERTICAL, false)
        span_recyclerview.adapter = mBehindAdapter
        span_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    span_recyclerview.updateLayoutParams { height = span_recyclerview.width / SEAT_COUNT - resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2 }
                }

        updatePresentationList()
    }

    private class FrontAdapter(data: List<Status>) : RxBaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_seat_selection_icon, data) {
        override fun convert(helper: BaseViewHolder, item: Status) {
        }

    }

    private class BehindAdapter(data: List<Seat>) : RxBaseQuickAdapter<Seat, BaseViewHolder>(R.layout.item_seat_selection_span, data) {
        override fun convert(helper: BaseViewHolder, seat: Seat) {
            val res = when (seat.status) {
                Status.IDLE -> R.drawable.selection_default_background
                Status.SELECTED -> R.drawable.selection_selected_background
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
                if (mFrontList[j] == status) {
                    spanSize += 1
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