package com.usher.demo.awesome.selection;

public class SelectionInfo {
    public enum Status {
        DISABLED,
        DEFAULT,
        SELECTED
    }

    private Status status;
    private int spanSize;

    public SelectionInfo(Status status) {
        this.status = status;
        this.spanSize = 1;
    }

    public SelectionInfo(Status status, int spanSize) {
        this.status = status;
        this.spanSize = spanSize;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }
}
