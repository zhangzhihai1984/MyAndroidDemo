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

class SeatSelectionView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_ROW_COUNT = 8
        private const val DEFAULT_COLUMN_COUNT = 16
    }

    private val mDataChangeSubject = PublishSubject.create<Unit>()
    private var mRowCount: Int
    private var mColumnCount: Int
    private var mSelectionData: ArrayList<ArrayList<Status>> = arrayListOf()
    private lateinit var mSelectionAdapter: SelectionAdapter

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
        mRowCount = a.getInteger(R.styleable.SeatSelectionView_rowCount, DEFAULT_ROW_COUNT)
        mColumnCount = a.getInteger(R.styleable.SeatSelectionView_columnCount, DEFAULT_COLUMN_COUNT)
        a.recycle()
    }

    private fun initView() {
        mSelectionAdapter = SelectionAdapter(mSelectionData, mColumnCount)

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
        index_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        selection_recyclerview.globalLayouts()
                .take(1)
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    selection_recyclerview.adapter = mSelectionAdapter.apply { itemHeight = selection_recyclerview.width / mColumnCount }
                    index_recyclerview.adapter = IndexAdapter(List(mRowCount) { it }).apply { itemHeight = selection_recyclerview.width / mColumnCount }
                }

        //滑动座位区域的同时对座位排号做相应距离的滚动, 同时禁止手动滑动座位排号
        selection_recyclerview.scrollEvents()
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    index_recyclerview.scrollBy(0, it.dy)
                }

        index_recyclerview.touches { true }
                .`as`(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe()
    }

    fun setData(data: List<ArrayList<Status>>, rowCount: Int = mRowCount, columnCount: Int = mColumnCount) {
        mRowCount = rowCount
        mColumnCount = columnCount
        mSelectionData.clear()
        mSelectionData.addAll(data)
        initView()
//        mSelectionAdapter.notifyDataSetChanged()
    }

    fun dataChanges() = mDataChangeSubject

    private class SelectionAdapter(data: List<List<Status>>, private val COLUMN_COUNT: Int, var itemHeight: Int = 100) : RxBaseQuickAdapter<List<Status>, SelectionAdapter.SelectionViewHolder>(R.layout.item_seat_selection, data) {

        private val mClickSubject = PublishSubject.create<SeatClick>()

        override fun convert(helper: SelectionViewHolder, statusList: List<Status>) {
            helper.itemView.tag = helper.layoutPosition
            helper.itemView.updateLayoutParams { height = itemHeight }
            helper.seatAdapter.setNewData(statusList)
            helper.statusAdapter.setNewData(getStatusList(statusList))
        }

        private fun getStatusList(statusList: List<Status>): List<Seat> {
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
            val seatAdapter = SeatAdapter(listOf())
            val statusAdapter = StatusAdapter(listOf())

            init {
                view.findViewById<RecyclerView>(R.id.seat_recyclerview).run {
                    layoutManager = GridLayoutManager(mContext, COLUMN_COUNT, RecyclerView.VERTICAL, false)
                    adapter = seatAdapter
                }

                view.findViewById<RecyclerView>(R.id.status_recyclerview).run {
                    layoutManager = GridLayoutManager(mContext, COLUMN_COUNT, RecyclerView.VERTICAL, false)
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

    private class IndexAdapter(data: List<Int>, var itemHeight: Int = 100) : RxBaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_seat_selection_index, data) {
        override fun convert(helper: BaseViewHolder, index: Int) {
            helper.itemView.updateLayoutParams { height = itemHeight }
            (helper.itemView as TextView).text = "${index + 1}"
        }
    }
}