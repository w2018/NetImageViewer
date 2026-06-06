package com.operit.netimageviewer.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * 收藏夹 DAO
 */
@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteEntity entity);

    @Query("SELECT * FROM favorites ORDER BY favoriteTime DESC")
    List<FavoriteEntity> getAll();

    @Query("SELECT * FROM favorites WHERE chapterId = :chapterId LIMIT 1")
    FavoriteEntity getByChapterId(int chapterId);

    @Query("DELETE FROM favorites WHERE chapterId = :chapterId")
    void deleteByChapterId(int chapterId);

    @Query("DELETE FROM favorites")
    void deleteAll();
}