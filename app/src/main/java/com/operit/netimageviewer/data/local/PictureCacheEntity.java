package com.operit.netimageviewer.data.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Room 实体：缓存图片数据
 */
@Entity(tableName = "picture_cache")
public class PictureCacheEntity {

    @PrimaryKey
    private int id;

    private int chapterId;
    private String url;
    private int orderIndex;

    public PictureCacheEntity() {}

    @Ignore
    public PictureCacheEntity(int id, int chapterId, String url, int orderIndex) {
        this.id = id;
        this.chapterId = chapterId;
        this.url = url;
        this.orderIndex = orderIndex;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
