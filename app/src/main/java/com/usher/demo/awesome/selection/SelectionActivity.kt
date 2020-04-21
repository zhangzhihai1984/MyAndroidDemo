package com.usher.demo.awesome.selection

import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R

class SelectionActivity : AppCompatActivity() {
    companion object {
        private const val SELECTION_SIZE = 8
    }

    private val mBehindList = arrayListOf(
            SeatStatus.SELECTED, SeatStatus.SELECTED,
            SeatStatus.DISABLED,
            SeatStatus.IDLE, SeatStatus.IDLE, SeatStatus.IDLE,
            SeatStatus.DISABLED, SeatStatus.DISABLED
    )
    private val mFrontList = arrayListOf<Seat>()
    private lateinit var mAdapter: BehindAdapter
    private lateinit var mAdapter2: PresentationAdapter

    enum class SeatStatus {
        IDLE,
        SELECTED,
        DISABLED
    }

    data class Seat(var status: SeatStatus, var spanSize: Int = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        initData()
        initView()
    }

    private fun initData() {
        mAdapter = BehindAdapter(mBehindList)
        mAdapter.itemClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { position ->
                    mBehindList[position] = when (mBehindList[position]) {
                        SeatStatus.IDLE -> SeatStatus.SELECTED
                        SeatStatus.SELECTED -> SeatStatus.IDLE
                        else -> SeatStatus.DISABLED
                    }

                    updatePresentationList()
                }

        mAdapter2 = PresentationAdapter(this, mFrontList)
    }

    private fun initView() {
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val mGridLayoutManager = GridLayoutManager(this, SELECTION_SIZE, RecyclerView.VERTICAL, false)
        mRecyclerView.layoutManager = mGridLayoutManager
        mRecyclerView.adapter = mAdapter
        mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val params = mRecyclerView.layoutParams
                params.height = mRecyclerView.width / SELECTION_SIZE - resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2
                mRecyclerView.layoutParams = params
                mRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        val mRecyclerView2 = findViewById<RecyclerView>(R.id.recyclerview2)
        val mGridLayoutManager2 = GridLayoutManager(this, SELECTION_SIZE, RecyclerView.VERTICAL, false)
        mGridLayoutManager2.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return mFrontList[position].spanSize
            }
        }
        mRecyclerView2.layoutManager = mGridLayoutManager2
        mRecyclerView2.adapter = mAdapter2
        mRecyclerView2.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val params = mRecyclerView2.layoutParams
                params.height = mRecyclerView2.width / SELECTION_SIZE - resources.getDimensionPixelSize(R.dimen.selection_item_margin) * 2
                mRecyclerView2.layoutParams = params
                mRecyclerView2.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        updatePresentationList()
    }

    private class BehindAdapter(data: List<SeatStatus>) : RxBaseQuickAdapter<SeatStatus, BaseViewHolder>(R.layout.item_selection, data) {
        override fun convert(helper: BaseViewHolder, item: SeatStatus) {
        }

    }

    private fun updatePresentationList() {
        mFrontList.clear()
        var spanSize: Int
        var j: Int
        var i = 0
        while (i < mBehindList.size) {
            spanSize = 1
            val status = mBehindList[i]
            j = i + 1
            while (j < mBehindList.size) {
                spanSize += if (mBehindList[j] == status) {
                    1
                } else {
                    mFrontList.add(Seat(status, spanSize))
                    break
                }
                j++
            }
            if (j == mBehindList.size) {
                mFrontList.add(Seat(status, spanSize))
            }
            i += spanSize
        }
        mAdapter2.notifyDataSetChanged()
    }
}