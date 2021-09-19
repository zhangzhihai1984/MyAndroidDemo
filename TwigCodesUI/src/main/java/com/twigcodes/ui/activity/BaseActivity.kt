package com.twigcodes.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.twigcodes.ui.util.PermissionUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

open class BaseActivity(contentLayoutId: Int, private val statusBarThemeForDayMode: Theme = Theme.DARK_AUTO, private val fullScreen: Boolean = false, private val hideNavigationBar: Boolean = false) : AppCompatActivity(contentLayoutId) {
    private val mActivityResultSubject = PublishSubject.create<ActivityResult>()
    private var mIsLocalNightMode = false

    private var lightStatusBarTheme = true
        set(isLight) {
            field = isLight

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val decorView = window.decorView
                val visibility = decorView.systemUiVisibility

                decorView.systemUiVisibility = when (isLight) {
                    true -> visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    false -> visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
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

    enum class NightModeStrategy {
        AUTO,
        YES,
        NO
    }

    data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fullScreen) {
//            ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
//                controller.hide(WindowInsetsCompat.Type.statusBars())
//                controller.hide(WindowInsetsCompat.Type.navigationBars())
//            }
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        if (hideNavigationBar) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        }

        mIsLocalNightMode = isSystemNightMode
        updateStatusBarTheme(mIsLocalNightMode)

        initData()
        initView()
    }

    open fun initData() {}

    open fun initView() {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.firstOrNull { it == PackageManager.PERMISSION_DENIED }?.run {
            PermissionUtil.deny()
        } ?: PermissionUtil.grant()
    }

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