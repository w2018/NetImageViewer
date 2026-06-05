package com.operit.netimageviewer.ui.history;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * 浏览历史与收藏页面的 ViewPager2 适配器
 */
public class HistoryFavoritePagerAdapter extends FragmentStateAdapter {

    public HistoryFavoritePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HistoryFragment();
        } else {
            return new FavoriteFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}