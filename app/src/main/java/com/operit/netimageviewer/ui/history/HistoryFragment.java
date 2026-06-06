package com.operit.netimageviewer.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
 * 支持：普通浏览、编辑模式（多选/全选删除）、左滑单条删除
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyView;
    private LinearLayout toolbarLayout;
    private Button btnSelectAll;
    private Button btnDeleteSelected;
    private Button btnCancelEdit;

    private HistoryFavoriteAdapter adapter;
    private final List<BrowseHistoryEntity> historyList = new ArrayList<>();

    /** 编辑模式回调接口，供 Activity 监听 */
    public interface OnEditModeListener {
        void onEditModeChanged(boolean isEditMode);
    }
    private OnEditModeListener editModeListener;

    public void setOnEditModeListener(OnEditModeListener listener) {
        this.editModeListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        toolbarLayout = view.findViewById(R.id.toolbarLayout);
        btnSelectAll = view.findViewById(R.id.btnSelectAll);
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelected);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 创建适配器
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

        // 注册左滑删除
        SwipeToDeleteCallback swipeCallback = new SwipeToDeleteCallback(adapter, position -> {
            Object item = adapter.getItem(position);
            if (item instanceof BrowseHistoryEntity) {
                BrowseHistoryEntity entity = (BrowseHistoryEntity) item;
                int id = entity.getId();
                new Thread(() -> {
                    AppDatabase.getInstance(requireContext()).browseHistoryDao().deleteById(id);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            historyList.remove(position);
                            adapter.removeItem(position);
                            updateEmptyState();
                            Snackbar.make(requireView(), R.string.history_delete_item, Snackbar.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 绑定编辑模式工具栏按钮事件
        setupToolbarButtons();

        // 加载数据
        loadHistory();

        return view;
    }

    /**
     * 绑定编辑模式工具栏按钮
     */
    private void setupToolbarButtons() {
        // 全选 / 取消全选
        btnSelectAll.setOnClickListener(v -> {
            if (adapter.isAllSelected()) {
                adapter.clearSelection();
                btnSelectAll.setText(R.string.history_select_all);
            } else {
                adapter.selectAll();
                btnSelectAll.setText(R.string.history_cancel_select_all);
            }
            updateDeleteButtonText();
        });

        // 删除选中项
        btnDeleteSelected.setOnClickListener(v -> {
            int count = adapter.getSelectedCount();
            if (count == 0) return;

            String message = getString(R.string.history_delete_confirm_message, count);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.history_delete_confirm_title)
                    .setMessage(message)
                    .setPositiveButton(R.string.confirm_delete, (dialog, which) -> {
                        List<Integer> ids = adapter.getSelectedIds();
                        new Thread(() -> {
                            AppDatabase.getInstance(requireContext())
                                    .browseHistoryDao().deleteByIds(ids);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    // 从数据源移除选中项
                                    List<Integer> positions = adapter.getSelectedPositions();
                                    // 先移除 historyList 中对应的数据
                                    for (int i = positions.size() - 1; i >= 0; i--) {
                                        int pos = positions.get(i);
                                        if (pos >= 0 && pos < historyList.size()) {
                                            historyList.remove(pos);
                                        }
                                    }
                                    adapter.removeItems(positions);
                                    updateEmptyState();
                                    // 退出编辑模式
                                    exitEditMode();
                                    String successMsg = getString(R.string.history_delete_success, count);
                                    Snackbar.make(requireView(), successMsg, Snackbar.LENGTH_SHORT).show();
                                });
                            }
                        }).start();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });

        // 取消编辑
        btnCancelEdit.setOnClickListener(v -> exitEditMode());
    }

    /**
     * 进入编辑模式
     */
    public void enterEditMode() {
        if (adapter.isEditMode()) return;

        toolbarLayout.setVisibility(View.VISIBLE);
        adapter.setEditMode(true);
        btnSelectAll.setText(R.string.history_select_all);
        updateDeleteButtonText();

        if (editModeListener != null) {
            editModeListener.onEditModeChanged(true);
        }
    }

    /**
     * 退出编辑模式
     */
    public void exitEditMode() {
        if (!adapter.isEditMode()) return;

        toolbarLayout.setVisibility(View.GONE);
        adapter.setEditMode(false);
        btnSelectAll.setText(R.string.history_select_all);

        if (editModeListener != null) {
            editModeListener.onEditModeChanged(false);
        }
    }

    /**
     * 当前是否处于编辑模式
     */
    public boolean isInEditMode() {
        return adapter.isEditMode();
    }

    /**
     * 更新删除按钮文本（显示选中数量）
     */
    private void updateDeleteButtonText() {
        int count = adapter.getSelectedCount();
        if (count > 0) {
            btnDeleteSelected.setText(getString(R.string.history_delete_selected_with_count, count));
        } else {
            btnDeleteSelected.setText(R.string.history_delete_selected);
        }
    }

    /**
     * 更新空视图显示状态
     */
    private void updateEmptyState() {
        if (historyList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
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
                    adapter.updateData(historyList);
                });
            }
        }).start();
    }

    /**
     * 清空所有历史（保留的对外方法）
     */
    public void clearAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.history_delete_confirm_title)
                .setMessage(R.string.history_clear_all_message)
                .setPositiveButton(R.string.confirm_delete, (dialog, which) -> {
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
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}