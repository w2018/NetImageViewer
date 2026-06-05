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
import com.operit.netimageviewer.data.local.BrowseHistoryEntity;
import com.operit.netimageviewer.ui.picture.PictureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览历史 Tab Fragment
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyView;
    private HistoryFavoriteAdapter adapter;
    private List<BrowseHistoryEntity> historyList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new HistoryFavoriteAdapter(requireContext(), historyList, item -> {
            // 点击跳转到章节图片页面
            if (item instanceof BrowseHistoryEntity) {
                BrowseHistoryEntity entity = (BrowseHistoryEntity) item;
                Intent intent = new Intent(requireContext(), PictureActivity.class);
                intent.putExtra("chapterId", entity.getChapterId());
                intent.putExtra("chapterTitle", entity.getChapterTitle());
                intent.putExtra("coverUrl", entity.getCoverUrl());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        // 加载数据
        loadHistory();

        // 设置长按删除
        recyclerView.addOnItemTouchListener(new androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener() {
            // 长按在 adapter 中处理
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        new Thread(() -> {
            List<BrowseHistoryEntity> data = AppDatabase.getInstance(requireContext())
                    .browseHistoryDao().getAll();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    historyList.clear();
                    if (data != null && !data.isEmpty()) {
                        historyList.addAll(data);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    public void clearAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认清空")
                .setMessage("确定要清空所有浏览历史吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(requireContext()).browseHistoryDao().deleteAll();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                loadHistory();
                                Snackbar.make(requireView(), R.string.history_cleared, Snackbar.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
