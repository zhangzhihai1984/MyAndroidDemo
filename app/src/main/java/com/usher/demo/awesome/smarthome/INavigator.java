package com.usher.demo.awesome.smarthome;

import io.reactivex.Observable;

public interface INavigator {
    void clear();

    void updateTabs(String tag);

    Observable<String> onNavigated();
}
