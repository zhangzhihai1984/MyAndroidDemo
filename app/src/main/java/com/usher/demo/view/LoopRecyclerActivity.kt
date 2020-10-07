package com.usher.demo.view

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.recyclerview.scrollStateChanges
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.layoutmanager.LoopLayoutManager
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_loop_recycler.*

class LoopRecyclerActivity : BaseActivity(R.layout.activity_loop_recycler, Theme.LIGHT_AUTO) {

    override fun initView() {
        val data = List(10) { "$it" }

        vertical_recyclerview.layoutManager = LoopLayoutManager(RecyclerView.VERTICAL)
        vertical_recyclerview.adapter = VerticalAdapter(data)
        vertical_recyclerview.scrollStateChanges()
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    (vertical_recyclerview.layoutManager as LoopLayoutManager).getFirstViewPositionWithRevision(vertical_recyclerview)
                }

        horizontal_recyclerview.layoutManager = LoopLayoutManager(RecyclerView.HORIZONTAL)
        horizontal_recyclerview.adapter = HorizontalAdapter(data)
        horizontal_recyclerview.scrollStateChanges()
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    (horizontal_recyclerview.layoutManager as LoopLayoutManager).getFirstViewPositionWithRevision(horizontal_recyclerview)
                }
    }

    private class VerticalAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_loop_recycler_vertical, data) {
        override fun convert(holder: BaseViewHolder, item: String) {
            (holder.itemView as TextView).text = item
        }
    }

    private class HorizontalAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_loop_recycler_horizontal, data) {
        override fun convert(holder: BaseViewHolder, item: String) {
            (holder.itemView as TextView).text = item
        }
    }
}