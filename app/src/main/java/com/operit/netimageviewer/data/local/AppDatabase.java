package com.operit.netimageviewer.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {
        ClassifyCacheEntity.class,
        ChapterCacheEntity.class,
        PictureCacheEntity.class,
        BrowseHistoryEntity.class,
        FavoriteEntity.class
    },
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ClassifyCacheDao classifyCacheDao();
    public abstract ChapterCacheDao chapterCacheDao();
    public abstract PictureCacheDao pictureCacheDao();
    public abstract BrowseHistoryDao browseHistoryDao();
    public abstract FavoriteDao favoriteDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "net_image_viewer_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
