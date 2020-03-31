package com.twigcodes.ui.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.subjects.PublishSubject

abstract class RxBaseQuickAdapter<T, K : BaseViewHolder>(layoutResId: Int, data: List<T>) : BaseQuickAdapter<T, K>(layoutResId, data) {
    private val mItemClickSubject = PublishSubject.create<Int>()
    private val mItemLongClickSubject = PublishSubject.create<Int>()
    private val mItemChildClickSubject = PublishSubject.create<ClickItem>()

    data class ClickItem(val view: View, val position: Int)

    init {
        setOnItemClickListener { _, _, position -> mItemClickSubject.onNext(position) }

        setOnItemLongClickListener { _, _, position ->
            mItemLongClickSubject.onNext(position)
            false
        }

        setOnItemChildClickListener { _, view, position -> mItemChildClickSubject.onNext(ClickItem(view, position)) }
    }

    fun itemClicks() = mItemClickSubject

    fun itemLongClicks() = mItemLongClickSubject

    fun itemChildClicks() = mItemChildClickSubject
}