package com.usher.demo.main

import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.usher.demo.R

class DemoAdapter(data: List<DemoItem>) : RxBaseQuickAdapter<DemoItem, BaseViewHolder>(R.layout.demo_item_layout, data) {
    override fun convert(helper: BaseViewHolder, demoItem: DemoItem) {
        helper.setText(R.id.desc_textview, demoItem.desc)
    }
}