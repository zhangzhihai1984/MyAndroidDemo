package com.usher.demo.setting

import android.content.Intent
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(R.layout.activity_setting) {

    override fun initView() {
        night_mode_textview.text = when (CommonUtil.getNightModeStrategy(this)) {
            NightModeStrategy.AUTO -> getString(R.string.setting_night_mode_auto)
            NightModeStrategy.YES -> getString(R.string.setting_night_mode_yes)
            NightModeStrategy.NO -> getString(R.string.setting_night_mode_no)
        }

        night_mode_layout.clicks()
                .compose(RxUtil.singleClick())
                .to(RxUtil.autoDispose(this))
                .subscribe { startActivity(Intent(this, NightModeActivity::class.java)) }
    }
}