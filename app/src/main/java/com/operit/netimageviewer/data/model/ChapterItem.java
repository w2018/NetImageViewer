package com.operit.netimageviewer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 二级目录：章节
 * API: /chapterList?classifyId=
 */
public class ChapterItem {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("cover")
    private String cover;

    @SerializedName("pageCount")
    private int pageCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
}
