package com.operit.netimageviewer.ui.picture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.model.PictureItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager2 图片适配器，使用 PhotoView 支持缩放
 */
public class PicturePagerAdapter extends RecyclerView.Adapter<PicturePagerAdapter.ViewHolder> {

    private final List<PictureItem> items = new ArrayList<>();
    private final RequestOptions glideOptions;

    public PicturePagerAdapter() {
        glideOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .fitCenter();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_picture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PictureItem item = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getUrl())
                .apply(glideOptions)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<PictureItem> newData) {
        items.clear();
        if (newData != null) {
            items.addAll(newData);
        }
        notifyDataSetChanged();
    }

    public PictureItem getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    /**
     * View 回收时清除 Glide 加载，防止内存泄露
     */
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.photoView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        ViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}
