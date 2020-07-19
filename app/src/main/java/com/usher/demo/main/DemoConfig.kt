package com.usher.demo.main

import com.usher.demo.awesome.decoration.ContactsActivity
import com.usher.demo.awesome.decoration.StickyHeaderActivity
import com.usher.demo.awesome.drag.DragActiity
import com.usher.demo.awesome.itemtouch.ChannelEditActivity
import com.usher.demo.demonstration.*
import com.usher.demo.material.FitsSystemWindowActivity
import com.usher.demo.material.ProfileActivity
import com.usher.demo.material.TabLayoutActivity
import com.usher.demo.material.home.HomeActivity
import com.usher.demo.other.LogActivity
import com.usher.demo.other.launchmode.AActivity
import com.usher.demo.other.notification.NotificationActivity
import com.usher.demo.rx.RxExitActivity
import com.usher.demo.rx.RxSearchActivity
import com.usher.demo.rx.RxSplashActivity
import com.usher.demo.rx.RxSumActivity
import com.usher.demo.view.*
import com.usher.demo.view.loading.LoadingActivity
import com.usher.demo.view.seat.SeatSelection2Activity
import com.usher.demo.view.seat.SeatSelectionActivity
import com.usher.demo.web.AngularActivity
import com.usher.demo.web.three.ThreeActivity

object DemoConfig {
    private val configMap = hashMapOf<String, List<DemoItem>>()
    const val TAG_KEY = "TAG_KEY"
    private const val KEY_MAIN = "main"
    private const val KEY_RX = "rx"
    private const val KEY_WEB = "web"
    private const val KEY_DEMONSTRATION = "demonstration"
    private const val KEY_VIEW = "view"
    private const val KEY_MATERIAL = "material"
    private const val KEY_AWESOME = "awesome"
    private const val KEY_OTHER = "other"

    init {
        configMap[KEY_MAIN] = listOf(
                DemoItem(KEY_RX, DemoListActivity::class.java, "rx"),
                DemoItem(KEY_DEMONSTRATION, DemoListActivity::class.java, "demonstration"),
                DemoItem(KEY_MATERIAL, DemoListActivity::class.java, "material"),
                DemoItem(KEY_VIEW, DemoListActivity::class.java, "view"),
                DemoItem(KEY_WEB, DemoListActivity::class.java, "web"),
                DemoItem(KEY_OTHER, DemoListActivity::class.java, "other"),
                DemoItem(KEY_AWESOME, DemoListActivity::class.java, "awesome")
        )

        configMap[KEY_RX] = listOf(
                DemoItem(RxSplashActivity::class.java, "splash"),
                DemoItem(RxSumActivity::class.java, "sum"),
                DemoItem(RxExitActivity::class.java, "exit"),
                DemoItem(RxSearchActivity::class.java, "search")
        )

        configMap[KEY_DEMONSTRATION] = listOf(
                DemoItem(ImageBlurActivity::class.java, "blur"),
                DemoItem(ImageScaleTypeActivity::class.java, "scale type"),
                DemoItem(BitmapXferActivity::class.java, "bitmap xfer"),
                DemoItem(BitmapWarpActivity::class.java, "bitmap warp"),
                DemoItem(BitmapRippleActivity::class.java, "bitmap ripple"),
                DemoItem(GraffitiActivity::class.java, "graffiti"),
                DemoItem(PorterDuffActivity::class.java, "porterduff"),
                DemoItem(PorterDuffColorFilterActivity::class.java, "Color Filter", "porterduff")
        )

        configMap[KEY_WEB] = listOf(
                DemoItem(ThreeActivity::class.java, "three"),
                DemoItem(AngularActivity::class.java, "angular")
        )

        configMap[KEY_MATERIAL] = listOf(
                DemoItem(FitsSystemWindowActivity::class.java, "fitsSystemWindow"),
                DemoItem(TabLayoutActivity::class.java, "tab"),
                DemoItem(ProfileActivity::class.java, "profile"),
                DemoItem(HomeActivity::class.java, "home")
        )

        configMap[KEY_VIEW] = listOf(
                DemoItem(MarqueeTextActivity::class.java, "marquee text"),
                DemoItem(WaveActivity::class.java, "wave"),
                DemoItem(PagerActivity::class.java, "pager"),
                DemoItem(SeatSelectionActivity::class.java, "seat selection"),
                DemoItem(SeatSelection2Activity::class.java, "seat selection2"),
                DemoItem(LoadingActivity::class.java, "loading"),
                DemoItem(ChartActivity::class.java, "chart"),
                DemoItem(LoopRecyclerActivity::class.java, "loop recycler"),
                DemoItem(ScanActivity::class.java, "scan"),
                DemoItem(IndexActivity::class.java, "index"),
                DemoItem(ColorPickerActivity::class.java, "color picker"),
                DemoItem(CurtainActivity::class.java, "curtain")
        )

        configMap[KEY_OTHER] = listOf(
                DemoItem(NotificationActivity::class.java, "notification"),
                DemoItem(AActivity::class.java, "launch mode"),
                DemoItem(LogActivity::class.java, "log")
        )

        configMap[KEY_AWESOME] = listOf(
                DemoItem(ChannelEditActivity::class.java, "channel edit"),
                DemoItem(StickyHeaderActivity::class.java, "sticky header"),
                DemoItem(ContactsActivity::class.java, "contacts"),
                DemoItem(DragActiity::class.java, "drag")
        )
    }

    fun getDemoConfig(key: String?): List<DemoItem> = configMap[key ?: KEY_MAIN] ?: listOf()
}

data class DemoItem(val key: String?, val cls: Class<*>, val title: String, val desc: String?) {
    constructor(key: String, cls: Class<*>, title: String) : this(key, cls, title, null)
    constructor(cls: Class<*>, title: String) : this(null, cls, title, null)
    constructor(cls: Class<*>, title: String, desc: String) : this(null, cls, title, desc)
}