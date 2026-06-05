package com.operit.netimageviewer.ui.chapter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.operit.netimageviewer.data.model.ChapterItem;
import com.operit.netimageviewer.data.repository.ImageRepository;

import java.util.List;

public class ChapterViewModel extends AndroidViewModel {

    private final ImageRepository repository;
    private final LiveData<String> networkError;

    public ChapterViewModel(@NonNull Application application) {
        super(application);
        repository = new ImageRepository(application);
        networkError = repository.getNetworkError();
    }

    public LiveData<List<ChapterItem>> getChapterList(int classifyId) {
        return repository.getChapterList(classifyId);
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.destroy();
    }
}
