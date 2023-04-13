package com.example.solo_project;

import android.net.Uri;

public class chat_item {
    private String content;
    private String name;
    private String time;
    private int viewType;

    public chat_item(String content, String name, String time, int viewType) {
        this.content = content;
        this.name = name;
        this.time = time;
        this.viewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getViewType() {
        return viewType;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
