package com.operit.netimageviewer.ui.picture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.operit.netimageviewer.data.model.PictureItem;
import com.operit.netimageviewer.data.repository.ImageRepository;

import java.util.List;

/**
 * 图片浏览 ViewModel
 * 支持预加载下一章
 */
public class PictureViewModel extends AndroidViewModel {

    private final ImageRepository repository;
    private final LiveData<String> networkError;

    public PictureViewModel(@NonNull Application application) {
        super(application);
        repository = new ImageRepository(application);
        networkError = repository.getNetworkError();
    }

    public LiveData<List<PictureItem>> getPictureList(int chapterId) {
        return repository.getPictureList(chapterId);
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    /**
     * 预加载下一章图片数据
     */
    public void preloadNextChapter(int nextChapterId) {
        repository.preloadNextChapter(nextChapterId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.destroy();
    }
}
