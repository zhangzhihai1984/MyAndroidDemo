package com.usher.demo.awesome

import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.layoutmanager.LoopLayoutManager
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_loop_hint.*
import java.util.concurrent.TimeUnit

class LoopHintActivity : BaseActivity(R.layout.activity_loop_hint) {

    override fun initView() {
        val data = listOf(*resources.getStringArray(R.array.selected_channels))

        loop_hint_recyclerview.layoutManager = LoopLayoutManager()
        loop_hint_recyclerview.adapter = LoopHintAdapter(data)

        val itemHeight = resources.getDimensionPixelSize(R.dimen.loop_hint_item_height)

        val itemChanges = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .doOnNext { loop_hint_recyclerview.smoothScrollBy(0, itemHeight) }
                .map { it.toInt() + 1 }
                .startWithItem(0)

        loop_hint_mask.clicks()
                .compose(RxUtil.singleClick())
                .withLatestFrom(itemChanges) { _, position -> position }
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    showToast(data[position % data.size])
                }
    }

    private class LoopHintAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_loop_hint, data) {

        override fun convert(holder: BaseViewHolder, item: String) {
            (holder.itemView as TextView).text = item
        }
    }
}