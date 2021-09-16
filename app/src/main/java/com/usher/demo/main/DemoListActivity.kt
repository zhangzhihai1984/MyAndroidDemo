package com.usher.demo.main

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_demo_list.*
import kotlinx.android.synthetic.main.item_demo.view.*

class DemoListActivity : BaseActivity(R.layout.activity_demo_list) {

    override fun initView() {
        val tag = intent.getStringExtra(DemoConfig.TAG_KEY)
        val demoItems = DemoConfig.getDemoConfig(tag)
        val adapter = DemoAdapter(demoItems)

        demo_recyclerview.layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        demo_recyclerview.adapter = adapter

        adapter.itemClicks()
                .compose(RxUtil.singleClick())
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    val demoItem = demoItems[position]
                    startActivity(Intent(this, demoItem.cls).apply {
                        putExtra(DemoConfig.TAG_KEY, demoItem.key)
                    })
                }

        setting_imageview.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { startActivity(Intent(this, SettingActivity::class.java)) }
    }

    private class DemoAdapter(data: List<DemoItem>) : RxBaseQuickAdapter<DemoItem, BaseViewHolder>(R.layout.item_demo, data) {
        override fun convert(holder: BaseViewHolder, demoItem: DemoItem) {
            holder.itemView.run {
                title_textview.text = demoItem.title
                desc_textview.text = demoItem.desc
                desc_textview.visibility = if (demoItem.desc.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }
    }
}