package com.usher.demo.main;

class DemoItem {
    String desc;
    String key;
    Class aClass;

    DemoItem(String desc, Class aClass) {
        this(desc, null, aClass);
    }

    DemoItem(String desc, String key, Class aClass) {
        this.desc = desc;
        this.key = key;
        this.aClass = aClass;
    }
}
