package com.usher.demo.demonstration

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.twigcodes.ui.PorterDuffXfermodeDiagramView
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_porterduff_xfermode.*

class PorterDuffXfermodeActivity : BaseActivity(Theme.LIGHT_AUTO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_porterduff_xfermode)
        initView()
    }

    private fun initView() {
        val modes = listOf(
                PorterDuff.Mode.CLEAR,
                PorterDuff.Mode.SRC,
                PorterDuff.Mode.DST,
                PorterDuff.Mode.SRC_OVER,
                PorterDuff.Mode.DST_OVER,
                PorterDuff.Mode.SRC_IN,
                PorterDuff.Mode.DST_IN,
                PorterDuff.Mode.SRC_OUT,
                PorterDuff.Mode.DST_OUT,
                PorterDuff.Mode.SRC_ATOP,
                PorterDuff.Mode.DST_ATOP,
                PorterDuff.Mode.XOR,
                PorterDuff.Mode.DARKEN,
                PorterDuff.Mode.LIGHTEN,
                PorterDuff.Mode.MULTIPLY,
                PorterDuff.Mode.SCREEN,
                PorterDuff.Mode.ADD,
                PorterDuff.Mode.OVERLAY
        )
        recyclerview.layoutManager = GridLayoutManager(this, 4, RecyclerView.VERTICAL, false)
        recyclerview.adapter = PorterDuffAdapter(modes)
    }

    private class PorterDuffAdapter(data: List<PorterDuff.Mode>) : RxBaseQuickAdapter<PorterDuff.Mode, BaseViewHolder>(R.layout.item_porterduff_xfermode, data) {
        override fun convert(helper: BaseViewHolder, mode: PorterDuff.Mode) {
            helper.getView<PorterDuffXfermodeDiagramView>(R.id.porterduff_view).mode = mode
            helper.setText(R.id.textview, mode.name)
        }
    }
}