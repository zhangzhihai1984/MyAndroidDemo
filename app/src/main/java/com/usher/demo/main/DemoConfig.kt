package com.usher.demo.main

import com.usher.demo.awesome.LoopHintActivity
import com.usher.demo.awesome.ZipCodeActivity
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
                DemoItem(KEY_RX, DemoListActivity::class.java, "RX"),
                DemoItem(KEY_DEMONSTRATION, DemoListActivity::class.java, "Demonstration"),
                DemoItem(KEY_MATERIAL, DemoListActivity::class.java, "Material"),
                DemoItem(KEY_VIEW, DemoListActivity::class.java, "View"),
                DemoItem(KEY_WEB, DemoListActivity::class.java, "Web"),
                DemoItem(KEY_OTHER, DemoListActivity::class.java, "Other"),
                DemoItem(KEY_AWESOME, DemoListActivity::class.java, "Awesome")
        )

        configMap[KEY_RX] = listOf(
                DemoItem(RxSplashActivity::class.java, "Splash"),
                DemoItem(RxSumActivity::class.java, "Sum"),
                DemoItem(RxExitActivity::class.java, "Exit"),
                DemoItem(RxSearchActivity::class.java, "Search")
        )

        configMap[KEY_DEMONSTRATION] = listOf(
                DemoItem(ImageScaleTypeActivity::class.java, "ScaleType"),
                DemoItem(BitmapXferActivity::class.java, "Bitmap Xfer"),
                DemoItem(BitmapBlurActivity::class.java, "Bitmap Blur", "RenderScript"),
                DemoItem(PorterDuffColorFilterActivity::class.java, "PorterDuff", "ColorFilter"),
                DemoItem(LightingColorFilterActivity::class.java, "Lighting", "ColorFilter"),
                DemoItem(PorterDuffXfermodeActivity::class.java, "PorterDuff", "Xfermode"),
                DemoItem(BitmapMeshWarpActivity::class.java, "Warp", "Bitmap Mesh"),
                DemoItem(BitmapMeshRippleActivity::class.java, "Ripple", "Bitmap Mesh"),
                DemoItem(BitmapMeshCurtainActivity::class.java, "Curtain", "Bitmap Mesh"),
                DemoItem(ViewPager2Activity::class.java, "ViewPager2"),
                DemoItem(TabLayout1Activity::class.java, "TabLayout", "ViewPager"),
                DemoItem(TabLayout2Activity::class.java, "TabLayout", "ViewPager2")
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
                DemoItem(MarqueeTextActivity::class.java, "Marquee Text"),
                DemoItem(WaveActivity::class.java, "Wave"),
                DemoItem(LoopPagerActivity::class.java, "Loop Pager"),
                DemoItem(LoopPager2Activity::class.java, "Loop Pager2"),
                DemoItem(SeatSelectionActivity::class.java, "Seat Selection"),
                DemoItem(SeatSelection2Activity::class.java, "Seat Selection2"),
                DemoItem(LoadingActivity::class.java, "Loading"),
                DemoItem(ChartActivity::class.java, "Chart"),
                DemoItem(LoopRecyclerActivity::class.java, "Loop Recycler"),
                DemoItem(ScanActivity::class.java, "Scan"),
                DemoItem(IndexActivity::class.java, "Index"),
                DemoItem(ColorPickerActivity::class.java, "Color Picker"),
                DemoItem(ColorSeekerActivity::class.java, "Color Seeker"),
                DemoItem(CurtainActivity::class.java, "Curtain"),
                DemoItem(GraffitiActivity::class.java, "Graffiti")
        )

        configMap[KEY_OTHER] = listOf(
                DemoItem(NotificationActivity::class.java, "notification"),
                DemoItem(AActivity::class.java, "launch mode"),
                DemoItem(LogActivity::class.java, "log")
        )

        configMap[KEY_AWESOME] = listOf(
                DemoItem(ChannelEditActivity::class.java, "Channel Edit"),
                DemoItem(StickyHeaderActivity::class.java, "Sticky Header"),
                DemoItem(ContactsActivity::class.java, "Contacts"),
                DemoItem(LoopHintActivity::class.java, "Loop Hint"),
                DemoItem(ZipCodeActivity::class.java, "ZipCode Input"),
                DemoItem(DragActiity::class.java, "Drag")
        )
    }

    fun getDemoConfig(key: String?): List<DemoItem> = configMap[key ?: KEY_MAIN] ?: listOf()
}

data class DemoItem(val key: String?, val cls: Class<*>, val title: String, val desc: String?) {
    constructor(key: String, cls: Class<*>, title: String) : this(key, cls, title, null)
    constructor(cls: Class<*>, title: String) : this(null, cls, title, null)
    constructor(cls: Class<*>, title: String, desc: String) : this(null, cls, title, desc)
}