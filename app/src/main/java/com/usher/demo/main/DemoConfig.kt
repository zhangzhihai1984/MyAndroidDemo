package com.usher.demo.main

import com.usher.demo.awesome.channel.ChannelActivity
import com.usher.demo.awesome.decoration.DecorationActivity
import com.usher.demo.awesome.drag.DragActiity
import com.usher.demo.awesome.selection.SelectionActivity
import com.usher.demo.awesome.smarthome.SmartHomeActivity
import com.usher.demo.image.BlurActivity
import com.usher.demo.image.RoundImageActivity
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
import com.usher.demo.view.ChartActivity
import com.usher.demo.view.CircleWaveActivity
import com.usher.demo.view.MarqueeTextActivity
import com.usher.demo.view.WaveActivity
import com.usher.demo.view.loading.LoadingActivity
import com.usher.demo.view.looprecycler.LoopRecyclerActivity
import com.usher.demo.view.pager.PagerActivity
import com.usher.demo.web.AngularActivity
import com.usher.demo.web.three.ThreeActivity

object DemoConfig {
    private val configMap = hashMapOf<String, List<DemoItem>>()
    const val TAG_KEY = "TAG_KEY"
    private const val KEY_RX = "rx"
    private const val KEY_WEB = "web"
    private const val KEY_IMAGE = "image"
    private const val KEY_VIEW = "view"
    private const val KEY_MATERIAL = "material"
    private const val KEY_AWESOME = "awesome"
    private const val KEY_OTHER = "other"
    private const val KEY_KOTLIN = "kotlin"

    init {
        configMap["main"] = listOf(
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
                DemoItem("blur", BlurActivity::class.java),
                DemoItem("picasso transform", RoundImageActivity::class.java)
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
                DemoItem("wave", WaveActivity::class.java),
                DemoItem("pager", PagerActivity::class.java),
                DemoItem("marquee text", MarqueeTextActivity::class.java),
                DemoItem("loading", LoadingActivity::class.java),
                DemoItem("chart", ChartActivity::class.java),
                DemoItem("loop reycler", LoopRecyclerActivity::class.java),
                DemoItem("circle wave", CircleWaveActivity::class.java)
        )

        configMap[KEY_OTHER] = listOf(
                DemoItem("notification", NotificationActivity::class.java),
                DemoItem("launch mode", AActivity::class.java),
                DemoItem("log", LogActivity::class.java)
        )

        configMap[KEY_AWESOME] = listOf(
                DemoItem("selection", SelectionActivity::class.java),
                DemoItem("edit channel", ChannelActivity::class.java),
                DemoItem("recyclerView decoration", DecorationActivity::class.java),
                DemoItem("smart home", SmartHomeActivity::class.java),
                DemoItem("drag", DragActiity::class.java)
        )
    }

    fun getDemoConfig(key: String): List<DemoItem> = configMap[key] ?: listOf()
}

data class DemoItem(val desc: String, val key: String?, val cls: Class<*>) {
    constructor(desc: String, cls: Class<*>) : this(desc, null, cls)
}