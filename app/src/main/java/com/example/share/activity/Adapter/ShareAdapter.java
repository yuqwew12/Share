package com.example.share.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share.R;
import com.example.share.ShareItem;

import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {

    private static List<ShareItem> shareItems;
    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ShareItem item);
    }
    public ShareAdapter(List<ShareItem> shareItems, OnItemClickListener listener) {
        this.shareItems = shareItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        return new ShareViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareViewHolder holder, int position) {
        // 根据当前位置获取对应的分享项
        ShareItem item = shareItems.get(position);
        // 将分享项绑定到指定的视图持有者上
        holder.bind(item);
    }
    @Override
    public int getItemCount() {
        // 返回共享项列表中的项数
        return shareItems.size();
    }
    /**
     * ShareViewHolder类用于在RecyclerView中显示分享条目.
     * 它持有一组用于显示分享信息的视图，如标题、内容、用户名等.
     */
    static class ShareViewHolder extends RecyclerView.ViewHolder {
        // 视图变量初始化
        TextView titleTextView;
        TextView contentTextView;
        TextView usernameTextView;
        TextView createTimeTextView;
        Button hasCollectButton;
        Button hasLikeButton;
        Button hasFocusButton;
        ImageView imageView;

        /**
         * 构造函数，用于初始化ViewHolder中的视图.
         *
         * @param itemView 父视图，用于查找子视图.
         */
        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化视图
            titleTextView = itemView.findViewById(R.id.title);
            contentTextView = itemView.findViewById(R.id.content);
            usernameTextView = itemView.findViewById(R.id.username);
            createTimeTextView = itemView.findViewById(R.id.createTime);
            hasCollectButton = itemView.findViewById(R.id.hasCollect);
            hasLikeButton = itemView.findViewById(R.id.hasLike);
            hasFocusButton = itemView.findViewById(R.id.hasFocus);
            imageView = itemView.findViewById(R.id.image);
            hasLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 获取当前项在RecyclerView中的位置
                    int position = getAdapterPosition();
                    // 检查位置是否有效
                    if (position != RecyclerView.NO_POSITION) {
                        // 通过位置获取对应的ShareItem对象
                        ShareItem item = shareItems.get(position);
                        // 调用监听器的onItemClick方法，传递ShareItem对象给监听器处理
                        listener.onItemClick(item);
                    }
                }

            });
        }

        /**
         * 将ViewHolder绑定到特定的分享条目.
         * 此方法负责将条目数据填充到ViewHolder的视图中.
         *
         * @param item 要绑定的分享条目.
         */
        public void bind(ShareItem item) {
            // 设置文本内容
            titleTextView.setText(item.getTitle());
            contentTextView.setText(item.getContent());
            usernameTextView.setText(item.getUsername());
            createTimeTextView.setText(String.valueOf(item.getCreateTime()));



            // 加载图片
            if (item.getImageUrlList() != null && !item.getImageUrlList().isEmpty()) {
                String imageUrl = item.getImageUrlList().get(0);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .into(imageView);
            } else {
                // 如果没有图片，可以设置默认图片或者不显示
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // 假设 R.drawable.default_image 是默认图片资源
            }
        }
    }


}