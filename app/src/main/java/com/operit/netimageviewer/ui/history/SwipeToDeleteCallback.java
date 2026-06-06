package com.operit.netimageviewer.ui.history;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.operit.netimageviewer.R;

/**
 * RecyclerView 右滑删除回调
 * 向右滑动时露出红色删除按钮背景
 * 左滑保持 ViewPager2 切换 Tab 功能不变
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final HistoryFavoriteAdapter adapter;
    private final OnSwipeDeleteListener listener;

    public interface OnSwipeDeleteListener {
        void onSwiped(int position);
    }

    /**
     * @param adapter       适配器
     * @param listener      删除确认回调
     * @param swipeDirs     滑动方向，默认 LEFT
     */
    public SwipeToDeleteCallback(HistoryFavoriteAdapter adapter, OnSwipeDeleteListener listener, int swipeDirs) {
        super(0, swipeDirs);
        this.adapter = adapter;
        this.listener = listener;
    }

    public SwipeToDeleteCallback(HistoryFavoriteAdapter adapter, OnSwipeDeleteListener listener) {
        this(adapter, listener, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false; // 不支持拖拽
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && listener != null) {
            listener.onSwiped(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // 获取 item 中的视图
            View itemView = viewHolder.itemView;
            View deleteBackground = itemView.findViewById(R.id.deleteBackground);
            View tvDeleteAction = itemView.findViewById(R.id.tvDeleteAction);
            View itemContent = itemView.findViewById(R.id.itemContent);

            if (deleteBackground != null && tvDeleteAction != null) {
                if (dX > 0) {
                    // 右滑中
                    deleteBackground.setVisibility(View.VISIBLE);
                    tvDeleteAction.setVisibility(View.VISIBLE);

                    // 滑动 itemContent 向右移动
                    float translationX = dX;
                    itemContent.setTranslationX(translationX);

                    // 删除按钮固定在左侧
                    tvDeleteAction.setTranslationX(0);
                } else {
                    // 左滑或复位
                    deleteBackground.setVisibility(View.GONE);
                    tvDeleteAction.setVisibility(View.GONE);
                    itemContent.setTranslationX(0);
                }
            } else {
                // 如果没有自定义视图，使用默认滑动效果
                viewHolder.itemView.setTranslationX(dX);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        // 重置视图
        View itemView = viewHolder.itemView;
        View deleteBackground = itemView.findViewById(R.id.deleteBackground);
        View tvDeleteAction = itemView.findViewById(R.id.tvDeleteAction);
        View itemContent = itemView.findViewById(R.id.itemContent);

        if (deleteBackground != null && tvDeleteAction != null && itemContent != null) {
            deleteBackground.setVisibility(View.GONE);
            tvDeleteAction.setVisibility(View.GONE);
            itemContent.setTranslationX(0);
        }
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.4f; // 滑动超过40%宽度即触发删除
    }
}
