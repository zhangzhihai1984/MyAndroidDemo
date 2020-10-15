package com.usher.demo.awesome.zipcode

import com.jakewharton.rxbinding4.widget.textChanges
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_zipcode.*

class ZipCodeActivity : BaseActivity(R.layout.activity_zipcode, Theme.LIGHT_AUTO) {

    override fun initView() {
        zipcode_edittext.textChanges()
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    zipcodeview.update(it.toList().map { c -> "$c" })
                }
    }
}