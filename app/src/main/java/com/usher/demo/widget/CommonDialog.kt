package com.usher.demo.widget

import android.app.Dialog
import android.content.Context
import android.text.Spannable
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.clicks
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_common_layout.*

class CommonDialog(private val mContext: Context) : Dialog(mContext, R.style.FramelessDialog) {
    private val mClickSubject = PublishSubject.create<Click>()
    private var mData: Any? = null

    enum class ClickType {
        CONFIRM,
        CANCEL
    }

    enum class ButtonType {
        SINGLE,
        DOUBLE_PRIMARY,
        DOUBLE_WARN
    }

    class Click(var type: ClickType, var data: Any?)

    init {
        setContentView(R.layout.dialog_common_layout)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        initView()
    }

    private fun initView() {
        cancel_button.clicks()
                .take(1)
                .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                .subscribe {
                    mClickSubject.onNext(Click(ClickType.CANCEL, mData))
                    dismiss()
                }

        Observable.merge(confirm_button.clicks(), done_button.clicks())
                .take(1)
                .`as`(RxUtil.autoDispose(mContext as LifecycleOwner))
                .subscribe {
                    mClickSubject.onNext(Click(ClickType.CONFIRM, mData))
                    dismiss()
                }
    }

    fun withDialogType(type: ButtonType): CommonDialog = this.apply {
        when (type) {
            ButtonType.DOUBLE_PRIMARY -> {
                double_layout.visibility = View.VISIBLE
                single_layout.visibility = View.GONE
                confirm_button.setBackgroundResource(R.drawable.button_primary_corner_background)
            }
            //TODO:
            ButtonType.DOUBLE_WARN -> {
                double_layout.visibility = View.VISIBLE
                single_layout.visibility = View.GONE
                confirm_button.setBackgroundResource(R.drawable.button_primary_corner_background)
            }
            ButtonType.SINGLE -> {
                double_layout.visibility = View.GONE
                single_layout.visibility = View.VISIBLE
            }
        }
    }

    fun withCancelable(cancelable: Boolean): CommonDialog = this.apply { setCancelable(cancelable) }

    fun withTitle(titleRes: Int): CommonDialog = this.apply {
        title_textview.text = context.getString(titleRes)
        title_textview.visibility = View.VISIBLE
    }

    fun withTitle(title: String): CommonDialog = this.apply {
        title_textview.text = title
        title_textview.visibility = View.VISIBLE
    }

    fun withContent(contentRes: Int): CommonDialog = this.apply { content_textview.text = context.getString(contentRes) }

    fun withContent(content: Spannable): CommonDialog = this.apply { content_textview.text = content }

    fun withContent(content: String): CommonDialog = this.apply { content_textview.text = content }

    fun withData(data: Any?): CommonDialog = this.apply { mData = data }

    fun clicks(): Observable<Click> {
        show()
        return mClickSubject
    }
}