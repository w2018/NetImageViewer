package com.operit.netimageviewer.ui.chapter;

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
import com.operit.netimageviewer.data.model.ChapterItem;
import com.operit.netimageviewer.ui.picture.PictureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private final List<ChapterItem> items = new ArrayList<>();
    private final RequestOptions glideOptions;

    public ChapterAdapter() {
        glideOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .centerCrop();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChapterItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvPageCount.setText(String.format(Locale.getDefault(), "共 %d 张", item.getPageCount()));

        Glide.with(holder.itemView.getContext())
                .load(item.getCover())
                .apply(glideOptions)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PictureActivity.class);
            intent.putExtra("chapterId", item.getId());
            intent.putExtra("chapterTitle", item.getTitle());
            intent.putExtra("coverUrl", item.getCover());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<ChapterItem> newData) {
        items.clear();
        if (newData != null) {
            items.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.ivCover);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvPageCount;

        ViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPageCount = itemView.findViewById(R.id.tvPageCount);
        }
    }
}
