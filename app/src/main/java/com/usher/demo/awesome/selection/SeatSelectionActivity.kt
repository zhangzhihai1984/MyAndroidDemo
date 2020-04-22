package com.usher.demo.awesome.selection

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.view.globalLayouts
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_seat_selection.*

class SeatSelectionActivity : AppCompatActivity() {

    enum class Status {
        IDLE,
        SELECTED,
        DISABLED
    }

    data class Seat(var status: Status, var spanSize: Int = 1)

    data class SeatClick(var rowPosition: Int, var columnPosition: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)
        initView()
    }

    private fun initView() {
        val seatStatusLists = listOf(arrayListOf(
                Status.SELECTED, Status.SELECTED,
                Status.DISABLED,
                Status.IDLE, Status.IDLE, Status.IDLE,
                Status.DISABLED, Status.DISABLED))

        val rowAdapter = RowAdapter(seatStatusLists)

        rowAdapter.seatClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { click ->
                    seatStatusLists[click.rowPosition][click.columnPosition] = when (seatStatusLists[click.rowPosition][click.columnPosition]) {
                        Status.IDLE -> Status.SELECTED
                        Status.SELECTED -> Status.IDLE
                        else -> Status.DISABLED
                    }
                    rowAdapter.notifyDataSetChanged()
                }

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = rowAdapter
    }

    private class RowAdapter(data: List<List<Status>>) : RxBaseQuickAdapter<List<Status>, RowAdapter.RowViewHolder>(R.layout.item_seat_selection_row, data) {
        companion object {
            private const val SEAT_COUNT_PER_ROW = 8
        }

        private val mClickSubject = PublishSubject.create<SeatClick>()

        override fun convert(helper: RowViewHolder, statusList: List<Status>) {
            helper.itemView.tag = helper.layoutPosition
            helper.seatAdapter.setNewData(statusList)
            helper.spanAdapter.setNewData(getSpanList(statusList))
        }

        private fun getSpanList(statusList: List<Status>): List<Seat> {
            val seatList = arrayListOf<Seat>()

            var spanSize: Int
            var j: Int
            var i = 0
            while (i < statusList.size) {
                spanSize = 1
                val status = statusList[i]
                j = i + 1
                while (j < statusList.size) {
                    if (statusList[j] == status) {
                        spanSize += 1
                    } else {
                        seatList.add(Seat(status, spanSize))
                        break
                    }

                    j++
                }
                if (j == statusList.size) {
                    seatList.add(Seat(status, spanSize))
                }
                i += spanSize
            }

            return seatList
        }

        fun seatClicks() = mClickSubject

        private inner class RowViewHolder(view: View) : BaseViewHolder(view) {
            val seatAdapter = SeatAdapter(listOf())
            val spanAdapter = SpanAdapter(listOf())

            init {
                view.findViewById<RecyclerView>(R.id.seat_recyclerview).run {
                    layoutManager = GridLayoutManager(mContext, SEAT_COUNT_PER_ROW, RecyclerView.VERTICAL, false)
                    adapter = seatAdapter
                    globalLayouts()
                            .take(1)
                            .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                            .subscribe { updateLayoutParams { height = getWidth() / SEAT_COUNT_PER_ROW - mContext.resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2 } }
                }

                view.findViewById<RecyclerView>(R.id.span_recyclerview).run {
                    layoutManager = GridLayoutManager(mContext, SEAT_COUNT_PER_ROW, RecyclerView.VERTICAL, false)
                    adapter = spanAdapter
                    globalLayouts()
                            .take(1)
                            .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                            .subscribe { updateLayoutParams { height = getWidth() / SEAT_COUNT_PER_ROW - mContext.resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2 } }
                }

                seatAdapter.itemClicks()
                        .compose(RxUtil.getSchedulerComposer())
                        .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                        .subscribe { position ->
                            mClickSubject.onNext(SeatClick(view.tag as Int, position))
                        }
            }
        }
    }

    private class SeatAdapter(data: List<Status>) : RxBaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_seat_selection_icon, data) {
        override fun convert(helper: BaseViewHolder, item: Status) {
        }

    }

    private class SpanAdapter(data: List<Seat>) : RxBaseQuickAdapter<Seat, BaseViewHolder>(R.layout.item_seat_selection_span, data) {
        init {
            setSpanSizeLookup { _, i -> mData[i].spanSize }
        }

        override fun convert(helper: BaseViewHolder, seat: Seat) {
            val res = when (seat.status) {
                Status.IDLE -> R.drawable.selection_default_background
                Status.SELECTED -> R.drawable.selection_selected_background
                else -> R.drawable.selection_disabled_background
            }
            helper.itemView.setBackgroundResource(res)
        }

    }
}