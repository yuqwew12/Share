package com.example.share.activity.Fragment;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.share.R;
import com.example.share.activity.SharedViewModel;
import com.example.share.activity.UserData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


public class ProfileFragment extends BaseFragment {
    private TextView usernameTextView;
    private ImageView avatarImageView;
    private SharedViewModel sharedViewModel;
    private Fragment likeFragment;
    private Fragment saveFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTextView = view.findViewById(R.id.profile_name);
        avatarImageView = view.findViewById(R.id.profile_image);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 设置用户ID
        sharedViewModel.setUserId(getUserIdFromArguments());

        // 获取传入的数据并设置用户名和头像
        Bundle arguments = getArguments();

        if (arguments != null) {
            UserData userData = (UserData) arguments.getSerializable("userData");
            if (userData != null) {
                // 设置用户名
                usernameTextView.setText(userData.getUsername());
                // 使用Glide加载头像图片
                Glide.with(this)
                        .load(userData.getAvatar())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(avatarImageView);

                // 初始化 LikeFragment
                likeFragment = new LikeFragment();
                saveFragment= new SaveFragment();
            }
        }
        UserData userData = (UserData) arguments.getSerializable("userData");
        // 添加按钮并设置点击事件
        Button button = view.findViewById(R.id.btn_view_liked_shares);
        button.setOnClickListener(v -> {
            // 跳转到 LikeFragment
            loadFragment(likeFragment, userData);
        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button button2 = view.findViewById(R.id.btn_view_saved_shares);
        button2.setOnClickListener(v -> {
            // 跳转到 SaveFragment

            loadFragment(saveFragment, userData);
        });

        return view;
    }

    private void loadFragment(Fragment fragment, UserData userData) {
        if (fragment instanceof LikeFragment) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("userData", userData);
            fragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = requireFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private String getUserIdFromArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            UserData userData = (UserData) arguments.getSerializable("userData");
            if (userData != null) {
                return userData.getId();
            }
        }
        return null;
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


