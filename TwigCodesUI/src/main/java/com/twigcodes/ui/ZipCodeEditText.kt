package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText

class ZipCodeEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyleAttr) {

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        Log.i("zzh", "key: $keyCode")
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == 1)
            Log.i("zzh", "input dismiss")
        return super.onKeyPreIme(keyCode, event)
    }
}