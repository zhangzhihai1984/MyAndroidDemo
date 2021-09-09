package com.usher.demo.awesome.drag

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.material.offsetChanges
import com.twigcodes.ui.util.RxUtil.autoDispose
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_drag.*
import java.util.*

class DragActiity : BaseActivity(R.layout.activity_drag) {
    private val mData: MutableList<ActionInfo> = ArrayList()

    override fun initView() {
        val callback = SceneTouchCallback()
        val mAdapter = ActionAdapter(this, mData, callback)
        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = mAdapter
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerview)
        appbarlayout.offsetChanges()
                .to(autoDispose(this))
                .subscribe { offset: Int ->
//                    Log.i("zzh", "offset: " + offset);
                    callback.isItemViewSwipeEnabled = offset <= -525 || offset >= 0
                    callback.setItemViewDragEnabled(offset <= -525 || offset >= 0)
                }
        mData.add(ActionInfo("智能插座"))
        mData.add(ActionInfo("空调"))
        mData.add(ActionInfo(10))
        mData.add(ActionInfo("空气盒子"))
        mData.add(ActionInfo("智能插座"))
        mData.add(ActionInfo("空调"))
        mData.add(ActionInfo(10))
        mData.add(ActionInfo("空气盒子"))
        mAdapter.notifyDataSetChanged()
    }
}