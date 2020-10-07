package com.usher.demo.rx

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.widget.textChanges
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_rx_search.*
import kotlinx.android.synthetic.main.item_search.view.*
import java.util.concurrent.TimeUnit

class RxSearchActivity : BaseActivity(R.layout.activity_rx_search, Theme.LIGHT_AUTO) {

    override fun initView() {
        val searchResults = arrayListOf<String>()
        val cities = listOf("北京市", "上海市", "天津市", "重庆市", "沈阳市", "台北市")
        val adapter = SearchAdapter(searchResults)

        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = adapter

        edittext.textChanges()
                .debounce(500, TimeUnit.MILLISECONDS)
                .map { it.trim() }
                .distinctUntilChanged()
                .map { cities.filter { city -> city.contains(it) } }
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    searchResults.clear()
                    searchResults.addAll(it)
                    adapter.notifyDataSetChanged()
                }
    }

    private class SearchAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_search, data) {
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.itemView.name_textview.text = item
        }
    }
}