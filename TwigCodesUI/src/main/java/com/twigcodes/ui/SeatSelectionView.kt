package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.recyclerview.dataChanges
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.jakewharton.rxbinding3.view.globalLayouts
import com.jakewharton.rxbinding3.view.touches
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.seat_selection_layout.view.*

class SeatSelectionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val ROW_COUNT = 8
        private const val COLUMN_COUNT = 16
    }

    private val mDataChangeSubject = PublishSubject.create<Unit>()
    private var mSeatData: ArrayList<ArrayList<Status>> = arrayListOf()
    private lateinit var mRowAdapter: RowAdapter

    enum class Status(var value: String) {
        IDLE("unsold"),
        SELECTED("sold"),
        DISABLED("disabled"),
        PENDING("pending")
    }

    data class Seat(var status: Status, var spanSize: Int = 1)

    data class SeatClick(var rowPosition: Int, var columnPosition: Int)

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.seat_selection_layout, this)
        initView()
    }

    private fun initView() {
        mRowAdapter = RowAdapter(mSeatData, COLUMN_COUNT)

        mRowAdapter.seatClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { click ->
                    mSeatData[click.rowPosition][click.columnPosition] = mSeatData[click.rowPosition][click.columnPosition].let { status ->
                        when (status) {
                            Status.IDLE -> Status.PENDING
                            Status.PENDING -> Status.IDLE
                            else -> status
                        }
                    }
                    mRowAdapter.notifyDataSetChanged()
                }

        mRowAdapter.dataChanges()
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { mDataChangeSubject.onNext(Unit) }

        selection_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        number_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        selection_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    selection_recyclerview.adapter = mRowAdapter.apply { itemHeight = selection_recyclerview.width / COLUMN_COUNT }
                    number_recyclerview.adapter = NumberAdapter(List(ROW_COUNT) { "${it + 1}" }).apply { itemHeight = selection_recyclerview.width / COLUMN_COUNT }
                }

        //滑动座位区域的同时对座位排号做相应距离的滚动, 同时禁止手动滑动座位排号
        selection_recyclerview.scrollEvents()
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    number_recyclerview.scrollBy(0, it.dy)
                }

        number_recyclerview.touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe()
    }

    fun setData(data: List<ArrayList<Status>>) {
        mSeatData.clear()
        mSeatData.addAll(data)
        mRowAdapter.notifyDataSetChanged()
    }

    fun dataChanges() = mDataChangeSubject

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