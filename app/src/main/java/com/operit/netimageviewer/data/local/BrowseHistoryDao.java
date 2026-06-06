package com.operit.netimageviewer.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * 浏览历史 DAO
 */
@Dao
public interface BrowseHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BrowseHistoryEntity entity);

    @Query("SELECT * FROM browse_history ORDER BY browseTime DESC")
    List<BrowseHistoryEntity> getAll();

    @Query("SELECT * FROM browse_history WHERE chapterId = :chapterId ORDER BY browseTime DESC LIMIT 1")
    BrowseHistoryEntity getByChapterId(int chapterId);

    @Query("DELETE FROM browse_history WHERE chapterId = :chapterId")
    void deleteByChapterId(int chapterId);

    @Query("DELETE FROM browse_history")
    void deleteAll();

    @Query("DELETE FROM browse_history WHERE id IN (:ids)")
    void deleteByIds(List<Integer> ids);

    @Query("DELETE FROM browse_history WHERE id = :id")
    void deleteById(int id);
}