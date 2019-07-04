package com.usher.demo.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usher.demo.R;
import com.usher.demo.base.BaseActivity;

public class DemoListActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_list);

        initView();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null != wifiManager) {
            Log.i("zzh", "id1: " + wifiManager.getConnectionInfo().getSSID());
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            Log.i("zzh", "id2: " + connectivityManager.getActiveNetworkInfo().getExtraInfo());
        }

//        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void initView() {
        String tag = getIntent().getStringExtra(DemoConfig.TAG_KEY);
        if (TextUtils.isEmpty(tag))
            tag = "main";

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new DemoListAdapter(this, DemoConfig.getDemoConfig(tag)));
    }
}