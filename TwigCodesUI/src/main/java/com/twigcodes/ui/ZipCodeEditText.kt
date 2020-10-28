package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class ZipCodeEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyleAttr) {
    private val mInputMethodDismissSubject = PublishSubject.create<Unit>()

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == 1) {
            mInputMethodDismissSubject.onNext(Unit)
        }
        return super.onKeyPreIme(keyCode, event)
    }

    fun inputMethodDismiss(): Observable<Unit> = mInputMethodDismissSubject
}