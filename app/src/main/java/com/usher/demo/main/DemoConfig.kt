package com.usher.demo.main

import com.usher.demo.awesome.decoration.ContactsActivity
import com.usher.demo.awesome.decoration.StickyHeaderActivity
import com.usher.demo.awesome.drag.DragActiity
import com.usher.demo.awesome.itemtouch.ChannelEditActivity
import com.usher.demo.awesome.smarthome.SmartHomeActivity
import com.usher.demo.image.*
import com.usher.demo.kotlin.KotlinActivity
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
    private const val KEY_IMAGE = "image"
    private const val KEY_VIEW = "view"
    private const val KEY_MATERIAL = "material"
    private const val KEY_AWESOME = "awesome"
    private const val KEY_OTHER = "other"
    private const val KEY_KOTLIN = "kotlin"

    init {
        configMap[KEY_MAIN] = listOf(
                DemoItem("rx", KEY_RX, DemoListActivity::class.java),
                DemoItem("image", KEY_IMAGE, DemoListActivity::class.java),
                DemoItem("material", KEY_MATERIAL, DemoListActivity::class.java),
                DemoItem("view", KEY_VIEW, DemoListActivity::class.java),
                DemoItem("web", KEY_WEB, DemoListActivity::class.java),
                DemoItem("other", KEY_OTHER, DemoListActivity::class.java),
                DemoItem("awesome", KEY_AWESOME, DemoListActivity::class.java),
                DemoItem("kotlin", KEY_KOTLIN, KotlinActivity::class.java)
        )

        configMap[KEY_RX] = listOf(
                DemoItem("splash", RxSplashActivity::class.java),
                DemoItem("sum", RxSumActivity::class.java),
                DemoItem("exit", RxExitActivity::class.java),
                DemoItem("search", RxSearchActivity::class.java)
        )

        configMap[KEY_IMAGE] = listOf(
                DemoItem("blur", ImageBlurActivity::class.java),
                DemoItem("scale type", ImageScaleTypeActivity::class.java),
                DemoItem("picasso transform", PicassoTransformationActivity::class.java),
                DemoItem("bitmap mesh", DrawBitmapMeshActivity::class.java),
                DemoItem("ripple", RippleActivity::class.java)
        )

        configMap[KEY_WEB] = listOf(
                DemoItem("three", ThreeActivity::class.java),
                DemoItem("angular", AngularActivity::class.java)
        )

        configMap[KEY_MATERIAL] = listOf(
                DemoItem("fitsSystemWindow", FitsSystemWindowActivity::class.java),
                DemoItem("tab", TabLayoutActivity::class.java),
                DemoItem("profile", ProfileActivity::class.java),
                DemoItem("home", HomeActivity::class.java)
        )

        configMap[KEY_VIEW] = listOf(
                DemoItem("marquee text", MarqueeTextActivity::class.java),
                DemoItem("wave", WaveActivity::class.java),
                DemoItem("pager", PagerActivity::class.java),
                DemoItem("seat selection", SeatSelectionActivity::class.java),
                DemoItem("seat selection2", SeatSelection2Activity::class.java),
                DemoItem("loading", LoadingActivity::class.java),
                DemoItem("chart", ChartActivity::class.java),
                DemoItem("loop recycler", LoopRecyclerActivity::class.java),
                DemoItem("scan", ScanActivity::class.java),
                DemoItem("index", IndexActivity::class.java),
                DemoItem("color picker", ColorPickerActivity::class.java)
        )

        configMap[KEY_OTHER] = listOf(
                DemoItem("notification", NotificationActivity::class.java),
                DemoItem("launch mode", AActivity::class.java),
                DemoItem("log", LogActivity::class.java)
        )

        configMap[KEY_AWESOME] = listOf(
                DemoItem("channel edit", ChannelEditActivity::class.java),
                DemoItem("sticky header", StickyHeaderActivity::class.java),
                DemoItem("contacts", ContactsActivity::class.java),
                DemoItem("smart home", SmartHomeActivity::class.java),
                DemoItem("drag", DragActiity::class.java)
        )
    }

    fun getDemoConfig(key: String?): List<DemoItem> = configMap[key ?: KEY_MAIN] ?: listOf()
}

data class DemoItem(val desc: String, val key: String?, val cls: Class<*>) {
    constructor(desc: String, cls: Class<*>) : this(desc, null, cls)
}