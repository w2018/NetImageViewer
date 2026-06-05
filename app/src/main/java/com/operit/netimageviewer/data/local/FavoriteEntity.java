package com.operit.netimageviewer.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 收藏夹实体
 */
@Entity(tableName = "favorites")
public class FavoriteEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int chapterId;
    private String chapterTitle;
    private String coverUrl;
    private long favoriteTime; // 收藏时间戳（毫秒）
    private String note;       // 备注（可选）

    public FavoriteEntity(int chapterId, String chapterTitle, String coverUrl, long favoriteTime) {
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.coverUrl = coverUrl;
        this.favoriteTime = favoriteTime;
        this.note = "";
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

    public long getFavoriteTime() { return favoriteTime; }
    public void setFavoriteTime(long favoriteTime) { this.favoriteTime = favoriteTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}