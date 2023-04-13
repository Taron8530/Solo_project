package com.example.solo_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kakao_pay_item_model {
    @Expose
    @SerializedName("tid")
    private String tid;
    @Expose
    @SerializedName("next_redirect_mobile_url")
    private String url;

    public String getTid() {
        return tid;
    }

    public String getUrl() {
        return url;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
