package com.example.share.activity;

import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.share.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity{

    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment shareFragment = new ShareFragment();
    private final Fragment moreFragment = new MoreFragment();

    @Override
    protected int initLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        loadFragment(profileFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    loadFragment(profileFragment);
                    return true;
                case R.id.nav_share:
                    loadFragment(shareFragment);
                    return true;
                case R.id.nav_more:
                    loadFragment(moreFragment);
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    protected void initData() {
        // Optionally, load initial data here
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
