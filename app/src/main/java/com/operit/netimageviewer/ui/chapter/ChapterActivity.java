package com.operit.netimageviewer.ui.chapter;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.model.ChapterItem;

import java.util.List;

/**
 * 二级目录：章节列表页
 */
public class ChapterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ChapterAdapter adapter;
    private ChapterViewModel viewModel;
    private int classifyId;
    private View rootView;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        classifyId = getIntent().getIntExtra("classifyId", 0);
        String title = getIntent().getStringExtra("classifyTitle");
        rootView = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChapterAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChapterViewModel.class);

        viewModel.getChapterList(classifyId).observe(this, this::onDataLoaded);

        // 观察网络错误
        viewModel.getNetworkError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Snackbar.make(rootView, errorMsg, Snackbar.LENGTH_LONG)
                        .setAction("重试", v -> {
                            viewModel.getChapterList(classifyId).observe(this, this::onDataLoaded);
                        })
                        .show();
            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.getChapterList(classifyId).observe(this, data -> {
                onDataLoaded(data);
                swipeRefresh.setRefreshing(false);
            });
        });
    }

    private void onDataLoaded(List<ChapterItem> data) {
        swipeRefresh.setRefreshing(false);
        Snackbar.make(rootView, R.string.refresh_success, Snackbar.LENGTH_SHORT).show();
        if (data != null && !data.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.setData(data);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            adapter.setData(null);
        }
    }

}
