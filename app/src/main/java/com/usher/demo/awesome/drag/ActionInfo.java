package com.usher.demo.awesome.drag;

public class ActionInfo {
    public String name;
    public int delay;

    public ActionInfo(String name) {
        this.name = name;
        this.delay = 0;
    }

    public ActionInfo(int delay) {
        this.delay = delay;
    }
}
