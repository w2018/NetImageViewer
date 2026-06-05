package com.operit.netimageviewer.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.operit.netimageviewer.data.model.ClassifyItem;
import com.operit.netimageviewer.data.repository.ImageRepository;

import java.util.List;

/**
 * 首页 ViewModel
 */
public class HomeViewModel extends AndroidViewModel {

    private final ImageRepository repository;
    private LiveData<List<ClassifyItem>> classifyList;
    private final LiveData<String> networkError;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new ImageRepository(application);
        classifyList = repository.getClassifyList();
        networkError = repository.getNetworkError();
    }

    public LiveData<List<ClassifyItem>> getClassifyList() {
        return classifyList;
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    public void refresh() {
        classifyList = repository.getClassifyList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.destroy();
    }
}
