package com.operit.netimageviewer.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.operit.netimageviewer.data.local.AppDatabase;
import com.operit.netimageviewer.data.local.ChapterCacheEntity;
import com.operit.netimageviewer.data.local.ClassifyCacheEntity;
import com.operit.netimageviewer.data.local.PictureCacheEntity;
import com.operit.netimageviewer.data.model.ApiResponse;
import com.operit.netimageviewer.data.model.ChapterItem;
import com.operit.netimageviewer.data.model.ClassifyItem;
import com.operit.netimageviewer.data.model.PictureItem;
import com.operit.netimageviewer.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 数据仓库：统一管理网络请求和本地缓存
 * 策略：先返回本地缓存，再请求网络更新
 */
public class ImageRepository {

    private final AppDatabase database;
    private final ExecutorService executor;
    private final Context appContext;
    private final MutableLiveData<String> networkError = new MutableLiveData<>();

    public ImageRepository(Context context) {
        database = AppDatabase.getInstance(context);
        executor = Executors.newFixedThreadPool(4);
        appContext = context.getApplicationContext();
    }

    /**
     * 获取网络错误状态（生命周期感知）
     * 每次 onFailure 时会 post 错误描述，Activity 可据此弹出提示
     */
    public LiveData<String> getNetworkError() {
        return networkError;
    }

    // ==================== 分类列表 ====================

    /**
     * 获取分类列表：先返回缓存，异步请求网络更新
     */
    public LiveData<List<ClassifyItem>> getClassifyList() {
        MutableLiveData<List<ClassifyItem>> liveData = new MutableLiveData<>();

        // 1. 先从 Room 加载缓存
        executor.execute(() -> {
            List<ClassifyCacheEntity> cached = database.classifyCacheDao().getAll();
            if (cached != null && !cached.isEmpty()) {
                List<ClassifyItem> list = new ArrayList<>();
                for (ClassifyCacheEntity e : cached) {
                    ClassifyItem item = new ClassifyItem();
                    item.setId(e.getId());
                    item.setTitle(e.getTitle());
                    item.setCover(e.getCover());
                    list.add(item);
                }
                liveData.postValue(list);
            }
        });

        // 2. 异步请求网络
        RetrofitClient.getInstance(appContext).getApiService().getClassifyList("classifyList")
                .enqueue(new Callback<ApiResponse<List<ClassifyItem>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<ClassifyItem>>> call,
                                           @NonNull Response<ApiResponse<List<ClassifyItem>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<ClassifyItem> data = response.body().getData();
                            if (data != null) {
                                liveData.postValue(data);
                                // 缓存到 Room
                                cacheClassifyList(data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<ClassifyItem>>> call,
                                          @NonNull Throwable t) {
                        String msg = getErrorMessage(t);
                        networkError.postValue("加载分类失败：" + msg);
                    }
                });

        return liveData;
    }

    private void cacheClassifyList(List<ClassifyItem> list) {
        executor.execute(() -> {
            List<ClassifyCacheEntity> entities = new ArrayList<>();
            for (ClassifyItem item : list) {
                entities.add(new ClassifyCacheEntity(item.getId(), item.getTitle(), item.getCover()));
            }
            database.classifyCacheDao().deleteAll();
            database.classifyCacheDao().insertAll(entities);
        });
    }

    // ==================== 章节列表 ====================

    public LiveData<List<ChapterItem>> getChapterList(int classifyId) {
        MutableLiveData<List<ChapterItem>> liveData = new MutableLiveData<>();

        executor.execute(() -> {
            List<ChapterCacheEntity> cached = database.chapterCacheDao().getByClassifyId(classifyId);
            if (cached != null && !cached.isEmpty()) {
                List<ChapterItem> list = new ArrayList<>();
                for (ChapterCacheEntity e : cached) {
                    ChapterItem item = new ChapterItem();
                    item.setId(e.getId());
                    item.setTitle(e.getTitle());
                    item.setCover(e.getCover());
                    item.setPageCount(e.getPageCount());
                    list.add(item);
                }
                liveData.postValue(list);
            }
        });

        RetrofitClient.getInstance(appContext).getApiService().getChapterList("chapterList", classifyId)
                .enqueue(new Callback<ApiResponse<List<ChapterItem>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<ChapterItem>>> call,
                                           @NonNull Response<ApiResponse<List<ChapterItem>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<ChapterItem> data = response.body().getData();
                            if (data != null) {
                                liveData.postValue(data);
                                cacheChapterList(classifyId, data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<ChapterItem>>> call,
                                          @NonNull Throwable t) {
                        String msg = getErrorMessage(t);
                        networkError.postValue("加载章节失败：" + msg);
                    }
                });

        return liveData;
    }

    private void cacheChapterList(int classifyId, List<ChapterItem> list) {
        executor.execute(() -> {
            List<ChapterCacheEntity> entities = new ArrayList<>();
            for (ChapterItem item : list) {
                entities.add(new ChapterCacheEntity(
                        item.getId(), classifyId, item.getTitle(), item.getCover(), item.getPageCount()));
            }
            database.chapterCacheDao().deleteByClassifyId(classifyId);
            database.chapterCacheDao().insertAll(entities);
        });
    }

    // ==================== 图片列表 ====================

    public LiveData<List<PictureItem>> getPictureList(int chapterId) {
        MutableLiveData<List<PictureItem>> liveData = new MutableLiveData<>();

        executor.execute(() -> {
            List<PictureCacheEntity> cached = database.pictureCacheDao().getByChapterId(chapterId);
            if (cached != null && !cached.isEmpty()) {
                List<PictureItem> list = new ArrayList<>();
                for (PictureCacheEntity e : cached) {
                    PictureItem item = new PictureItem();
                    item.setId(e.getId());
                    item.setUrl(e.getUrl());
                    item.setOrder(e.getOrderIndex());
                    list.add(item);
                }
                liveData.postValue(list);
            }
        });

        RetrofitClient.getInstance(appContext).getApiService().getPictureList("pictureList", chapterId)
                .enqueue(new Callback<ApiResponse<List<PictureItem>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<PictureItem>>> call,
                                           @NonNull Response<ApiResponse<List<PictureItem>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<PictureItem> data = response.body().getData();
                            if (data != null) {
                                liveData.postValue(data);
                                cachePictureList(chapterId, data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<PictureItem>>> call,
                                          @NonNull Throwable t) {
                        String msg = getErrorMessage(t);
                        networkError.postValue("加载图片失败：" + msg);
                    }
                });

        return liveData;
    }

    private void cachePictureList(int chapterId, List<PictureItem> list) {
        executor.execute(() -> {
            List<PictureCacheEntity> entities = new ArrayList<>();
            for (PictureItem item : list) {
                entities.add(new PictureCacheEntity(
                        item.getId(), chapterId, item.getUrl(), item.getOrder()));
            }
            database.pictureCacheDao().deleteByChapterId(chapterId);
            database.pictureCacheDao().insertAll(entities);
        });
    }

    // ==================== 预加载下一章 ====================

    /**
     * 预加载下一章的图片数据到 Room 缓存
     */
    public void preloadNextChapter(int nextChapterId) {
        RetrofitClient.getInstance(appContext).getApiService().getPictureList("pictureList", nextChapterId)
                .enqueue(new Callback<ApiResponse<List<PictureItem>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<PictureItem>>> call,
                                           @NonNull Response<ApiResponse<List<PictureItem>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<PictureItem> data = response.body().getData();
                            if (data != null) {
                                cachePictureList(nextChapterId, data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<PictureItem>>> call,
                                          @NonNull Throwable t) {
                        String msg = getErrorMessage(t);
                        networkError.postValue("预加载图片失败：" + msg);
                    }
                });
    }
    // ==================== 清除所有缓存 ====================

    /**
     * 清除所有 Room 数据库缓存（分类、章节、图片）
     * 由 SettingsActivity 保存新 URL 后调用
     */
    public void clearAllRoomCaches() {
        executor.execute(() -> {
            database.classifyCacheDao().deleteAll();
            database.chapterCacheDao().deleteAll();
            database.pictureCacheDao().deleteAll();
        });
    }

    /**
     * 清除 OkHttp HTTP 缓存目录
     * 由 SettingsActivity 保存新 URL 后调用
     */
    public void clearHttpCache() {
        java.io.File cacheDir = new java.io.File(appContext.getCacheDir(), "http_cache");
        if (cacheDir.exists()) {
            deleteDir(cacheDir);
        }
    }

    private void deleteDir(java.io.File dir) {
        if (dir.isDirectory()) {
            java.io.File[] children = dir.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    deleteDir(child);
                }
            }
        }
        dir.delete();
    }


    /**
     * 将网络异常转换为用户友好的中文错误提示
     */
    private String getErrorMessage(Throwable t) {
        if (t instanceof java.net.SocketTimeoutException) {
            return "网络连接超时，请检查网络后重试";
        } else if (t instanceof java.net.UnknownHostException) {
            return "网络不可用，请连接网络";
        } else if (t instanceof java.net.ConnectException) {
            return "无法连接到服务器，请检查网络";
        } else if (t instanceof javax.net.ssl.SSLException) {
            return "安全连接失败，请检查网络环境";
        } else if (t instanceof java.io.IOException) {
            return "网络通信异常：" + t.getMessage();
        } else {
            String msg = t.getMessage();
            return msg != null ? msg : "未知网络错误";
        }
    }

    public void destroy() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
