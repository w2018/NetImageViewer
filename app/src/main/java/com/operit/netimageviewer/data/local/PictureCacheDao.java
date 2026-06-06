package com.operit.netimageviewer.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PictureCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PictureCacheEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PictureCacheEntity entity);

    @Query("SELECT * FROM picture_cache WHERE chapterId = :chapterId ORDER BY orderIndex ASC")
    List<PictureCacheEntity> getByChapterId(int chapterId);

    @Query("DELETE FROM picture_cache WHERE chapterId = :chapterId")
    void deleteByChapterId(int chapterId);

    @Query("DELETE FROM picture_cache")
    void deleteAll();
}
