package com.example.share.activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.share.R;
import com.example.share.activity.Fragment.MoreFragment;
import com.example.share.activity.Fragment.ProfileFragment;
import com.example.share.activity.Fragment.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity{
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment shareFragment = new ShareFragment();
    private final Fragment moreFragment = new MoreFragment();
    @Override
    protected int initLayout() {
        return R.layout.activity_home;
    }
    public enum NavigationItem {
        PROFILE(R.id.nav_profile),
        SHARE(R.id.nav_share),
        MORE(R.id.nav_more);

        private final int id;

        NavigationItem(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static NavigationItem fromId(int id) {
            for (NavigationItem item : NavigationItem.values()) {
                if (item.id == id) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Unknown navigation item ID: " + id);
        }
    }
    /**
     * 初始化视图组件。
     * 设置默认显示的 Fragment，并为底部导航栏设置项选择监听器。
     */
    @Override
    protected void initView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        UserData userData = (UserData) getIntent().getSerializableExtra("userData");

        loadFragment(profileFragment, userData);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            NavigationItem navigationItem = NavigationItem.fromId(item.getItemId());
            switch (navigationItem) {
                case PROFILE:
                    loadFragment(profileFragment, userData);
                    return true;
                case SHARE:
                    loadFragment(shareFragment, userData);
                    return true;
                case MORE:
                    loadFragment(moreFragment, userData);
                    return true;
                default:
                    return false;
            }
        });
    }
    private void loadFragment(Fragment fragment, UserData userData) {
        if (fragment instanceof ProfileFragment) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("userData", userData);
            fragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void initData() {
        // Optionally, load initial data here
    }
}
