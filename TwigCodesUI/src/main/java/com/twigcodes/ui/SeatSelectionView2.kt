package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.recyclerview.dataChanges
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.view.globalLayouts
import com.jakewharton.rxbinding4.view.scrollChangeEvents
import com.jakewharton.rxbinding4.view.touches
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.seat_selection_layout2.view.*

class SeatSelectionView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val DEFAULT_SEAT_WIDTH = 120
        private const val DEFAULT_SEAT_HEIGHT = 120
    }

    private val mDataChangeSubject = PublishSubject.create<Unit>()
    private var mSeatWidth: Int
    private var mSeatHeight: Int
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

    data class SeatClick(var rowPosition: Int, var columnPosition: Int)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SeatSelectionView2)
        mSeatWidth = a.getDimensionPixelSize(R.styleable.SeatSelectionView2_seatWidth, DEFAULT_SEAT_WIDTH)
        mSeatHeight = a.getDimensionPixelSize(R.styleable.SeatSelectionView2_seatHeight, DEFAULT_SEAT_HEIGHT)
        a.recycle()

        orientation = VERTICAL
        View.inflate(context, R.layout.seat_selection_layout2, this)
        initView()
    }

    private fun initView() {
        mSelectionAdapter = SelectionAdapter(mSelectionData, mSeatWidth, mSeatHeight)
        mSelectionAdapter.seatClicks()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(context as LifecycleOwner))
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
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { mDataChangeSubject.onNext(Unit) }

        selection_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        selection_recyclerview.adapter = mSelectionAdapter
        //纵向滑动座位区域的同时对座位排号做相应的纵向滚动, 同时禁止手动滑动座位排号
        selection_recyclerview.scrollEvents()
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { index_recyclerview.scrollBy(0, it.dy) }

        mIndexAdapter = IndexAdapter(mIndexData, mSeatHeight)
        index_recyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        index_recyclerview.adapter = mIndexAdapter
        index_recyclerview.touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe()

        //横向滑动座位区域的同时对屏幕区域做相应的横向滚动, 同时禁止手动滑动屏幕区域
        selection_scrollview.scrollChangeEvents()
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe { screen_scrollview.scrollTo(it.scrollX, 0) }

        screen_scrollview.touches { true }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe()
    }

    fun setData(data: List<ArrayList<Status>>, itemWidth: Int = mSeatWidth, itemHeight: Int = mSeatHeight) {
        mSeatWidth = itemWidth
        mSeatHeight = itemHeight

        mSelectionData.clear()
        mSelectionData.addAll(data)
        mSelectionAdapter.itemWidth = mSeatWidth
        mSelectionAdapter.itemHeight = mSeatHeight
        mSelectionAdapter.notifyDataSetChanged()

        mIndexData.clear()
        mIndexData.addAll(List(data.size) { it })
        mIndexAdapter.itemHeight = itemHeight
        mIndexAdapter.notifyDataSetChanged()

        selection_recyclerview.globalLayouts()
                .take(1)
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    screen_layout.updateLayoutParams { width = selection_layout.width }
                    screen_view.updateLayoutParams { width = selection_layout.width * 2 / 3 }

                    selection_scrollview.scrollTo(0, 0)
                    selection_recyclerview.layoutManager?.scrollToPosition(0)
                    index_recyclerview.layoutManager?.scrollToPosition(0)
                }
    }

    fun dataChanges() = mDataChangeSubject

    private class SelectionAdapter(data: List<List<Status>>, var itemWidth: Int, var itemHeight: Int) : RxBaseQuickAdapter<List<Status>, SelectionAdapter.SelectionViewHolder>(R.layout.item_seat_selection2, data) {

        private val mClickSubject = PublishSubject.create<SeatClick>()

        override fun convert(helper: SelectionViewHolder, statusList: List<Status>) {
            helper.itemView.tag = helper.layoutPosition
            helper.statusAdapter.itemWidth = itemWidth
            helper.statusAdapter.itemHeight = itemHeight
            helper.statusAdapter.setNewData(statusList)
        }

        fun seatClicks() = mClickSubject

        private inner class SelectionViewHolder(view: View) : BaseViewHolder(view) {
            val statusAdapter = StatusAdapter(listOf(), itemWidth, itemHeight)

            init {
                (view as RecyclerView).run {
                    layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
                    adapter = statusAdapter
                }

                statusAdapter.itemClicks()
                        .compose(RxUtil.getSchedulerComposer())
                        .to(RxUtil.autoDispose(mContext as LifecycleOwner))
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

    private class StatusAdapter(data: List<Status>, var itemWidth: Int, var itemHeight: Int) : RxBaseQuickAdapter<Status, BaseViewHolder>(R.layout.item_seat_selection2_status, data) {

        override fun convert(helper: BaseViewHolder, status: Status) {
            helper.itemView.updateLayoutParams {
                width = itemWidth
                height = itemHeight
            }
            helper.setVisible(R.id.seat_imageview, true)
            val res = when (status) {
                Status.IDLE -> R.drawable.seat_selection_idle_background
                Status.SELECTED -> R.drawable.seat_selection_selected_background
                Status.DISABLED -> {
                    helper.setVisible(R.id.seat_imageview, false)
                    R.drawable.seat_selection_disabled_background
                }
                Status.PENDING -> R.drawable.seat_selection_pending_background
            }
            helper.itemView.setBackgroundResource(res)
        }
    }

    private class IndexAdapter(data: List<Int>, var itemHeight: Int) : RxBaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_seat_selection2_index, data) {
        override fun convert(helper: BaseViewHolder, index: Int) {
            helper.itemView.updateLayoutParams { height = itemHeight }
            val str = "${index + 1}"
            (helper.itemView as TextView).text = str
        }
    }
}