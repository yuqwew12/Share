package com.example.share.activity;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static android.provider.UserDictionary.Words.APP_ID;

import static com.example.share.activity.util.AppConfig.Register_URL;

import android.content.ContentProviderOperation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.OkHttpClient;
import com.example.share.R;
import com.example.share.activity.api.ApiConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity{
    private EditText etAccount;
    private EditText etPwd;
    private Button btnResiter;
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnResiter = findViewById(R.id.btn_register);
    }

    @Override
    protected void initData() {
        btnResiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                register(account, pwd);
            }
        });
    }
    private void register(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建 JSON 请求体
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String json = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
                    RequestBody body = RequestBody.create(JSON, json);

                    // 创建请求
                    Request request = new Request.Builder()
                            .url(Register_URL)
                            .post(body)
                            .addHeader("Accept", "application/json, text/plain, */*")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("appId", ApiConfig.YOUR_APP_ID)
                            .addHeader("appSecret", ApiConfig.YOUR_APP_SECRET)
                            .build();

                    // 执行请求

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            showToast("Registration failed: " + response.message());
                        } else {
                            showToast( response.message());
                            showToast("Registration successful");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Error: " + e.getMessage());
                }
            }
        }).start();
    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
