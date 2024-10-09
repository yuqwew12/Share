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
    private static OnItemClickListener onItemClickListener;
    private static OnSaveClickListener onSaveClickListener;

    public interface OnItemClickListener {
        void onItemClick(ShareItem item);
    }

    public interface OnSaveClickListener {
        void onSaveClick(ShareItem item);
    }

    public ShareAdapter(List<ShareItem> shareItems, OnItemClickListener onItemClickListener, OnSaveClickListener onSaveClickListener) {
        this.shareItems = shareItems;
        this.onItemClickListener = onItemClickListener;
        this.onSaveClickListener = onSaveClickListener;
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        return new ShareViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareViewHolder holder, int position) {
        ShareItem item = shareItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return shareItems.size();
    }

    static class ShareViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView usernameTextView;
        TextView createTimeTextView;
        Button hasCollectButton;
        Button hasLikeButton;
        Button hasFocusButton;
        Button saveButton;
        ImageView imageView;

        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            contentTextView = itemView.findViewById(R.id.content);
            usernameTextView = itemView.findViewById(R.id.username);
          //  createTimeTextView = itemView.findViewById(R.id.createTime);
          //  hasCollectButton = itemView.findViewById(R.id.hasCollect);
            hasLikeButton = itemView.findViewById(R.id.hasLike);
            hasFocusButton = itemView.findViewById(R.id.hasFocus);
            saveButton = itemView.findViewById(R.id.hasCollect); // 添加保存按钮
            imageView = itemView.findViewById(R.id.image);

            hasLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ShareItem item = shareItems.get(position);
                        onItemClickListener.onItemClick(item);
                    }
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ShareItem item = shareItems.get(position);
                        onSaveClickListener.onSaveClick(item);
                    }
                }
            });
        }

        public void bind(ShareItem item) {
            titleTextView.setText(item.getTitle());
            contentTextView.setText(item.getContent());
            usernameTextView.setText(item.getUsername());
           // createTimeTextView.setText(String.valueOf(item.getCreateTime()));

            if (item.getImageUrlList() != null && !item.getImageUrlList().isEmpty()) {
                String imageUrl = item.getImageUrlList().get(0);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // 默认图片资源
            }
        }
    }
}
