package com.operit.netimageviewer.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClassifyCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ClassifyCacheEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClassifyCacheEntity entity);

    @Query("SELECT * FROM classify_cache")
    List<ClassifyCacheEntity> getAll();

    @Query("DELETE FROM classify_cache")
    void deleteAll();
}
