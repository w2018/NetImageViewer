package com.operit.netimageviewer.ui.picture;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.local.AppDatabase;
import com.operit.netimageviewer.data.local.BrowseHistoryEntity;
import com.operit.netimageviewer.data.local.FavoriteEntity;
import com.operit.netimageviewer.data.model.PictureItem;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.util.Locale;

/**
 * 三级目录：图片浏览页
 * ViewPager2 + PhotoView 缩放 + 沉浸式 + 预加载下一章
 * 支持浏览历史记录和收藏功能
 */
public class PictureActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView tvPageIndicator;
    private PicturePagerAdapter adapter;
    private PictureViewModel viewModel;
    private int chapterId;
    private String chapterTitle;
    private String coverUrl;
    private List<Integer> allChapterIds = new ArrayList<>();
    private int currentChapterIndex = -1;
    private View rootView;
    private View emptyView;
    private MenuItem favoriteMenuItem;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        // 沉浸式全屏
        setupImmersive();

        rootView = findViewById(android.R.id.content);

        chapterId = getIntent().getIntExtra("chapterId", 0);
        chapterTitle = getIntent().getStringExtra("chapterTitle");
        coverUrl = getIntent().getStringExtra("coverUrl");

        // 设置 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(chapterTitle != null ? chapterTitle : "图片浏览");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewPager = findViewById(R.id.viewPager);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        emptyView = findViewById(R.id.emptyView);

        adapter = new PicturePagerAdapter();
        viewPager.setAdapter(adapter);

        // 设置 ViewPager2 离屏页面限制（预加载相邻页）
        viewPager.setOffscreenPageLimit(2);

        viewModel = new ViewModelProvider(this).get(PictureViewModel.class);

        // 加载当前章节
        loadChapter(chapterId);

        // 页面切换监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updatePageIndicator(position);
            }
        });

        // 点击视图切换沉浸式状态栏显示/隐藏
        findViewById(R.id.viewPager).setOnClickListener(v -> toggleImmersive());
        findViewById(R.id.emptyView).setOnClickListener(v -> toggleImmersive());

        // 监听网络错误，用 Snackbar 提示并支持重试
        viewModel.getNetworkError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Snackbar.make(rootView, errorMsg, Snackbar.LENGTH_LONG)
                        .setAction("重试", v -> loadChapter(chapterId))
                        .show();
            }
        });

        // 记录浏览历史
        recordBrowseHistory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture, menu);
        Log.d("PicAct", "onCreateOptionsMenu called");
        favoriteMenuItem = menu.findItem(R.id.action_favorite);
        // 检查当前章节是否已收藏，更新图标状态
        checkFavoriteStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            Log.d("PicAct", "onOptionsItemSelected: fav clicked");
            toggleFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 记录浏览历史
     */
    private void recordBrowseHistory() {
        new Thread(() -> {
            BrowseHistoryEntity history = new BrowseHistoryEntity(
                    chapterId,
                    chapterTitle != null ? chapterTitle : "",
                    coverUrl != null ? coverUrl : "",
                    System.currentTimeMillis()
            );
            AppDatabase.getInstance(getApplicationContext()).browseHistoryDao().insert(history);
        }).start();
    }

    /**
     * 检查当前章节是否已收藏，更新图标状态
     */
    private void checkFavoriteStatus() {
        new Thread(() -> {
            FavoriteEntity existing = AppDatabase.getInstance(getApplicationContext())
                    .favoriteDao().getByChapterId(chapterId);
            boolean fav = existing != null;
            runOnUiThread(() -> {
                isFavorite = fav;
                updateFavoriteIcon();
            });
        }).start();
    }

    /**
     * 切换收藏状态
     */
    private void toggleFavorite() {
        new Thread(() -> {
            try {
                Log.d("PicAct", "toggleFavorite() called, isFavorite=" + isFavorite
                        + ", chapterId=" + chapterId + ", rootView=" + rootView);
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                if (isFavorite) {
                    // 取消收藏
                    Log.d("PicAct", "toggleFavorite: deleting favorite, chapterId=" + chapterId);
                    db.favoriteDao().deleteByChapterId(chapterId);
                    Log.d("PicAct", "toggleFavorite: delete success");
                    runOnUiThread(() -> {
                        try {
                            Log.d("PicAct", "Removing fav, UI on main, rootView=" + rootView);
                            isFavorite = false;
                            updateFavoriteIcon();
                            Snackbar.make(rootView, "已取消收藏", Snackbar.LENGTH_SHORT).show();
                            Log.d("PicAct", "Snackbar shown: 已取消收藏");
                        } catch (Exception e) {
                            Log.e("PicAct", "UI thread error (remove): " + e.getMessage(), e);
                        }
                    });
                } else {
                    // 添加收藏
                    FavoriteEntity favorite = new FavoriteEntity(
                            chapterId,
                            chapterTitle != null ? chapterTitle : "",
                            coverUrl != null ? coverUrl : "",
                            System.currentTimeMillis()
                    );
                    Log.d("PicAct", "toggleFavorite: inserting favorite, chapterId=" + chapterId);
                    db.favoriteDao().insert(favorite);
                    Log.d("PicAct", "toggleFavorite: insert success");
                    runOnUiThread(() -> {
                        try {
                            Log.d("PicAct", "Adding fav, UI on main, rootView=" + rootView);
                            isFavorite = true;
                            updateFavoriteIcon();
                            Snackbar.make(rootView, "已添加到收藏", Snackbar.LENGTH_SHORT).show();
                            Log.d("PicAct", "Snackbar shown: 已添加到收藏");
                        } catch (Exception e) {
                            Log.e("PicAct", "UI thread error (add): " + e.getMessage(), e);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("PicAct", "toggleFavorite() exception: " + e.getMessage(), e);
            }
        }).start();
    }

    /**
     * 更新收藏图标（实心/空心）
     */
    private void updateFavoriteIcon() {
        if (favoriteMenuItem != null) {
            favoriteMenuItem.setIcon(
                    isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );
            favoriteMenuItem.setTitle(isFavorite ? "取消收藏" : "收藏");
        }
    }

    private void loadChapter(int cid) {
        viewModel.getPictureList(cid).observe(this, data -> {
            if (data != null && !data.isEmpty()) {
                viewPager.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setData(data);
                updatePageIndicator(0);
            } else {
                viewPager.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                adapter.setData(null);
                updatePageIndicator(0);
            }
        });

        // 预加载下一章（如果有）
        preloadNextIfAvailable(cid);
    }

    /**
     * 预加载下一章数据
     */
    private void preloadNextIfAvailable(int currentCid) {
        if (!allChapterIds.isEmpty() && currentChapterIndex >= 0
                && currentChapterIndex + 1 < allChapterIds.size()) {
            int nextChapterId = allChapterIds.get(currentChapterIndex + 1);
            viewModel.preloadNextChapter(nextChapterId);
        }
    }

    private void updatePageIndicator(int position) {
        int total = adapter.getItemCount();
        if (total > 0) {
            tvPageIndicator.setText(String.format(Locale.getDefault(), "%d / %d", position + 1, total));
            tvPageIndicator.setVisibility(View.VISIBLE);
        } else {
            tvPageIndicator.setVisibility(View.GONE);
        }
    }

    private boolean isImmersive = true;

    private void setupImmersive() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
    }

    private void toggleImmersive() {
        View decorView = getWindow().getDecorView();
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isImmersive) {
            // 显示状态栏和 Toolbar
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            toolbar.setVisibility(View.VISIBLE);
        } else {
            // 隐藏状态栏和 Toolbar
            setupImmersive();
            toolbar.setVisibility(View.GONE);
        }
        isImmersive = !isImmersive;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupImmersive();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE); // 默认显示 Toolbar
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理 Glide 内存缓存（可选，防止内存泄露）
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
