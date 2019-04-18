package com.usher.demo.awesome.smarthome.entities;

public class ADInfo {
    String adurl;
    String redirecturl;

    public ADInfo(String adurl, String redirecturl) {
        this.adurl = adurl;
        this.redirecturl = redirecturl;
    }

    public String getUrl() {
        return adurl;
    }
}
