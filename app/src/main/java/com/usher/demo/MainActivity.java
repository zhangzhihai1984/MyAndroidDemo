package com.usher.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usher.demo.angular.AngularActivity;
import com.usher.demo.decoration.DecorationActivity;
import com.usher.demo.decoration.DecorationAdapter;
import com.usher.demo.decoration.ItemInfo;
import com.usher.demo.drag.ChannelActivity;
import com.usher.demo.image.BlurActivity;
import com.usher.demo.image.RoundImageActivity;
import com.usher.demo.launchmode.AActivity;
import com.usher.demo.material.FitsSystemWindowActivity;
import com.usher.demo.material.ProfileActivity;
import com.usher.demo.material.TabLayoutActivity;
import com.usher.demo.material.home.HomeActivity;
import com.usher.demo.notification.NotificationActivity;
import com.usher.demo.rx.SplashActivity;
import com.usher.demo.selection.SelectionActivity;
import com.usher.demo.text.MarqueeTextActivity;
import com.usher.demo.wave.WaveActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {
    private static final String TAG = "zzh";
    private Context mContext;

    private TestDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("zzh", "MainActivity onCreate");

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            /*Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (null != vibrator) {
                long[] pattern = {1000, 500, 1000, 500};
                vibrator.vibrate(pattern, 0);
            }*/

//                startActivity(new Intent(mContext, LoadingActivity.class));
//                startActivity(new Intent(mContext, ThreeActivity.class));
//                startActivity(new Intent(mContext, RxActivity.class));
//            startActivity(new Intent(mContext, PagerActivity.class));
            startActivity(new Intent(mContext, TabLayoutActivity.class));
        });

        mDialog = new TestDialog(this);

        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);

//        lp.y = 100;
//        window.setAttributes(lp);

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.dimAmount = 0.6f;
//        lp.alpha = 0.8f;

//        testSignature();

//        addLanguage();

        rxTest();

        testStrFormat();

        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("zzh", "MainActivity onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("zzh", "MainActivity onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("zzh", "MainActivity onDestroy");
    }

    private void initView() {
        View contextMenuButton = findViewById(R.id.contextmenu_button);
        contextMenuButton.setTag(R.id.context_menu_1, "Menu1");
        contextMenuButton.setTag(R.id.context_menu_2, "Menu2");
        contextMenuButton.setOnCreateContextMenuListener(this);

        findViewById(R.id.decoration_button).setOnClickListener(v -> startActivity(new Intent(mContext, DecorationActivity.class)));

        View popupButton = findViewById(R.id.popup_button);
        popupButton.setOnClickListener(v -> {

            List<ItemInfo> mList = new ArrayList<>();

            List<List<String>> lists = new ArrayList<>();
            lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list1)));
//                lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list2)));
//                lists.add(Arrays.asList(getResources().getStringArray(R.array.sticky_list3)));

            for (int i = 0; i < lists.size(); i++) {
                for (String content : lists.get(i)) {
                    mList.add(new ItemInfo(String.valueOf(i), "GROUP " + i, content));
                }
            }

            setBackgroundAlpha(0.4f);
            TestPopupWindow popupWindow = new TestPopupWindow(mContext);
            popupWindow.setOnDismissListener(() -> setBackgroundAlpha(1.0f));
            popupWindow.setData(mList);
//                popupWindow.showAtLocation(findViewById(R.id.main_root_layout), Gravity.CENTER, 0, 0);
            popupWindow.showAsDropDown(popupButton, popupButton.getWidth(), -getResources().getDimensionPixelSize(R.dimen.popup_height) / 2);
            popupWindow.update();
        });

        findViewById(R.id.selection_button).setOnClickListener(v -> startActivity(new Intent(mContext, SelectionActivity.class)));
        findViewById(R.id.wave_button).setOnClickListener(v -> startActivity(new Intent(mContext, WaveActivity.class)));
        findViewById(R.id.intent_button).setOnClickListener(v -> startActivity(new Intent(mContext, AActivity.class)));
        findViewById(R.id.notification_button).setOnClickListener(v -> startActivity(new Intent(mContext, NotificationActivity.class)));
        findViewById(R.id.round_image_button).setOnClickListener(v -> startActivity(new Intent(mContext, RoundImageActivity.class)));
        findViewById(R.id.marquee_textview_button).setOnClickListener(v -> startActivity(new Intent(mContext, MarqueeTextActivity.class)));
        findViewById(R.id.splash_button).setOnClickListener(v -> startActivity(new Intent(mContext, SplashActivity.class)));
        findViewById(R.id.fitssystemwindow_button).setOnClickListener(v -> startActivity(new Intent(mContext, FitsSystemWindowActivity.class)));
        findViewById(R.id.blur_button).setOnClickListener(v -> startActivity(new Intent(mContext, BlurActivity.class)));
        findViewById(R.id.profile_button).setOnClickListener(v -> startActivity(new Intent(mContext, ProfileActivity.class)));
        findViewById(R.id.home_button).setOnClickListener(v -> startActivity(new Intent(mContext, HomeActivity.class)));
        findViewById(R.id.angular_button).setOnClickListener(v -> startActivity(new Intent(mContext, AngularActivity.class)));

        findViewById(R.id.imageview).setOnClickListener(v -> new AppInfoDialog(mContext).show());
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;

        if (alpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        getWindow().setAttributes(lp);
    }

    private void testSignature() {
        String timestamp = "2018-05-15 17:50:00";
        String appKey = "G7Q7ZT3HW6HWPQBEUTG5B6GK2XWZUKAJ";

        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);

            Signature[] signatures = packageInfo.signatures;
            ByteArrayInputStream stream = new ByteArrayInputStream(signatures[0].toByteArray());
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(stream);

            StringBuilder builder = new StringBuilder();
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            for (byte b : digest.digest(certificate.getEncoded())) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
//            String fingerprint = builder.toString().toUpperCase();
            String fingerprint = "C7DAB911032E9E6CD2FBAB01F324A9B37D452F8B";
            Log.i("zzh", "SHA-1 : FingerPrint: " + fingerprint);

            StringBuilder builder1 = new StringBuilder();
            MessageDigest digest1 = MessageDigest.getInstance("MD5");
            String input = fingerprint + timestamp + appKey + timestamp;
            for (byte b : digest1.digest(input.getBytes("utf-8"))) {
                builder1.append(Integer.toHexString((b >> 4) & 0xf));
                builder1.append(Integer.toHexString(b & 0xf));
            }

            String signature = builder1.toString().toUpperCase();
            Log.i("zzh", "Signature: " + signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLanguage() {

        //00000000DEAE60AC47877F09C93ACE05
        /*Map<String, String> map = new HashMap<>();
        map.put("a", "aa");
        map.put("b", "bb");
        map.put("c", "cc");
        Log.i("zzh", "map: " + map.toString());*/

//        String jsonStr = "{\"languages\":[{\"zh\":[{\"com.joylink.curtain\":\"窗帘\"},{\"getAllProperties\":\"得到所有属性值\"},{\"allProperties\":\"所有属性值\"},{\"version\":\"版本\"},{\"range\":[{\"on\":\"打开\"},{\"off\":\"关闭\"}],\"switch\":\"开关\"},{\"percentage\":\"打开百分比\"},{\"status\":\"状态\"}],\"service\":\"com.joylink.curtain\",\"version\":\"1.0.0\"}],\"version\":\"3.0\"}";
        String jsonStr = "{\"languages\":[{\"zh\":[{\"com.joylink.panel_switch\":\"墙面开关\"},{\"getAllProperties\":\"得到所有属性值\"},{\"allProperties\":\"所有属性值\"},{\"index\":\"开关编号\"},{\"version\":\"版本\"},{\"range\":[{\"on\":\"打开\"},{\"off\":\"关闭\"}],\"switch_multi\":\"多开关操作\"},{\"switch_number\":\"开关总数\"},{\"status\":\"状态\"}],\"service\":\"com.joylink.panel_switch\",\"version\":\"1.0.2\"}],\"version\":\"3.0\"}";
        try {
            JSONArray languages = new JSONObject(jsonStr).getJSONArray("languages");

            String zh = languages.getString(0);
            Log.i("zzh", zh);
            Map<String, Object> zhMap = parseJSON2Map(zh);

            for (String k : zhMap.keySet()) {
                Log.i("zzh", k + ": " + zhMap.get(k).toString());

            }

        } catch (JSONException e) {
            Log.i("zzh", e.toString());
            e.printStackTrace();
        }
    }

    private Map<String, Object> parseJSON2Map(String jsonStr) {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        Map<String, Object> map = gson.fromJson(jsonStr, type);

        return flatMap(map);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> flatMap(Map<String, Object> map) {
        Map<String, Object> finalMap = new HashMap<>();

        for (String k : map.keySet()) {
            Object v = map.get(k);
            Log.i("zzh", "v: " + v.toString());

            if (v instanceof List) {
                for (Object obj : (List) v) {
                    finalMap.putAll(flatMap((Map<String, Object>) obj));
                }
            } else {
                finalMap.put(k, v);
            }
        }

        return finalMap;
    }

    private void testStrFormat() {
        Log.i("zzh", String.format("a%07d", 12));
    }

    String str = null;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, Menu.NONE, "hello world").setActionView(v);
        menu.add(0, 1, Menu.NONE, getString(R.string.my_family_member_list_main_account_name, str)).setActionView(v);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(this, (String) item.getActionView().getTag(R.id.context_menu_1), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, (String) item.getActionView().getTag(R.id.context_menu_2), Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            mDialog.show();
//            startActivity(new Intent(this, ScrollingActivity.class));
//            startActivity(new Intent(this, ListActivity.class));
            startActivity(new Intent(this, ChannelActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AppInfoDialog extends Dialog {

        AppInfoDialog(@NonNull Context context) {
            super(context, R.style.FramelessDialog);

            setContentView(R.layout.app_info_layout);

            String appName = null;

            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                appName = getPackageManager().getApplicationLabel(packageInfo.applicationInfo).toString();


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            ((TextView) findViewById(R.id.app_id_textview)).setText(getString(R.string.application_id, BuildConfig.APPLICATION_ID));
            ((TextView) findViewById(R.id.app_name_textview)).setText(getString(R.string.application_name, appName));
            ((TextView) findViewById(R.id.app_version_textview)).setText(getString(R.string.application_version, BuildConfig.VERSION_NAME));
            ((TextView) findViewById(R.id.app_flavor_textview)).setText(getString(R.string.application_flavor, BuildConfig.FLAVOR));
            ((TextView) findViewById(R.id.app_api_textview)).setText(getString(R.string.application_api_host, BuildConfig.API_HOST));
        }
    }

    class TestDialog extends Dialog {
        TestDialog(@NonNull Context context) {
            super(context, R.style.FramelessDialog);

            setContentView(R.layout.game_no_energy_dialog_layout);

            setCanceledOnTouchOutside(false);

            Window window = getWindow();

            if (null != window) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            }


            this.findViewById(R.id.got_it_textview).setOnClickListener(v -> cancel());
        }
    }

    class TestPopupWindow extends PopupWindow {
        private final List<ItemInfo> mList = new ArrayList<>();
        private RecyclerView mRecyclerView;
        private DecorationAdapter mAdapter;

        TestPopupWindow(Context context) {
            super(context);

            initView();
        }

        private void initView() {
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            setFocusable(true);
            setOutsideTouchable(true);
            setTouchable(true);
            setBackgroundDrawable(new PaintDrawable(Color.parseColor("#00000000")));
            setAnimationStyle(R.style.PopupAnimation);
            setClippingEnabled(true);

            View view = LayoutInflater.from(mContext).inflate(R.layout.popup_layout, null);
            setContentView(view);

            mAdapter = new DecorationAdapter(mContext, mList);

            mRecyclerView = view.findViewById(R.id.recyclerview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(mAdapter);
        }

        void setData(List<ItemInfo> list) {
            mList.clear();
            mList.addAll(list);

            mAdapter.notifyDataSetChanged();
        }
    }

    private void rxTestNonLambda() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void rxTest() {
        Disposable d = Observable.range(1, 3)
                .switchMap(v -> Observable.range(v, 2))
                .subscribe(v -> Log.i("zzh", "range: " + v));

        /*observable.create((observableonsubscribe<integer>) emitter -> {
            log.i("zzh", "observable thread " + thread.currentthread().getname());
            log.i("zzh", "emit 1");
            emitter.onnext(1);
            log.i("zzh", "emit 2");
            emitter.onnext(2);
            log.i("zzh", "emit 3");
            emitter.onnext(3);
            emitter.oncomplete();
        }).subscribeon(schedulers.newthread())
                .observeon(androidschedulers.mainthread())
                .map(integer -> integer * 2)
                .doonnext(integer -> log.i("zzh", "doonnext " + integer))
                .subscribe(new observer<integer>() {
                    private disposable disposable;
                    private int i;

                    @override
                    public void onsubscribe(disposable d) {
                        log.i("zzh", "observer thread " + thread.currentthread().getname());
                        log.i("zzh", "subscribe");
                        disposable = d;
                    }

                    @override
                    public void onnext(integer value) {
                        log.i("zzh", "onnext: " + value);

//                i++;

                        if (i == 2) {
                            disposable.dispose();

                            log.i("zzh", "dispose " + disposable.isdisposed());
                        }
                    }

                    @override
                    public void onerror(throwable e) {
                        log.i("zzh", "error");
                    }

                    @override
                    public void oncomplete() {
                        log.i("zzh", "complete");
                    }
                });*/

        /*Disposable d = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        }).flatMap(v -> {
            final List<String> list = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                list.add("I'm " + v);
            }

            return Observable.fromIterable(list).delay(1000, TimeUnit.MILLISECONDS);
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {

                }, error -> {

                });*/
                /*.subscribe(new Observer<String>() {
                    int i = 0;
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        i++;
                        Log.i("zzh", s + " " + i);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("zzh", "error");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zzh", "complete " + i);
                    }
                });*/

        /*Observable.range(1, 5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(v -> Log.i("zzh", "doOnNext1: " + v))
                .map(v -> v * 2)
                .doOnNext(v -> Log.i("zzh", "doOnNext2: " + v))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i("zzh", "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i("zzh", "onComplete");
                    }
                });*/

        //Zip
        /*Observable<Integer> observable1 = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            Log.i(TAG, "emit 1");
            emitter.onNext(1);
            Thread.sleep(1000);

            Log.i(TAG, "emit 2");
            emitter.onNext(2);
            Thread.sleep(1000);

            Log.i(TAG, "emit 3");
            emitter.onNext(3);
            Thread.sleep(1000);

            Log.i(TAG, "emit 4");
            emitter.onNext(4);
            Thread.sleep(5000);

            Log.i(TAG, "emit complete1");
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            Log.i(TAG, "emit A");
            emitter.onNext("A");
            Thread.sleep(1000);

            Log.i(TAG, "emit B");
            emitter.onNext("B");
            Thread.sleep(1000);

            Log.i(TAG, "emit C");
            emitter.onNext("C");
            Thread.sleep(3000);

            Log.i(TAG, "emit complete2");
//            emitter.onComplete();
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, (v1, v2) -> v1 + v2)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });*/

        //Flowable
        /*Flowable.create((FlowableOnSubscribe<Integer>) emitter -> {
            Log.i(TAG, "Before: " + emitter.requested());

            Log.i(TAG, "emit 1");
            emitter.onNext(1);
            Log.i(TAG, "after 1: " + emitter.requested());

            Log.i(TAG, "emit 2");
            emitter.onNext(2);
            Log.i(TAG, "after 2: " + emitter.requested());

            Log.i(TAG, "emit 3");
            emitter.onNext(3);
            Log.i(TAG, "after 3: " + emitter.requested());

        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.i(TAG, "onSubscribe");
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });*/

        /*Observable.just("hello")
                .flatMap(v -> {
                    if (v.equals("hell")) {
                        return Observable.just("world");
                    }
                    throw new Exception("NULL!!!!!!!");
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i + 1);
        }
        Observable.fromIterable(list)
                .map(v -> {
                    if (v == 2) {
                        throw new Exception("HAHAHAH");
                    } else {
                        return v;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        Log.i(TAG, "onNext: " + i);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });*/

        /*Observable.just("A", "B", "C", "D")
                .compose(getTransformer())
                .zipWith(Observable.just(1, 2, 3, 4), (v1, v2) -> v1 + v2)
                .zipWith(Observable.interval(1, TimeUnit.SECONDS).take(3), (v1, v2) -> v1)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String v) {
                        Log.i(TAG, "onNext: " + v);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });*/

        /*Observable.defer((Callable<ObservableSource<Integer>>) () -> Observable.just(1, 2, 3))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/

        /*Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            Thread.sleep(400);

            emitter.onNext(2);
            Thread.sleep(555);

            emitter.onNext(3);
            Thread.sleep(100);

            emitter.onNext(4);
            Thread.sleep(600);

            emitter.onNext(5);
            emitter.onComplete();
        })
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer v) {
                        Log.i(TAG, "onNext: " + v);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    private ObservableTransformer<String, String> getTransformer() {
        return upstream -> upstream.map(v -> v + "+");
    }

    private ObservableTransformer threadTransformer() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
