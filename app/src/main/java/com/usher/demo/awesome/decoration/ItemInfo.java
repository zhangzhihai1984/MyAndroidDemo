package com.usher.demo.awesome.decoration;

public class ItemInfo {
    private String groupId;
    private String groupTitle;
    private String content;

    public ItemInfo(String groupId, String groupTitle, String content) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getContent() {
        return content;
    }
}
