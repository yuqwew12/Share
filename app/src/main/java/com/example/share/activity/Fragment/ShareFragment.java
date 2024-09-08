package com.example.share.activity.Fragment;

import static com.example.share.activity.api.ApiConfig.like_URL;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share.R;
import com.example.share.ShareItem;
import com.example.share.activity.Adapter.ImageInfoListTypeAdapter;
import com.example.share.activity.Adapter.ShareAdapter;
import com.example.share.activity.ImageInfo;
import com.example.share.activity.ShareResponse;
import com.example.share.activity.SharedViewModel;
import com.example.share.activity.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.share.activity.Fragment.ProfileFragment;
public class ShareFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private ShareAdapter shareAdapter;
    private List<ShareItem> shareItems = new ArrayList<>();
    private OkHttpClient httpClient;
    private Gson gson;
    CheckBox hasLikeCheckBox;
    private SharedViewModel sharedViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始化点赞按钮

        // 初始化视图
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化 OkHttp 和 Gson
        httpClient = new OkHttpClient();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<List<ImageInfo>>() {}.getType(), new ImageInfoListTypeAdapter());
        gson = gsonBuilder.create();

        // 初始化适配器
       // shareAdapter = new ShareAdapter(shareItems);
        recyclerView.setAdapter(shareAdapter);

        // 初始化适配器
        shareAdapter = new ShareAdapter(shareItems, new ShareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShareItem item) {
                // 处理点击事件
                handleLike(item);
            }
        });
        // 加载数据
        loadShareList();
        // 设置按钮点击事件
        Button hasLikeButton = view.findViewById(R.id.hasLike);




    }

    @Override
    protected int initLayout() {
        return R.id.recycler_view;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    // 处理点赞逻辑
    private void handleLike(ShareItem item) {
        boolean isChecked = !item.isHasLike(); // 反转当前状态
        if (isChecked) {
            // 点赞操作
            item.setHasLike(true); // 更新本地状态
            updateLikeStatusOnServer(item.getId(), true);
        } else {
            // 取消点赞操作
            item.setHasLike(false); // 更新本地状态
            updateLikeStatusOnServer(item.getId(), false);
        }
    }
    private void updateLikeStatusOnServer(long itemId, boolean isLiked) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求体
        String jsonInputString = String.format("{\"itemId\": %d, \"isLiked\": %b}", itemId, isLiked);
        RequestBody requestBody = RequestBody.create(jsonInputString, MediaType.get("application/json; charset=utf-8"));


        // 创建sharedViewModel 实例

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 观察用户ID变化
        sharedViewModel.getUserIdLiveData().observe(getViewLifecycleOwner(), userId -> {
            // 使用用户ID
            Log.d("ShareFragment", "User ID: " + userId);

        // 打印用户ID
        Log.d("MainActivity", "User ID: " + userId);
        System.out.println("User ID: " + userId);
        // 创建请求
        Request request = new Request.Builder()
                .url(like_URL + "?shareId="+itemId +"&userId="+userId)
                .post(requestBody)
                .header("appId", "63460c96c2fb45738d9cdc7deebcdde3")
                .header("appSecret", "942526cc88c2a0b54411d8472919aa9ffdcfa")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println("点赞状态更新失败: " + response.code());
                    return;
                }

                System.out.println("点赞状态更新成功");
            }
        });
        });
    }


    private void loadShareList() {
        // 构建请求
        Request request = new Request.Builder()
                .url("https://api-store.openguet.cn/api/member/photo/share?current=1&size=10&userId=1")
                .header("appId", "63460c96c2fb45738d9cdc7deebcdde3")
                .header("appSecret", "942526cc88c2a0b54411d8472919aa9ffdcfa")
                .get()
                .build();

        // 发送请求
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> showToast("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    parseResponse(responseBody);
                } else {
                    getActivity().runOnUiThread(() -> showToast("请求失败: " + response.code() + " " + response.message()));
                }
            }
        });
    }

    private void parseResponse(String responseBody) {
        try {
            ShareResponse shareResponse = gson.fromJson(responseBody, new TypeToken<ShareResponse>() {}.getType());
            if (shareResponse.getCode() == 200) {
                List<ShareItem> items = shareResponse.getData().getRecords();
                shareItems.clear();
                shareItems.addAll(items);
                getActivity().runOnUiThread(() -> {
                    shareAdapter.notifyDataSetChanged();
                });
                //shareAdapter.notifyDataSetChanged();
            } else {
               // showToast("获取数据失败: " + shareResponse.getMsg());
               //  requireActivity().runOnUiThread(() -> showToast("获取数据失败: " + shareResponse.getMsg()));
                getActivity().runOnUiThread(() -> showToast("获取数据失败: " + shareResponse.getMsg()));
            }
        } catch (Exception e) {
            e.printStackTrace();
           // showToast("解析响应失败: " + e.getMessage());
           // requireActivity().runOnUiThread(() -> showToast("解析响应失败: " + e.getMessage()));
            // 捕获异常，处理解析错误
            // 确保在主线程中更新UI
            getActivity().runOnUiThread(() -> showToast("解析响应失败: " + e.getMessage()));
        }
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}