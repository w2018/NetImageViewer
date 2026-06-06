package com.operit.netimageviewer.ui.cache;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.operit.netimageviewer.R;

import java.io.File;
import java.util.Locale;

/**
 * 缓存管理页面
 * 显示缓存大小 + 一键清理
 */
public class CacheManagerActivity extends AppCompatActivity {

    private TextView tvCacheSize;
    private Button btnClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_manager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.cache_title);
        }

        tvCacheSize = findViewById(R.id.tvCacheSize);
        btnClean = findViewById(R.id.btnCleanCache);

        refreshCacheSize();

        btnClean.setOnClickListener(v -> {
            cleanCache();
            refreshCacheSize();
        });
    }

    private void refreshCacheSize() {
        long size = getDirSize(getCacheDir());
        tvCacheSize.setText(String.format(Locale.getDefault(), "%.2f MB", size / (1024.0 * 1024.0)));
    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir == null || !dir.exists()) return 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;
        for (File file : files) {
            if (file.isDirectory()) {
                size += getDirSize(file);
            } else {
                size += file.length();
            }
        }
        return size;
    }

    private void cleanCache() {
        deleteDir(getCacheDir());
    }

    private boolean deleteDir(File dir) {
        if (dir == null || !dir.exists()) return false;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                }
                file.delete();
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
