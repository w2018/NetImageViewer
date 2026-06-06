package com.operit.netimageviewer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 一级目录：分类
 * API: /classifyList
 */
public class ClassifyItem {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("cover")
    private String cover;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
}
