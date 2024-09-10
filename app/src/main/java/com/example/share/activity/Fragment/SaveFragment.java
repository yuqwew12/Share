package com.example.share.activity.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;

import androidx.lifecycle.ViewModelProvider;

import com.example.share.R;
import com.example.share.activity.Adapter.MyAdapter;
import com.example.share.activity.Adapter.MysaveAdapter;
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
import okhttp3.Response;

public class SaveFragment extends BaseFragment{

    private ListView listView;
    private List<SaveFragment.SaveResponse.SaveData.Record> savedPhotos = new ArrayList<>();
    private SharedViewModel sharedViewModel;
    private OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String TAG = "SaveFragment";
    private static final String BASE_API_URL = "https://api-store.openguet.cn/api/member/photo/share/save";
    private static final String APP_ID = "63460c96c2fb45738d9cdc7deebcdde3";
    private static final String APP_SECRET = "942526cc88c2a0b54411d8472919aa9ffdcfa";
    @Override
    protected int initLayout() {
        return R.layout.fragment_save;
    }
    @Override
    protected void initView() {
        listView = mRootView.findViewById(R.id.save_listview);
            fetchSavedPhotos();
    }
    @Override
    protected void initData() {}
    private void fetchSavedPhotos() {
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
            Log.d("SaveFragment", "User ID: " + userId);

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
        Type type = new TypeToken<SaveFragment.SaveResponse>() {}.getType();

        try {
            SaveFragment.SaveResponse saveResponse = gson.fromJson(responseJson, type);

            if (saveResponse != null && saveResponse.getData() != null) {
                savedPhotos.clear();
                savedPhotos.addAll(Arrays.asList(saveResponse.getData().getRecords()));
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
                        SaveFragment.SaveResponse.SaveData.Record record = new SaveFragment.SaveResponse.SaveData.Record();
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

                        savedPhotos.add(record);
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
        MysaveAdapter adapter = new MysaveAdapter(requireContext(), savedPhotos);
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

    public static class SaveResponse {
        private String msg;
        private int code;
        private SaveFragment.SaveResponse.SaveData data;

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

        public SaveFragment.SaveResponse.SaveData getData() {
            return data;
        }

        public void setData(SaveFragment.SaveResponse.SaveData data) {
            this.data = data;
        }

        public static class SaveData {
            private int current;
            private int size;
            private int total;
            private SaveFragment.SaveResponse.SaveData.Record[] records;

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

            public SaveFragment.SaveResponse.SaveData.Record[] getRecords() {
                return records;
            }

            public void setRecords(SaveFragment.SaveResponse.SaveData.Record[] records) {
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
