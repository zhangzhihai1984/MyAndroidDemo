package com.usher.demo.awesome.decoration

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_sticky_header.*

class StickyHeaderActivity : BaseActivity(Theme.DARK_ONLY) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_header)
        initView()
    }

    private fun initView() {
        val data = listOf(
                listOf(*resources.getStringArray(R.array.sticky_list1)),
                listOf(*resources.getStringArray(R.array.sticky_list2)),
                listOf(*resources.getStringArray(R.array.sticky_list3)),
                listOf(*resources.getStringArray(R.array.sticky_list4)),
                listOf(*resources.getStringArray(R.array.sticky_list5)),
                listOf(*resources.getStringArray(R.array.sticky_list6)),
                listOf(*resources.getStringArray(R.array.sticky_list7))
        ).mapIndexed { i, list ->
            list.map { content -> ItemInfo("$i", "GROUP $i", content) }
        }.flatten()

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.addItemDecoration(StickyItemDecoration(this, data))
        recyclerview.adapter = StickyHeaderAdapter(data)
    }

    private class StickyHeaderAdapter(data: List<ItemInfo>) : RxBaseQuickAdapter<ItemInfo, BaseViewHolder>(R.layout.item_sticky_header, data) {
        override fun convert(helper: BaseViewHolder, item: ItemInfo) {
            helper.setText(R.id.content_textview, item.content)
        }
    }
}