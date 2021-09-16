package com.usher.demo.awesome.zipcode

import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_zipcode.*

class ZipCodeActivity : BaseActivity(R.layout.activity_zipcode) {

    override fun initView() {
        zipcodeview.zipCodeChanges()
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { zipcode_textview.text = it }
    }
}