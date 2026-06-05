package com.operit.netimageviewer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 三级目录：图片
 * API: /pictureList?chapterId=
 */
public class PictureItem {
    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("order")
    private int order;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
