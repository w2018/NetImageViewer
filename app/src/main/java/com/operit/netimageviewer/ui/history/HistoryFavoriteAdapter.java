package com.operit.netimageviewer.ui.history;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.local.BrowseHistoryEntity;
import com.operit.netimageviewer.data.local.FavoriteEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 浏览历史 & 收藏通用适配器
 * 支持：普通点击、编辑模式（多选）、左滑删除
 */
public class HistoryFavoriteAdapter extends RecyclerView.Adapter<HistoryFavoriteAdapter.ViewHolder> {

    private final Context context;
    private List<Object> dataList = new ArrayList<>();
    private OnItemClickListener listener;

    // 编辑模式（多选）
    private boolean isEditMode = false;
    private final Set<Integer> checkedIds = new HashSet<>();

    // 删除回调
    private OnDeleteListener onDeleteListener;

    // 时间格式化
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    // ItemTouchHelper 实例（用于左滑删除）
    private ItemTouchHelper itemTouchHelper;

    /**
     * 点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    /**
     * 删除事件接口
     */
    public interface OnDeleteListener {
        /** 左滑单条删除 */
        void onSwipeDelete(int position);
        /** 多选批量删除 */
        void onMultiDelete(List<Integer> positions);
    }

    public HistoryFavoriteAdapter(Context context, List<?> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList != null ? new ArrayList<>(dataList) : new ArrayList<>();
        this.listener = listener;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    /**
     * 设置 ItemTouchHelper，用于左滑删除时调用
     */
    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = dataList.get(position);
        boolean isHistory = item instanceof BrowseHistoryEntity;

        // 设置标题和封面
        String title = isHistory
                ? ((BrowseHistoryEntity) item).getChapterTitle()
                : ((FavoriteEntity) item).getChapterTitle();
        String coverUrl = isHistory
                ? ((BrowseHistoryEntity) item).getCoverUrl()
                : ((FavoriteEntity) item).getCoverUrl();
        long time = isHistory
                ? ((BrowseHistoryEntity) item).getBrowseTime()
                : ((FavoriteEntity) item).getFavoriteTime();

        holder.tvTitle.setText(title != null ? title : "未知标题");

        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(context)
                    .load(coverUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.mipmap.ic_launcher);
        }

        holder.tvTime.setText(dateFormat.format(new Date(time)));

        // ---- 编辑模式（多选）UI ----
        if (isEditMode) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.deleteBackground.setVisibility(View.GONE);
            holder.tvDeleteAction.setVisibility(View.GONE);

            holder.cbSelect.setChecked(checkedIds.contains(position));
            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkedIds.add(position);
                } else {
                    checkedIds.remove(position);
                }
            });

            // 编辑模式下点击整行切换复选框
            holder.itemContent.setOnClickListener(v -> {
                boolean newChecked = !holder.cbSelect.isChecked();
                holder.cbSelect.setChecked(newChecked);
            });
        } else {
            // ---- 普通模式 UI ----
            holder.cbSelect.setVisibility(View.GONE);
            holder.cbSelect.setOnCheckedChangeListener(null);

            // 左滑时删除背景和按钮由 ItemTouchHelper 控制，默认隐藏
            holder.deleteBackground.setVisibility(View.GONE);
            holder.tvDeleteAction.setVisibility(View.GONE);

            // 普通模式下点击跳转
            holder.itemContent.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ========== 编辑模式方法 ==========

    /**
     * 切换编辑模式
     */
    public void setEditMode(boolean editMode) {
        if (this.isEditMode != editMode) {
            this.isEditMode = editMode;
            if (!editMode) {
                checkedIds.clear();
            }
            notifyDataSetChanged();
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * 全选
     */
    public void selectAll() {
        checkedIds.clear();
        for (int i = 0; i < dataList.size(); i++) {
            checkedIds.add(i);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void clearSelection() {
        checkedIds.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取选中数量
     */
    public int getSelectedCount() {
        return checkedIds.size();
    }

    /**
     * 获取选中的数据位置列表
     */
    public List<Integer> getSelectedPositions() {
        return new ArrayList<>(checkedIds);
    }

    /**
     * 获取选中的数据 ID 列表（BrowseHistoryEntity.id 或 FavoriteEntity.id）
     */
    public List<Integer> getSelectedIds() {
        List<Integer> ids = new ArrayList<>();
        for (int pos : checkedIds) {
            if (pos >= 0 && pos < dataList.size()) {
                Object item = dataList.get(pos);
                if (item instanceof BrowseHistoryEntity) {
                    ids.add(((BrowseHistoryEntity) item).getId());
                } else if (item instanceof FavoriteEntity) {
                    ids.add(((FavoriteEntity) item).getId());
                }
            }
        }
        return ids;
    }

    /**
     * 全选状态
     */
    public boolean isAllSelected() {
        return checkedIds.size() == dataList.size() && dataList.size() > 0;
    }

    // ========== 数据更新 ==========

    public void updateData(List<?> newData) {
        dataList.clear();
        if (newData != null) {
            dataList.addAll(newData);
        }
        checkedIds.clear();
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        if (position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        }
        return null;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < dataList.size()) {
            dataList.remove(position);
            // 调整 checkedIds 中受影响的位置
            Set<Integer> adjusted = new HashSet<>();
            for (int pos : checkedIds) {
                if (pos < position) {
                    adjusted.add(pos);
                } else if (pos > position) {
                    adjusted.add(pos - 1);
                }
            }
            checkedIds.clear();
            checkedIds.addAll(adjusted);
            notifyDataSetChanged();
        }
    }

    public void removeItems(List<Integer> positions) {
        // 从大到小排序以安全移除
        List<Integer> sorted = new ArrayList<>(positions);
        java.util.Collections.sort(sorted, java.util.Collections.reverseOrder());
        for (int pos : sorted) {
            if (pos >= 0 && pos < dataList.size()) {
                dataList.remove(pos);
            }
        }
        checkedIds.clear();
        notifyDataSetChanged();
    }

    // ========== ViewHolder ==========

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivCover;
        TextView tvTitle;
        TextView tvTime;
        View deleteBackground;
        TextView tvDeleteAction;
        LinearLayout itemContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            deleteBackground = itemView.findViewById(R.id.deleteBackground);
            tvDeleteAction = itemView.findViewById(R.id.tvDeleteAction);
            itemContent = itemView.findViewById(R.id.itemContent);
        }
    }
}
