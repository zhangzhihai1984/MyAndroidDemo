package com.usher.demo.material

import android.content.Intent
import com.jakewharton.rxbinding4.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_fitssystemwindow.*

class FitsSystemWindowActivity : BaseActivity(R.layout.activity_fitssystemwindow) {
    companion object {
        private const val EXTRA_NAME = "EXTRA_TAG"
        private const val EXTRA_VALUE_NONE = "NONE"
        private const val EXTRA_VALUE_ROOT = "ROOT"
        private const val EXTRA_VALUE_TOP = "TOP"
    }

    override fun initView() {
        val name = when (intent.getStringExtra(EXTRA_NAME)) {
            EXTRA_VALUE_ROOT -> {
                root_layout.fitsSystemWindows = true
                getString(R.string.fitssystemwindow_root_view)
            }
            EXTRA_VALUE_TOP -> {
                top_textview.fitsSystemWindows = true
                getString(R.string.fitssystemwindow_top_view)
            }
            else -> getString(R.string.fitssystemwindow_none_view)
        }

        status_textview.text = getString(R.string.fitssystemwindow_status_text, name)

        none_view_textview.clicks()
                .take(1)
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    startActivity(Intent(this, FitsSystemWindowActivity::class.java).apply {
                        putExtra(EXTRA_NAME, EXTRA_VALUE_NONE)
                    })
                    finish()
                }

        root_view_textview.clicks()
                .take(1)
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    startActivity(Intent(this, FitsSystemWindowActivity::class.java).apply {
                        putExtra(EXTRA_NAME, EXTRA_VALUE_ROOT)
                    })
                    finish()
                }

        top_view_textview.clicks()
                .take(1)
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    startActivity(Intent(this, FitsSystemWindowActivity::class.java).apply {
                        putExtra(EXTRA_NAME, EXTRA_VALUE_TOP)
                    })
                    finish()
                }
    }
}