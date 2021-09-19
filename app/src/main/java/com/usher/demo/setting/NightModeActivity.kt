package com.usher.demo.setting

import android.content.Intent
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.main.DemoListActivity
import com.usher.demo.utils.CommonUtil
import com.usher.demo.widget.CommonDialog
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_night_mode.*

class NightModeActivity : BaseActivity(R.layout.activity_night_mode) {
    private val mCurrentNightModeStrategy by lazy { CommonUtil.getNightModeStrategy(this) }
    private lateinit var mPendingNightModeStrategy: NightModeStrategy

    override fun initView() {
        title_view.endTextClicks()
                .compose(RxUtil.singleClick())
                .flatMap {
                    if (mPendingNightModeStrategy != mCurrentNightModeStrategy)
                        CommonDialog(this)
                                .withContent(R.string.night_mode_save_hint)
                                .withDialogType(CommonDialog.ButtonType.DOUBLE_PRIMARY)
                                .clicks()
                                .map { true }
                    else
                        Observable.just(false)
                }
                .to(RxUtil.autoDispose(this))
                .subscribe { restart ->
                    CommonUtil.setNightModeStrategy(this, mPendingNightModeStrategy)
                    if (restart) {
                        startActivity(Intent(this, DemoListActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    finish()
                }

        night_only_layout.clicks()
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    mPendingNightModeStrategy = when (mPendingNightModeStrategy) {
                        NightModeStrategy.YES -> NightModeStrategy.NO
                        else -> NightModeStrategy.YES
                    }
                    update()
                }

        night_auto_layout.clicks()
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    mPendingNightModeStrategy = when (mPendingNightModeStrategy) {
                        NightModeStrategy.AUTO -> NightModeStrategy.NO
                        else -> NightModeStrategy.AUTO
                    }
                    update()
                }

        mPendingNightModeStrategy = mCurrentNightModeStrategy
        update()
    }

    private fun update() {
        night_only_checkbox.isChecked = mPendingNightModeStrategy == NightModeStrategy.YES
        night_auto_checkbox.isChecked = mPendingNightModeStrategy == NightModeStrategy.AUTO
    }
}