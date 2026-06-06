package com.operit.netimageviewer.ui.history;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.operit.netimageviewer.R;

/**
 * 浏览历史与收藏页面
 * 使用 ViewPager2 + TabLayout 实现双 Tab 切换
 * 支持编辑模式：Toolbar 菜单控制 HistoryFragment 的编辑状态
 */
public class HistoryFavoriteActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HistoryFavoritePagerAdapter pagerAdapter;
    private MenuItem editMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.history_favorite_title);
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new HistoryFavoritePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Tab 切换时，如果历史 Tab 处于编辑模式则自动退出
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != 0) {
                    HistoryFragment historyFragment = getHistoryFragment();
                    if (historyFragment != null && historyFragment.isInEditMode()) {
                        historyFragment.exitEditMode();
                    }
                }
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.tab_history);
            } else {
                tab.setText(R.string.tab_favorites);
            }
        }).attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次 Activity 恢复时，重新注册编辑模式监听器
        // 因为 Fragment 可能被重建，监听器引用会丢失
        registerEditModeListener();
    }

    /**
     * 查找 HistoryFragment 并注册编辑模式监听器
     */
    private void registerEditModeListener() {
        HistoryFragment historyFragment = getHistoryFragment();
        if (historyFragment != null) {
            historyFragment.setOnEditModeListener(isEditMode -> {
                updateEditMenuItem(isEditMode);
                // 编辑模式下禁用 ViewPager 滑动切换 Tab
                viewPager.setUserInputEnabled(!isEditMode);
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history_favorite, menu);
        editMenuItem = menu.findItem(R.id.action_edit);
        // 初始状态：显示"编辑"
        updateEditMenuItem(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 每次菜单显示前，同步编辑按钮状态
        HistoryFragment historyFragment = getHistoryFragment();
        if (historyFragment != null) {
            updateEditMenuItem(historyFragment.isInEditMode());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            HistoryFragment historyFragment = getHistoryFragment();
            if (historyFragment != null) {
                if (historyFragment.isInEditMode()) {
                    historyFragment.exitEditMode();
                } else {
                    historyFragment.enterEditMode();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 编辑模式下先退出编辑模式再返回
        HistoryFragment historyFragment = getHistoryFragment();
        if (historyFragment != null && historyFragment.isInEditMode()) {
            historyFragment.exitEditMode();
            return true;
        }
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        HistoryFragment historyFragment = getHistoryFragment();
        if (historyFragment != null && historyFragment.isInEditMode()) {
            historyFragment.exitEditMode();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 获取 HistoryFragment 实例
     * ViewPager2 的 Fragment tag 格式为 "f" + position
     */
    private HistoryFragment getHistoryFragment() {
        androidx.fragment.app.Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag("f" + 0);
        if (fragment instanceof HistoryFragment) {
            return (HistoryFragment) fragment;
        }
        return null;
    }

    /**
     * 更新编辑菜单项的标题（"编辑" ↔ "取消"）
     */
    public void updateEditMenuItem(boolean isEditMode) {
        if (editMenuItem != null) {
            editMenuItem.setTitle(isEditMode ? R.string.history_cancel : R.string.history_edit);
        }
    }
}