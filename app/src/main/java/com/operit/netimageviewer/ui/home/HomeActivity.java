package com.operit.netimageviewer.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.model.ClassifyItem;
import com.operit.netimageviewer.data.remote.RetrofitClient;
import com.operit.netimageviewer.ui.settings.SettingsActivity;
import com.operit.netimageviewer.ui.cache.CacheManagerActivity;
import com.operit.netimageviewer.ui.history.HistoryFavoriteActivity;
import com.operit.netimageviewer.ui.help.HelpActivity;
import com.operit.netimageviewer.ui.about.AboutActivity;

import java.util.List;

/**
 * 首页：展示一级分类目录
 * ActionBar + 下拉刷新 + RecyclerView 网格布局
 */
public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ClassifyAdapter adapter;
    private HomeViewModel viewModel;
    private View rootView;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rootView = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyView = findViewById(R.id.emptyView);

        // 设置Toolbar作为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 网格布局：2列
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ClassifyAdapter();
        recyclerView.setAdapter(adapter);


        // 检查 API 地址是否已配置，防止首次启动未配置 URL 时崩溃
        if (!RetrofitClient.getInstance(this).hasBaseUrl()) {
            Snackbar.make(rootView, "请先设置 API 服务器地址", Snackbar.LENGTH_INDEFINITE)
                    .setAction("去设置", v -> {
                        startActivity(new Intent(this, SettingsActivity.class));
                    })
                    .show();
            // 禁用下拉刷新（因为没有 URL 可请求）
            swipeRefresh.setEnabled(false);
        } else {
            viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            // 观察数据
            viewModel.getClassifyList().observe(this, this::onDataLoaded);

            // 观察网络错误
            viewModel.getNetworkError().observe(this, errorMsg -> {
                if (errorMsg != null) {
                    Snackbar.make(rootView, errorMsg, Snackbar.LENGTH_LONG)
                            .setAction("重试", v -> {
                                viewModel.refresh();
                            })
                            .show();
                }
            });

            // 下拉刷新
            swipeRefresh.setOnRefreshListener(() -> {
                viewModel.refresh();
                viewModel.getClassifyList().observe(this, data -> {
                    onDataLoaded(data);
                    swipeRefresh.setRefreshing(false);
                });
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_cache) {
            startActivity(new Intent(this, CacheManagerActivity.class));
            return true;
        } else if (id == R.id.action_history_favorite) {
            startActivity(new Intent(this, HistoryFavoriteActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDataLoaded(List<ClassifyItem> data) {
        swipeRefresh.setRefreshing(false);
        Snackbar.make(rootView, R.string.refresh_success, Snackbar.LENGTH_SHORT).show();
        if (data != null && !data.isEmpty()) {
            adapter.setData(data);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            adapter.setData(null);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

}
