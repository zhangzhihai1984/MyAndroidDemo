package com.usher.demo.main

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import kotlinx.android.synthetic.main.activity_demo_list.*

class DemoListActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_list)
        initView()
    }

    private fun initView() {
        val tag = intent.getStringExtra(DemoConfig.TAG_KEY)
        val demoItems = DemoConfig.getDemoConfig(tag)
        val adapter = DemoAdapter(demoItems)

        recyclerview.layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        recyclerview.adapter = adapter

        adapter.itemClicks()
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { position ->
                    val demoItem = demoItems[position]
                    startActivity(Intent(this, demoItem.cls).apply {
                        putExtra(DemoConfig.TAG_KEY, demoItem.key)
                    })
                }
    }
}