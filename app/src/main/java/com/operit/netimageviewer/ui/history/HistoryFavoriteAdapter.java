package com.operit.netimageviewer.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.local.BrowseHistoryEntity;
import com.operit.netimageviewer.data.local.FavoriteEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 通用适配器：同时支持浏览历史和收藏列表的展示
 */
public class HistoryFavoriteAdapter extends RecyclerView.Adapter<HistoryFavoriteAdapter.ViewHolder> {

    private final Context context;
    private final List<?> dataList;
    private final OnItemClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public HistoryFavoriteAdapter(Context context, List<?> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
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

        // 处理 BrowseHistoryEntity
        if (item instanceof BrowseHistoryEntity) {
            BrowseHistoryEntity entity = (BrowseHistoryEntity) item;
            holder.tvTitle.setText(entity.getChapterTitle());
            holder.tvTime.setText(dateFormat.format(new Date(entity.getBrowseTime())));
            Glide.with(context)
                    .load(entity.getCoverUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.ivCover);
        }
        // 处理 FavoriteEntity
        else if (item instanceof FavoriteEntity) {
            FavoriteEntity entity = (FavoriteEntity) item;
            holder.tvTitle.setText(entity.getChapterTitle());
            holder.tvTime.setText(dateFormat.format(new Date(entity.getFavoriteTime())));
            Glide.with(context)
                    .load(entity.getCoverUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.ivCover);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}