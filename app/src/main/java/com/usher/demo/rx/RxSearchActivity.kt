package com.usher.demo.rx

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding4.widget.textChanges
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_rx_search.*
import kotlinx.android.synthetic.main.item_search.view.*
import java.util.concurrent.TimeUnit

class RxSearchActivity : BaseActivity(R.layout.activity_rx_search) {
    private val mCities = listOf("北京市", "上海市", "天津市", "重庆市", "沈阳市", "台北市")
    private val mAdapter by lazy { SearchAdapter(listOf()) }

    private val getCities: (keyword: String) -> Observable<List<String>> = { keyword ->
        Observable.create { emitter ->
            val cities = mCities.filter { city -> city.contains(keyword) }
            emitter.onNext(cities)
        }
    }

    override fun initView() {
        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = mAdapter

        edittext.textChanges()
                .debounce(500, TimeUnit.MILLISECONDS)
                .map { it.trim().toString() }
                .distinctUntilChanged()
                .switchMap { getCities(it) }
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    mAdapter.setNewData(it)
                }
    }

    private class SearchAdapter(data: List<String>) : RxBaseQuickAdapter<String, BaseViewHolder>(R.layout.item_search, data) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.itemView.name_textview.text = item
        }
    }
}