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
        private const val DEFAULT_COLUMN_COUNT = 16
        private const val DEFAULT_SEAT_HEIGHT = 120
    }

    private val mDataChangeSubject = PublishSubject.create<Unit>()
    private var mColumnCount: Int
    private var mSelectionData: ArrayList<ArrayList<Status>> = arrayListOf()
    private var mIndexData: ArrayList<Int> = arrayListOf()
    private lateinit var mSelectionAdapter: SelectionAdapter
    private lateinit var mIndexAdapter: IndexAdapter

    enum class Status {
        IDLE,
        SELECTED,
        DISABLED,
        PENDING
    }

    data class Seat(var status: Status, var spanSize: Int = 1)

    data class SeatClick(var rowPosition: Int, var columnPosition: Int)

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.seat_selection_layout, this)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SeatSelectionView, defStyleAttr, defStyleRes)
        mColumnCount = a.getInteger(R.styleable.SeatSelectionView_columnCount, DEFAULT_COLUMN_COUNT)
        a.recycle()
        initView()
    }

    private fun initView() {
        mSelectionAdapter = SelectionAdapter(mSelectionData, mColumnCount, DEFAULT_SEAT_HEIGHT)
        mSelectionAdapter.seatClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { click ->
                    mSelectionData[click.rowPosition][click.columnPosition] = mSelectionData[click.rowPosition][click.columnPosition].let { status ->
                        when (status) {
                            Status.IDLE -> Status.PENDING
                            Status.PENDING -> Status.IDLE
                            else -> status
                        }
                    }
                    mSelectionAdapter.notifyDataSetChanged()
                }
        mSelectionAdapter.dataChanges()
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { mDataChangeSubject.onNext(Unit) }

        selection_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        selection_recyclerview.adapter = mSelectionAdapter
        //滑动座位区域的同时对座位排号做相应距离的滚动, 同时禁止手动滑动座位排号
        selection_recyclerview.scrollEvents()
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    index_recyclerview.scrollBy(0, it.dy)
                }

        mIndexAdapter = IndexAdapter(mIndexData, DEFAULT_SEAT_HEIGHT)
        index_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        index_recyclerview.adapter = mIndexAdapter
        index_recyclerview.touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe()
    }

    fun setData(data: List<ArrayList<Status>>, columnCount: Int = mColumnCount) {
        mColumnCount = columnCount
        mSelectionData.clear()
        mSelectionData.addAll(data)
        mSelectionAdapter.columnCount = columnCount
        mSelectionAdapter.notifyDataSetChanged()

        mIndexData.clear()
        mIndexData.addAll(List(data.size) { it })

        selection_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    mSelectionAdapter.itemHeight = selection_recyclerview.width / columnCount
                    mSelectionAdapter.notifyDataSetChanged()
                    mIndexAdapter.itemHeight = selection_recyclerview.width / columnCount
                    mIndexAdapter.notifyDataSetChanged()

                    selection_recyclerview.layoutManager?.scrollToPosition(0)
                    index_recyclerview.layoutManager?.scrollToPosition(0)
                }
    }

    fun dataChanges() = mDataChangeSubject

    private class SelectionAdapter(data: List<List<Status>>, var columnCount: Int, var itemHeight: Int) : RxBaseQuickAdapter<List<Status>, SelectionAdapter.SelectionViewHolder>(R.layout.item_seat_selection, data) {

        private val mClickSubject = PublishSubject.create<SeatClick>()

        override fun convert(helper: SelectionViewHolder, statusList: List<Status>) {
            val seatList = getSeatList(statusList)

            helper.itemView.tag = helper.layoutPosition
            helper.itemView.updateLayoutParams { height = itemHeight }

            helper.seatRecyclerView.layoutManager = GridLayoutManager(mContext, columnCount, RecyclerView.VERTICAL, false)
            helper.statusRecyclerView.layoutManager = GridLayoutManager(mContext, columnCount, RecyclerView.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return seatList[position].spanSize
                    }
                }
            }

            helper.seatAdapter.setNewData(statusList)
            helper.statusAdapter.setNewData(seatList)
        }

        private fun getSeatList(statusList: List<Status>): List<Seat> {
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

        private inner class SelectionViewHolder(view: View) : BaseViewHolder(view) {
            val seatRecyclerView: RecyclerView = view.findViewById(R.id.seat_recyclerview)
            val statusRecyclerView: RecyclerView = view.findViewById(R.id.status_recyclerview)
            val seatAdapter = SeatAdapter(listOf())
            val statusAdapter = StatusAdapter(listOf())

            init {
                seatRecyclerView.run {
                    layoutManager = GridLayoutManager(mContext, columnCount, RecyclerView.VERTICAL, false)
                    adapter = seatAdapter
                }

                statusRecyclerView.run {
                    layoutManager = GridLayoutManager(mContext, columnCount, RecyclerView.VERTICAL, false)
                    adapter = statusAdapter
                }

                seatAdapter.itemClicks()
                        .compose(RxUtil.getSchedulerComposer())
                        .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                        .subscribe { position ->
                            val seatClick = SeatClick(view.tag as Int, position)
                            when (data[seatClick.rowPosition][seatClick.columnPosition]) {
                                Status.IDLE, Status.PENDING -> mClickSubject.onNext(seatClick)
                                else -> {
                                }
                            }
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

    private class StatusAdapter(data: List<Seat>) : RxBaseQuickAdapter<Seat, BaseViewHolder>(R.layout.item_seat_selection_status, data) {
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

    private class IndexAdapter(data: List<Int>, var itemHeight: Int) : RxBaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_seat_selection_index, data) {
        override fun convert(helper: BaseViewHolder, index: Int) {
            helper.itemView.updateLayoutParams { height = itemHeight }
            val str = "${index + 1}"
            (helper.itemView as TextView).text = str
        }
    }
}