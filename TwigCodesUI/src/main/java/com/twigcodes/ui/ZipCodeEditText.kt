package com.twigcodes.ui

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.globalLayouts
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class ZipCodeEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyleAttr) {
    private val mInputMethodDismissSubject = PublishSubject.create<Unit>()
    private var mCurrentWindowHeight = 0

    init {
        /**
         * 当屏幕可视区域高度变小可以简单理解为软件盘弹出, 同理当高度变大, 其实就是恢复之前的值, 可以认为是软键盘收起.
         *
         * 需要说明的是, 正常来说, 无论是通过back键还是虚拟键的向下按钮收起软键盘, 都可以通过onKeyPreIme监听到, 但是
         * 点击手机软键盘中的向下按钮后收起软键盘的过程onKeyPreIme是无法监听到的, 这主要是因为这个软键盘并不是原生的,
         * 而是由各手机厂商或是第三放厂商实现的.
         */
        globalLayouts()
                .map {
                    val r = Rect()
                    getWindowVisibleDisplayFrame(r)
                    r.bottom
                }
                .distinctUntilChanged()
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {height ->
                    val r = Rect()
                    getWindowVisibleDisplayFrame(r)
                    if (mCurrentWindowHeight > 0 && r.bottom > mCurrentWindowHeight)
                        mInputMethodDismissSubject.onNext(Unit)
                    mCurrentWindowHeight = r.bottom
                }
    }

//    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == 1) {
//            mInputMethodDismissSubject.onNext(Unit)
//        }
//        return super.onKeyPreIme(keyCode, event)
//    }

    fun inputMethodDismiss(): Observable<Unit> = mInputMethodDismissSubject
}