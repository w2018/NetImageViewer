package com.operit.netimageviewer.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.model.ClassifyItem;
import com.operit.netimageviewer.ui.chapter.ChapterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页分类列表适配器
 * RecyclerView 自动复用 View，Glide 自动回收 Bitmap
 */
public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ViewHolder> {

    private final List<ClassifyItem> items = new ArrayList<>();
    private final RequestOptions glideOptions;

    public ClassifyAdapter() {
        glideOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存原图和转换后的图
                .skipMemoryCache(false)
                .centerCrop();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classify, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassifyItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());

        Glide.with(holder.itemView.getContext())
                .load(item.getCover())
                .apply(glideOptions)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ChapterActivity.class);
            intent.putExtra("classifyId", item.getId());
            intent.putExtra("classifyTitle", item.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<ClassifyItem> newData) {
        items.clear();
        if (newData != null) {
            items.addAll(newData);
        }
        notifyDataSetChanged();
    }

    /**
     * 在 RecyclerView 回收时清理 Glide 请求，防止内存泄露
     */
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.ivCover);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
