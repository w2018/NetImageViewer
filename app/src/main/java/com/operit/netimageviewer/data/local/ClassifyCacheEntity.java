package com.operit.netimageviewer.data.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Room 实体：缓存分类数据
 */
@Entity(tableName = "classify_cache")
public class ClassifyCacheEntity {

    @PrimaryKey
    private int id;

    private String title;
    private String cover;

    public ClassifyCacheEntity() {}

    @Ignore
    public ClassifyCacheEntity(int id, String title, String cover) {
        this.id = id;
        this.title = title;
        this.cover = cover;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
}
