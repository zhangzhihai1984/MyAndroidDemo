package com.twigcodes.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.twigcodes.ui.util.PermissionUtil
import io.reactivex.subjects.PublishSubject

open class BaseActivity(private val statusBarThemeForDayMode: Theme = Theme.DARK) : AppCompatActivity() {
    private val mActivityResultSubject = PublishSubject.create<ActivityResult>()
    private var mIsLocalNightMode = false

    enum class Theme(val isLight: Boolean) {
        LIGHT(true),
        DARK(false),
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

    fun activityResult() = mActivityResultSubject

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    fun updateStatusBarTheme(isNightMode: Boolean) {
        statusBarTheme = when {
            !isNightMode -> statusBarThemeForDayMode
            statusBarThemeForDayMode.isLight -> Theme.DARK
            else -> Theme.LIGHT
        }
    }

    open fun onUIModeChanged(isNightMode: Boolean) {}

    private var statusBarTheme = Theme.LIGHT
        set(theme) {
            field = theme
            val decorView = window.decorView
            val visibility = decorView.systemUiVisibility

            decorView.systemUiVisibility = when (theme) {
                Theme.LIGHT -> visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                Theme.DARK -> visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

    private val isSystemNightMode: Boolean
        get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}