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

    enum class Status(var value: String) {
        IDLE("unsold"),
        SELECTED("sold"),
        DISABLED("disabled"),
        PENDING("pending")
    }

    data class Seat(var status: Status, var spanSize: Int = 1)

    data class SeatClick(var rowPosition: Int, var columnPosition: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)
        initView()
    }

    private fun initView() {
        val row = 5
        val column = 16
        val mockData = Array(row) {
            Array(column) {
                when ((0..6).random()) {
                    in 0..3 -> "unsold"
                    4, 5 -> "sold"
                    else -> "disabled"
                }
            }.toList()
        }.toList()

        var allStatus = mockData.map {
            it.map { name ->
                when (name) {
                    Status.IDLE.value -> Status.IDLE
                    Status.SELECTED.value -> Status.SELECTED
                    else -> Status.DISABLED
                }
            }
        }

        allStatus = ArrayList(allStatus.map { ArrayList(it) })

        val rowAdapter = RowAdapter(allStatus)

        rowAdapter.seatClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { click ->
                    allStatus[click.rowPosition][click.columnPosition] = allStatus[click.rowPosition][click.columnPosition].let { status ->
                        when (status) {
                            Status.IDLE -> Status.PENDING
                            Status.PENDING -> Status.IDLE
                            else -> status
                        }
                    }
                    rowAdapter.notifyDataSetChanged()
                }

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = rowAdapter
    }

    private class RowAdapter(data: List<List<Status>>, private val COLUMN_COUNT: Int = 16) : RxBaseQuickAdapter<List<Status>, RowAdapter.RowViewHolder>(R.layout.item_seat_selection_row, data) {

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
                    layoutManager = GridLayoutManager(mContext, COLUMN_COUNT, RecyclerView.VERTICAL, false)
                    adapter = seatAdapter
                    globalLayouts()
                            .take(1)
                            .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                            .subscribe { updateLayoutParams { height = getWidth() / COLUMN_COUNT } }
                }

                view.findViewById<RecyclerView>(R.id.span_recyclerview).run {
                    layoutManager = GridLayoutManager(mContext, COLUMN_COUNT, RecyclerView.VERTICAL, false)
                    adapter = spanAdapter
                    globalLayouts()
                            .take(1)
                            .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                            .subscribe { updateLayoutParams { height = getWidth() / COLUMN_COUNT } }
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
        override fun convert(helper: BaseViewHolder, status: Status) {
            helper.itemView.visibility = when (status) {
                Status.DISABLED -> View.INVISIBLE
                else -> View.VISIBLE
            }
        }
    }

    private class SpanAdapter(data: List<Seat>) : RxBaseQuickAdapter<Seat, BaseViewHolder>(R.layout.item_seat_selection_span, data) {
        init {
            setSpanSizeLookup { _, i -> mData[i].spanSize }
        }

        override fun convert(helper: BaseViewHolder, seat: Seat) {
            val res = when (seat.status) {
                Status.IDLE -> R.drawable.seat_selection_idle_background
                Status.SELECTED -> R.drawable.seat_selection_selected_background
                Status.DISABLED -> R.drawable.seat_selection_disabled_background
                Status.PENDING -> R.drawable.seat_selection_pending_background
            }
            helper.itemView.setBackgroundResource(res)
        }
    }
}