package com.operit.netimageviewer.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.local.AppDatabase;
import com.operit.netimageviewer.data.local.FavoriteEntity;
import com.operit.netimageviewer.ui.picture.PictureActivity;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的收藏 Tab Fragment
 */
public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyView;
    private HistoryFavoriteAdapter adapter;
    private List<FavoriteEntity> favoriteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new HistoryFavoriteAdapter(requireContext(), favoriteList, item -> {
            // 点击跳转到章节图片页面
            if (item instanceof FavoriteEntity) {
                FavoriteEntity entity = (FavoriteEntity) item;
                Intent intent = new Intent(requireContext(), PictureActivity.class);
                intent.putExtra("chapterId", entity.getChapterId());
                intent.putExtra("chapterTitle", entity.getChapterTitle());
                intent.putExtra("coverUrl", entity.getCoverUrl());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        loadFavorites();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        new Thread(() -> {
            try {
                List<FavoriteEntity> data = AppDatabase.getInstance(requireContext())
                        .favoriteDao().getAll();
                Log.d("FavFrag", "loadFavorites: data=" + (data == null ? "null" : "size=" + data.size()));
                if (data != null) {
                    for (FavoriteEntity e : data) {
                        Log.d("FavFrag", "  item: id=" + e.getId() + " chapterId=" + e.getChapterId()
                                + " title=" + e.getChapterTitle() + " time=" + e.getFavoriteTime());
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            if (data != null && !data.isEmpty()) {
                                favoriteList.clear();
                                favoriteList.addAll(data);
                                adapter.updateData(favoriteList);
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                                Log.d("FavFrag", "Showing " + data.size() + " favorites, adapter.getItemCount()=" + adapter.getItemCount());
                            } else {
                                favoriteList.clear();
                                adapter.updateData(favoriteList);
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                                Log.d("FavFrag", "Showing empty view (no favorites)");
                            }
                        } catch (Exception e) {
                            Log.e("FavFrag", "UI update error: " + e.getMessage(), e);
                        }
                    });
                } else {
                    Log.w("FavFrag", "getActivity() is null, cannot update UI");
                }
            } catch (Exception e) {
                Log.e("FavFrag", "loadFavorites exception: " + e.getMessage(), e);
            }
        }).start();
    }

    public void clearAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认清空")
                .setMessage("确定要清空所有收藏吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(requireContext()).favoriteDao().deleteAll();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                loadFavorites();
                                Snackbar.make(requireView(), R.string.favorites_cleared, Snackbar.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
