package com.usher.demo.view

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.layoutmanager.LoopLayoutManager
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_loop_recycler.*

class LoopRecyclerActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loop_recycler)
        initView()
    }

    private fun initView() {
        val data = List(10) { "$it" }

        vertical_recyclerview.layoutManager = LoopLayoutManager(RecyclerView.VERTICAL)
        vertical_recyclerview.adapter = VerticalAdapter(data)
        vertical_recyclerview.scrollStateChanges()
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .`as`(RxUtil.autoDispose(this))
                .subscribe {
//                    val position = (vertical_recyclerview.layoutManager as LoopLayoutManager2).reviseOffset()
//                    Log.i("zzh", "idle $position")
                }

        horizontal_recyclerview.layoutManager = LoopLayoutManager(RecyclerView.HORIZONTAL)
        horizontal_recyclerview.adapter = HorizontalAdapter(data)
        horizontal_recyclerview.scrollStateChanges()
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .`as`(RxUtil.autoDispose(this))
                .subscribe {

                }
    }

    private class VerticalAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_loop_recycler_vertical, data) {
        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.name_textview, item)
        }
    }

    private class HorizontalAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_loop_recycler_horizontal, data) {
        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.name_textview, item)
        }
    }
}