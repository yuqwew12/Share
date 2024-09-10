package com.example.share.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.share.R;
import com.example.share.activity.Fragment.SaveFragment;

import java.util.List;

public class MysaveAdapter extends ArrayAdapter<SaveFragment.SaveResponse.SaveData.Record> {
    public MysaveAdapter(Context context, List<SaveFragment.SaveResponse.SaveData.Record> objects) {
        super(context, 0, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_like, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = convertView.findViewById(R.id.title_textview);
            viewHolder.usernameTextView = convertView.findViewById(R.id.username_textview);
            viewHolder.contentTextView = convertView.findViewById(R.id.content_textview);
            viewHolder.imageView = convertView.findViewById(R.id.image_view); // 确保正确获取 ImageView
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SaveFragment.SaveResponse.SaveData.Record record = getItem(position);
        viewHolder.titleTextView.setText(record.getTitle());
        viewHolder.usernameTextView.setText(record.getUsername());
        viewHolder.contentTextView.setText(record.getContent());

        // 加载图像
        if (record.getImageUrlList() != null && record.getImageUrlList().length > 0) {
            String imageUrl = record.getImageUrlList()[0]; // 直接获取 URL 字符串
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.bbb) // 加载占位图
                    .error(R.drawable.aaaa) // 加载失败时显示的图
                    .into(viewHolder.imageView);
        }
        return convertView;
    }

    class ViewHolder {
        TextView titleTextView;
        TextView usernameTextView;
        TextView contentTextView;
        ImageView imageView;
    }
}

