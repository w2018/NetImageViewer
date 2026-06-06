package com.operit.netimageviewer.data.remote;

import com.operit.netimageviewer.data.model.ApiResponse;
import com.operit.netimageviewer.data.model.ChapterItem;
import com.operit.netimageviewer.data.model.ClassifyItem;
import com.operit.netimageviewer.data.model.PictureItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API 接口定义
 * 完整 JSON 返回示例见 README_API.md
 */
public interface ApiService {

    /**
     * 一级：获取分类列表
     * GET /api.php?action=classifyList
     */
    @GET("api.php")
    Call<ApiResponse<List<ClassifyItem>>> getClassifyList(
        @Query("action") String action
    );

    /**
     * 二级：获取某分类下的章节列表
     * GET /api.php?action=chapterList&id=
     */
    @GET("api.php")
    Call<ApiResponse<List<ChapterItem>>> getChapterList(
        @Query("action") String action,
        @Query("id") int classifyId
    );

    /**
     * 三级：获取某章节的图片列表
     * GET /api.php?action=pictureList&id=
     */
    @GET("api.php")
    Call<ApiResponse<List<PictureItem>>> getPictureList(
        @Query("action") String action,
        @Query("id") int chapterId
    );
}
