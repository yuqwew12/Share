package com.example.share.activity;

import static com.example.share.activity.api.ApiConfig.YOUR_APP_ID;
import static com.example.share.activity.util.AppConfig.LOGIN_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;

import com.example.share.MainActivity;
import com.example.share.R;
import com.example.share.activity.api.ResponseBody;
import com.example.share.activity.util.AppConfig;

import okhttp3.Headers;
import okhttp3.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;

import com.example.share.activity.api.ApiConfig;
import com.google.gson.reflect.TypeToken;

public class LoginActivity extends BaseActivity {
    private EditText etAccount;
    private EditText etPwd;
    private Button btnLogin;
    private OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    protected int initLayout() {
        return R.layout.activity_login;
    }
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);
    }

    protected void initData() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (account.isEmpty() || pwd.isEmpty()) {
                    runOnUiThread(() -> showToast("用户名或密码不能为空"));
                    return;
                }
                login(account, pwd);
            }
        });
    }
    private void login(String username, String password) {
        new Thread(() -> {
            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "63460c96c2fb45738d9cdc7deebcdde3")
                    .add("appSecret", "942526cc88c2a0b54411d8472919aa9ffdcfa")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
            //请求组合创建
            Request request = new Request.Builder()
                    .url(LOGIN_URL+"?username="+username+"&password="+password)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                runOnUiThread(() -> showToast("服务器错误，请重试"));
                return;
            }

            String body = response.body().string();
            Log.d("info", body);

            Type jsonType = new TypeToken<ResponseBody<UserData>>(){}.getType();
            ResponseBody<UserData> dataResponseBody = gson.fromJson(body, jsonType);

            runOnUiThread(() -> {
                if (dataResponseBody.getCode() == 200) { // 假设 200 是成功的状态码
                    // 登录成功，跳转到 HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userData", dataResponseBody.getData());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish(); // 结束当前活动
                } else {
                    showToast("登录失败：" + dataResponseBody.getMsg());
                }
            });
        }
    };
}
