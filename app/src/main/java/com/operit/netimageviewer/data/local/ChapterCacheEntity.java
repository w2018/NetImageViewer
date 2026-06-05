package com.operit.netimageviewer.data.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Room 实体：缓存章节数据
 */
@Entity(tableName = "chapter_cache")
public class ChapterCacheEntity {

    @PrimaryKey
    private int id;

    private int classifyId;
    private String title;
    private String cover;
    private int pageCount;

    public ChapterCacheEntity() {}

    @Ignore
    public ChapterCacheEntity(int id, int classifyId, String title, String cover, int pageCount) {
        this.id = id;
        this.classifyId = classifyId;
        this.title = title;
        this.cover = cover;
        this.pageCount = pageCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClassifyId() { return classifyId; }
    public void setClassifyId(int classifyId) { this.classifyId = classifyId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
}
