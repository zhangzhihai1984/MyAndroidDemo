package com.twigcodes.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.twigcodes.ui.util.PermissionUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

open class BaseActivity(private val statusBarThemeForDayMode: Theme = Theme.DARK_AUTO) : AppCompatActivity() {
    private val mActivityResultSubject = PublishSubject.create<ActivityResult>()
    private var mIsLocalNightMode = false

    private var lightStatusBarTheme = true
        set(isLight) {
            field = isLight

            val decorView = window.decorView
            val visibility = decorView.systemUiVisibility

            decorView.systemUiVisibility = when (isLight) {
                true -> visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                false -> visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

    private val isSystemNightMode: Boolean
        get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    enum class Theme(val isLight: Boolean, val isAuto: Boolean) {
        LIGHT_AUTO(true, true),
        DARK_AUTO(false, true),
        LIGHT_ONLY(true, false),
        DARK_ONLY(false, false)
    }

    data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mIsLocalNightMode = isSystemNightMode
        updateStatusBarTheme(mIsLocalNightMode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) =
            grantResults.firstOrNull { it == PackageManager.PERMISSION_DENIED }?.run {
                PermissionUtil.deny()
            } ?: PermissionUtil.grant()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mActivityResultSubject.onNext(ActivityResult(requestCode, resultCode, data))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val isCurrentNightMode = isSystemNightMode

        if (mIsLocalNightMode != isCurrentNightMode)
            onUIModeChanged(isCurrentNightMode)

        mIsLocalNightMode = isCurrentNightMode
    }

    fun activityResult(): Observable<ActivityResult> = mActivityResultSubject

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    /**
     * statusbar的theme有light和dark之分, 同时也有auto和only之分.
     *
     * 1. 如果是only的话, 表明不关心当前系统ui mode为day还是night, 保持设置的light或night不变.
     *
     * 2. 如果是auto的话, 表明需要根据当前系统ui mode为day还是night进行相应的切换.
     * (1) 如果当前为day mode, 保持设置的light或night不变.
     * (2) 如果当前为night mode, 如果设置的是light, 变为dark, 如果设置的dark, 变为light
     */
    fun updateStatusBarTheme(isNightMode: Boolean) {
        lightStatusBarTheme = when {
            !statusBarThemeForDayMode.isAuto || !isNightMode -> statusBarThemeForDayMode.isLight
            statusBarThemeForDayMode.isLight -> false
            else -> true
        }
    }

    open fun onUIModeChanged(isNightMode: Boolean) {}
}