package com.example.share.activity.Fragment;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.share.R;
import com.example.share.activity.UserData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends BaseFragment {
    private TextView usernameTextView;
    private ImageView avatarImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTextView = view.findViewById(R.id.profile_name);
        avatarImageView = view.findViewById(R.id.profile_image);
        Bundle arguments = getArguments();
        if (arguments != null) {
            UserData userData = (UserData) arguments.getSerializable("userData");
            if (userData != null) {
                // Set username
                usernameTextView.setText(userData.getUsername());

                // Load the avatar image from URL using Glide
                Glide.with(this)
                        .load(userData.getAvatar())  // Assuming userData has a getAvatarUrl() method
                        .placeholder(R.drawable.placeholder)  // Optional placeholder image
                        .error(R.drawable.error)  // Optional error image
                        .into(avatarImageView);
            }
        }
        return view;
    }
    @Override
    protected int initLayout() {
        return R.layout.fragment_profile;
    }
    @Override
    protected void initView() {
    }
    @Override
    protected void initData() {
    }


}

