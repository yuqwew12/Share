package com.example.share.activity.Fragment;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.share.R;
import com.example.share.activity.BaseActivity;
import com.example.share.activity.ImageInfo;
import com.example.share.activity.SharedViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LikeFragment extends BaseFragment {

    private ListView listView;
    private List<LikeResponse.LikeData.Record> likedPhotos = new ArrayList<>();
    private SharedViewModel sharedViewModel;
    private OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String TAG = "LikeFragment";
    private static final String BASE_API_URL = "https://api-store.openguet.cn/api/member/photo/like";
    private static final String APP_ID = "63460c96c2fb45738d9cdc7deebcdde3";
    private static final String APP_SECRET = "942526cc88c2a0b54411d8472919aa9ffdcfa";
    @Override
    protected int initLayout() {
        return R.layout.fragment_like;
    }
    @Override
    protected void initView() {
        listView = mRootView.findViewById(R.id.like_listview);
        fetchLikedPhotos();
    }
    @Override
    protected void initData() {
        // 初始化数据
    }
    private void fetchLikedPhotos() {
        int current = 1; // 当前页
        int size = 100; // 页面大小

        if (sharedViewModel == null) {
            sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        }
        sharedViewModel.getUserIdLiveData().observe(getViewLifecycleOwner(), userId -> {
            if (userId == null || userId.isEmpty()) {
                handleErrorResponse("User ID is empty or null");
                return;
            }
            Log.d("likeFragment", "User ID: " + userId);

            // 构建完整的请求 URL
            String fullUrl = BASE_API_URL + "?current=" + current + "&size=" + size + "&userId=" + userId;

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .headers(new Headers.Builder()
                            .add("Accept", "application/json, text/plain, */*")
                            .add("appId", APP_ID)
                            .add("appSecret", APP_SECRET)
                            .build())
                    .get()
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleNetworkError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            handleResponse(responseBody);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        handleErrorResponse(response.code() + " " + response.message());
                    }
                }
            });
        });
    }
    private void handleResponse(String responseJson) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<LikeResponse>() {}.getType();

        try {
            LikeResponse likeResponse = gson.fromJson(responseJson, type);

            if (likeResponse != null && likeResponse.getData() != null) {
                likedPhotos.clear();
                likedPhotos.addAll(Arrays.asList(likeResponse.getData().getRecords()));
                new Handler(Looper.getMainLooper()).post(this::updateUI);
            } else {
                handleErrorResponse(responseJson);
            }
        } catch (JsonSyntaxException e) {
            handleJsonParseException(e, responseJson);
        }
    }
    private void handleJsonParseException(JsonSyntaxException e, String responseJson) throws Exception {
        Log.e(TAG, "JSON parse error: " + e.getMessage(), e);
        new Handler(Looper.getMainLooper()).post(() -> showToast("JSON 解析错误: " + e.getMessage()));
        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            if (jsonObject.has("msg")) {
                String msg = jsonObject.getString("msg");
                Log.e(TAG, "Server response message: " + msg);
                new Handler(Looper.getMainLooper()).post(() -> showToast("服务器响应消息: " + msg));
            }
            if (jsonObject.has("code")) {
                int code = jsonObject.getInt("code");
                Log.e(TAG, "Server response code: " + code);
            }
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject.has("records")) {
                    JSONArray recordsArray = dataObject.getJSONArray("records");
                    for (int i = 0; i < recordsArray.length(); i++) {
                        JSONObject recordObject = recordsArray.getJSONObject(i);
                        LikeResponse.LikeData.Record record = new LikeResponse.LikeData.Record();
                        if (recordObject.has("title")) {
                            record.setTitle(recordObject.getString("title"));
                        }
                        if (recordObject.has("username")) {
                            record.setUsername(recordObject.getString("username"));
                        }
                        if (recordObject.has("content")) {
                            record.setContent(recordObject.getString("content"));
                        }
                        if (recordObject.has("imageCode")) {
                            try {
                                record.setImageCode(recordObject.getLong("imageCode"));
                            } catch (NumberFormatException ex) {
                                record.setImageCode(0L); // 默认值或其他处理
                                Log.e(TAG, "Invalid imageCode value: " + recordObject.get("imageCode"), ex);
                            }
                        }
                        if (recordObject.has("imageUrlList")) {
                            Object imageUrlListObj = recordObject.get("imageUrlList");

                            if (imageUrlListObj instanceof JSONArray) {
                                JSONArray imageUrlListArray = (JSONArray) imageUrlListObj;
                                String[] imageUrlArray = new String[imageUrlListArray.length()];

                                for (int j = 0; j < imageUrlListArray.length(); j++) {
                                    String imageUrl = imageUrlListArray.getString(j);
                                    imageUrlArray[j] = imageUrl.trim();

                                    // 打印解析出来的 URL
                                    Log.d(TAG, "Parsed URL at index " + j + ": " + imageUrlArray[j]);
                                }

                                // 设置 String 数组
                                record.setImageUrlList(imageUrlArray);

                                // 打印最终的 String 数组长度
                                Log.d(TAG, "Parsed Image array length: " + imageUrlArray.length);
                            } else {
                                Log.e(TAG, "Unexpected type for imageUrlList: " + imageUrlListObj.getClass().getName());
                            }
                        }


                        likedPhotos.add(record);
                    }
                    new Handler(Looper.getMainLooper()).post(this::updateUI);
                }
            }
        } catch (JSONException je) {
            Log.e(TAG, "JSONException: " + je.getMessage(), je);
            new Handler(Looper.getMainLooper()).post(() -> showToast("JSON 解析错误: " + je.getMessage()));
        }
    }
    private void updateUI() {
        MyAdapter adapter = new MyAdapter(requireContext(), likedPhotos);
        listView.setAdapter(adapter);
    }
    private void handleErrorResponse(String errorMessage) {
        Log.e(TAG, "Error response: " + errorMessage);
        new Handler(Looper.getMainLooper()).post(() -> showToast("处理错误响应: " + errorMessage));
    }
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error", t);
        new Handler(Looper.getMainLooper()).post(() -> showToast("网络请求失败: " + t.getMessage()));
    }

    private void handleException(Exception e, String message) {
        Log.e(TAG, message, e);
        new Handler(Looper.getMainLooper()).post(() -> showToast(message));
    }
    class MyAdapter extends ArrayAdapter<LikeResponse.LikeData.Record> {
        public MyAdapter(Context context, List<LikeResponse.LikeData.Record> objects) {
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
            LikeResponse.LikeData.Record record = getItem(position);
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
    public static class LikeResponse {
        private String msg;
        private int code;
        private LikeData data;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public LikeData getData() {
            return data;
        }

        public void setData(LikeData data) {
            this.data = data;
        }

        public static class LikeData {
            private int current;
            private int size;
            private int total;
            private Record[] records;

            public int getCurrent() {
                return current;
            }

            public void setCurrent(int current) {
                this.current = current;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Record[] getRecords() {
                return records;
            }

            public void setRecords(Record[] records) {
                this.records = records;
            }

            public static class Record {
                private int collectId;
                private int collectNum;
                private String content;
                private long createTime;
                private boolean hasCollect;
                private boolean hasFocus;
                private boolean hasLike;
                private int id;
                private long imageCode; // 修改为 long 类型
                private String[] imageUrlList; // 修改为 String[] 类型
                private int likeId;
                private int likeNum;
                private long pUserId;
                private String title;
                private String username;

                public int getCollectId() {
                    return collectId;
                }

                public void setCollectId(int collectId) {
                    this.collectId = collectId;
                }

                public int getCollectNum() {
                    return collectNum;
                }

                public void setCollectNum(int collectNum) {
                    this.collectNum = collectNum;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public long getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(long createTime) {
                    this.createTime = createTime;
                }

                public boolean isHasCollect() {
                    return hasCollect;
                }

                public void setHasCollect(boolean hasCollect) {
                    this.hasCollect = hasCollect;
                }

                public boolean isHasFocus() {
                    return hasFocus;
                }

                public void setHasFocus(boolean hasFocus) {
                    this.hasFocus = hasFocus;
                }

                public boolean isHasLike() {
                    return hasLike;
                }

                public void setHasLike(boolean hasLike) {
                    this.hasLike = hasLike;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public long getImageCode() {
                    return imageCode;
                }

                public void setImageCode(long imageCode) {
                    this.imageCode = imageCode;
                }

                public String[] getImageUrlList() {
                    return imageUrlList;
                }

                public void setImageUrlList(String[] imageUrlList) {
                    this.imageUrlList = imageUrlList;
                }

                public int getLikeId() {
                    return likeId;
                }

                public void setLikeId(int likeId) {
                    this.likeId = likeId;
                }

                public int getLikeNum() {
                    return likeNum;
                }

                public void setLikeNum(int likeNum) {
                    this.likeNum = likeNum;
                }

                public long getpUserId() {
                    return pUserId;
                }

                public void setpUserId(long pUserId) {
                    this.pUserId = pUserId;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }
            }
        }
    }

}


