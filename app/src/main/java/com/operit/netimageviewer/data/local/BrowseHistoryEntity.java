package com.operit.netimageviewer.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 浏览历史记录实体
 */
@Entity(tableName = "browse_history",
        indices = {@Index(value = "chapterId", unique = true)})
public class BrowseHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int chapterId;
    private String chapterTitle;
    private String coverUrl;
    private long browseTime; // 浏览时间戳（毫秒）

    public BrowseHistoryEntity(int chapterId, String chapterTitle, String coverUrl, long browseTime) {
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.coverUrl = coverUrl;
        this.browseTime = browseTime;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public long getBrowseTime() { return browseTime; }
    public void setBrowseTime(long browseTime) { this.browseTime = browseTime; }
}
