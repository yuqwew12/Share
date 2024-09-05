package com.example.share;


import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import com.example.share.activity.BaseActivity;
import com.example.share.activity.HomeActivity;
import com.example.share.activity.LoginActivity;
import com.example.share.activity.RegisterActivity;
import com.example.share.activity.Response.LoginResponse;
import com.example.share.activity.util.StringUtils;
import com.google.gson.Gson;


public class MainActivity extends BaseActivity {

    private Button btnLogin;
    private Button btnRegister;


    protected int initLayout() {
        return R.layout.activity_main;
    }


    protected void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    protected void initData() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent in = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(in);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(in);
            }
        });
    }
}