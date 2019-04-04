package com.usher.demo.rx;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;

import com.jakewharton.rxbinding3.widget.RxTextView;
import com.usher.demo.R;
import com.usher.demo.utils.RxUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class RxSearchActivity extends AppCompatActivity {
    private final List<String> mSearchResults = new ArrayList<>();
    private final List<String> mCities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rx_search);

        initData();
        initView();
    }

    private void initData() {
        mCities.addAll(Arrays.asList("北京市", "上海市", "天津市", "重庆市", "沈阳市", "台北市"));
    }

    private void initView() {
        RxSearchAdapter adapter = new RxSearchAdapter(this, mSearchResults);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        EditText input = findViewById(R.id.edittext);
        RxTextView.textChanges(input)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(v -> v.toString().trim())
                .distinctUntilChanged()
                .switchMap(v -> Observable.fromIterable(mCities)
                        .filter(city -> city.contains(v))
                        .collect((Callable<ArrayList<String>>) ArrayList::new, ArrayList::add)
                        .toObservable()
                )
                .compose(RxUtil.getSchedulerComposer())
                .as(RxUtil.autoDispose(this))
                .subscribe(v -> {
                    mSearchResults.clear();
                    mSearchResults.addAll(v);
                    adapter.notifyDataSetChanged();
                });
    }
}
