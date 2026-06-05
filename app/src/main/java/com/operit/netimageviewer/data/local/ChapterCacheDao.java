package com.operit.netimageviewer.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChapterCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChapterCacheEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChapterCacheEntity entity);

    @Query("SELECT * FROM chapter_cache WHERE classifyId = :classifyId")
    List<ChapterCacheEntity> getByClassifyId(int classifyId);

    @Query("DELETE FROM chapter_cache WHERE classifyId = :classifyId")
    void deleteByClassifyId(int classifyId);

    @Query("DELETE FROM chapter_cache")
    void deleteAll();
}
