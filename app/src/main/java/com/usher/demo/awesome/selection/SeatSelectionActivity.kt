package com.usher.demo.awesome.selection

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_seat_selection.*

class SeatSelectionActivity : AppCompatActivity() {
    companion object {
        private const val ROW_COUNT = 8
        private const val COLUMN_COUNT = 16
    }

    private lateinit var mSeatData: ArrayList<ArrayList<Status>>

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
                            Status.IDLE.value -> Status.IDLE
                            Status.SELECTED.value -> Status.SELECTED
                            else -> Status.DISABLED
                        }
                    }
                }.map { row -> ArrayList(row) }
        )
    }

    private fun initView() {
        val rowAdapter = RowAdapter(mSeatData, COLUMN_COUNT)

        rowAdapter.seatClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { click ->
                    mSeatData[click.rowPosition][click.columnPosition] = mSeatData[click.rowPosition][click.columnPosition].let { status ->
                        when (status) {
                            Status.IDLE -> Status.PENDING
                            Status.PENDING -> Status.IDLE
                            else -> status
                        }
                    }
                    rowAdapter.notifyDataSetChanged()
                }

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        number_recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    recyclerview.adapter = rowAdapter.apply { itemHeight = recyclerview.width / COLUMN_COUNT }
                    number_recyclerview.adapter = NumberAdapter(List(ROW_COUNT) { "${it + 1}" }).apply { itemHeight = recyclerview.width / COLUMN_COUNT }
                }

        //滑动座位区域的同时对座位排号做相应距离的滚动, 同时禁止手动滑动座位排号
        recyclerview.scrollEvents()
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
                    number_recyclerview.scrollBy(0, it.dy)
                }

        number_recyclerview.touches { true }
                .`as`(RxUtil.autoDispose(this))
                .subscribe()
    }

    private class RowAdapter(data: List<List<Status>>, private val COLUMN_COUNT: Int, var itemHeight: Int = 100) : RxBaseQuickAdapter<List<Status>, RowAdapter.RowViewHolder>(R.layout.item_seat_selection_row, data) {

        private val mClickSubject = PublishSubject.create<SeatClick>()

        override fun convert(helper: RowViewHolder, statusList: List<Status>) {
            helper.itemView.tag = helper.layoutPosition
            helper.itemView.updateLayoutParams { height = itemHeight }
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
                }

                view.findViewById<RecyclerView>(R.id.span_recyclerview).run {
                    layoutManager = GridLayoutManager(mContext, COLUMN_COUNT, RecyclerView.VERTICAL, false)
                    adapter = spanAdapter
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

    private class NumberAdapter(data: List<String>, var itemHeight: Int = 100) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_seat_selection_number, data) {
        override fun convert(helper: BaseViewHolder, number: String) {
            helper.itemView.updateLayoutParams { height = itemHeight }
            (helper.itemView as TextView).text = number
        }
    }
}