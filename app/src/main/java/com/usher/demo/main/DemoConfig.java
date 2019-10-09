package com.usher.demo.main;

import com.usher.demo.awesome.channel.ChannelActivity;
import com.usher.demo.awesome.decoration.DecorationActivity;
import com.usher.demo.awesome.drag.DragActiity;
import com.usher.demo.awesome.selection.SelectionActivity;
import com.usher.demo.awesome.smarthome.SmartHomeActivity;
import com.usher.demo.image.BlurActivity;
import com.usher.demo.image.RoundImageActivity;
import com.usher.demo.kotlin.KotlinActivity;
import com.usher.demo.material.FitsSystemWindowActivity;
import com.usher.demo.material.ProfileActivity;
import com.usher.demo.material.TabLayoutActivity;
import com.usher.demo.material.home.HomeActivity;
import com.usher.demo.other.launchmode.AActivity;
import com.usher.demo.other.notification.NotificationActivity;
import com.usher.demo.rx.RxExitActivity;
import com.usher.demo.rx.RxSearchActivity;
import com.usher.demo.rx.RxSplashActivity;
import com.usher.demo.rx.RxSumActivity;
import com.usher.demo.view.ChartActivity;
import com.usher.demo.view.CircleWaveActivity;
import com.usher.demo.view.MarqueeTextActivity;
import com.usher.demo.view.WaveActivity;
import com.usher.demo.view.loading.LoadingActivity;
import com.usher.demo.view.looprecycler.LoopRecyclerActivity;
import com.usher.demo.view.pager.PagerActivity;
import com.usher.demo.web.AngularActivity;
import com.usher.demo.web.three.ThreeActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DemoConfig {
    private static final Map<String, List<DemoItem>> configMap = new HashMap<>();
    static final String TAG_KEY = "TAG_KEY";
    private static final String KEY_RX = "rx";
    private static final String KEY_WEB = "web";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_VIEW = "view";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_AWESOME = "awesome";
    private static final String KEY_OTHER = "other";
    private static final String KEY_KOTLIN = "kotlin";

    static {

        configMap.put("main", Arrays.asList(
                new DemoItem("rx", KEY_RX, DemoListActivity.class),
                new DemoItem("image", KEY_IMAGE, DemoListActivity.class),
                new DemoItem("material", KEY_MATERIAL, DemoListActivity.class),
                new DemoItem("view", KEY_VIEW, DemoListActivity.class),
                new DemoItem("web", KEY_WEB, DemoListActivity.class),
                new DemoItem("other", KEY_OTHER, DemoListActivity.class),
                new DemoItem("awesome", KEY_AWESOME, DemoListActivity.class),
                new DemoItem("kotlin", KEY_KOTLIN, KotlinActivity.class)
        ));

        configMap.put(KEY_RX, Arrays.asList(
                new DemoItem("splash", RxSplashActivity.class),
                new DemoItem("sum", RxSumActivity.class),
                new DemoItem("exit", RxExitActivity.class),
                new DemoItem("search", RxSearchActivity.class)
        ));

        configMap.put(KEY_IMAGE, Arrays.asList(
                new DemoItem("blur", BlurActivity.class),
                new DemoItem("picasso transform", RoundImageActivity.class)
        ));

        configMap.put(KEY_WEB, Arrays.asList(
                new DemoItem("three", ThreeActivity.class),
                new DemoItem("angular", AngularActivity.class)
        ));

        configMap.put(KEY_MATERIAL, Arrays.asList(
                new DemoItem("fitsSystemWindow", FitsSystemWindowActivity.class),
                new DemoItem("tab", TabLayoutActivity.class),
                new DemoItem("profile", ProfileActivity.class),
                new DemoItem("home", HomeActivity.class)

        ));

        configMap.put(KEY_VIEW, Arrays.asList(
                new DemoItem("wave", WaveActivity.class),
                new DemoItem("pager", PagerActivity.class),
                new DemoItem("marquee text", MarqueeTextActivity.class),
                new DemoItem("loading", LoadingActivity.class),
                new DemoItem("chart", ChartActivity.class),
                new DemoItem("loop reycler", LoopRecyclerActivity.class),
                new DemoItem("circle wave", CircleWaveActivity.class)
        ));

        configMap.put(KEY_OTHER, Arrays.asList(
                new DemoItem("notification", NotificationActivity.class),
                new DemoItem("launch mode", AActivity.class)
        ));

        configMap.put(KEY_AWESOME, Arrays.asList(
                new DemoItem("selection", SelectionActivity.class),
                new DemoItem("edit channel", ChannelActivity.class),
                new DemoItem("recyclerView decoration", DecorationActivity.class),
                new DemoItem("smart home", SmartHomeActivity.class),
                new DemoItem("drag", DragActiity.class)
        ));
    }

    static List<DemoItem> getDemoConfig(String key) {
        return configMap.get(key);
    }
}