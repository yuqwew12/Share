package com.example.share.activity.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

//import com.example.share.Manifest;
import com.example.share.R;
import com.example.share.activity.SharedViewModel;

import okhttp3.*;
import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
public class MoreFragment extends Fragment {

    private EditText etTitle;
    private EditText etContent;
    private Button btnPublish;
    private Button btnSelectImage;
    private ImageView ivSelectedImage;
    private OkHttpClient httpClient;
    private Uri selectedImageUri;
    private Bitmap selectedBitmap;
    private SharedViewModel sharedViewModel;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private ArrayList<Bitmap> selectedBitmaps = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        httpClient = new OkHttpClient();
        return inflater.inflate(R.layout.activity_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);
        btnPublish = view.findViewById(R.id.btn_publish);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        ivSelectedImage = view.findViewById(R.id.iv_selected_image);

    // 初始化 ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        try {
                            Context context = requireContext(); // 如果在 Fragment 中使用
                            if (context == null) {
                                // 处理 requireContext() 返回 null 的情况
                                return;
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), selectedImage);
                                Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                selectedBitmaps.add(bitmap);
                                // 设置图片到 ImageView
                                ivSelectedImage.setImageBitmap(bitmap);
                                Log.d("MoreFragment", "Image added to selectedBitmaps");
                            } else {
                                // 兼容旧版本
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);

                                // 设置图片到 ImageView
                                ivSelectedImage.setImageBitmap(bitmap);selectedBitmaps.add(bitmap);
                                Log.d("MoreFragment", "Image added to selectedBitmaps");
                            }
                        } catch (IOException e) {
                            Log.e("MoreFragment", "Failed to load image", e);
                        }
                    }
                }
        );
        // 设置按钮点击事件
        btnPublish.setOnClickListener(v -> publishPhotoShare());
        btnSelectImage.setOnClickListener(v -> {
            Log.d("MoreFragment", "btnSelectImage clicked");
            selectImage();
        });

        // 初始化 OkHttp 客户端
        initializeOkHttpClient();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 注销 ActivityResultLauncher
        if (imagePickerLauncher != null) {
            imagePickerLauncher.unregister();
        }
    }
    private void initializeOkHttpClient() {
        httpClient = new OkHttpClient();
    }
    private void selectImage() {
        // 检查是否需要请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 14 及以上版本，使用新的媒体权限
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_IMAGE_PICK);
            } else {
                pickImageFromGallery();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上版本，使用分区存储
            pickImageFromGallery();
        } else {
            // 低于 Android 10 的版本，使用旧的权限
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST_IMAGE_PICK);
            } else {
                pickImageFromGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Log.e("MoreFragment", "Permission denied");
                Toast.makeText(getActivity(), "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void publishPhotoShare() {
        // 获取标题和内容
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        // 检查图片列表是否为空
        if (selectedBitmaps.isEmpty()) {
            Log.e("MoreFragment", "No images selected");
            Toast.makeText(getActivity(), "请选择至少一张图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 先上传图片
        uploadImageToServer(title, content, selectedBitmaps);
    }
    // 更新 uploadImageToServer 方法以支持多张图片
    private void uploadImageToServer(String title, String content, List<Bitmap> selectedBitmaps) {
        // 检查 fileList 是否为空
        if (selectedBitmaps.isEmpty()) {
            Log.e("MoreFragment", "No images selected");
            getActivity().runOnUiThread(() -> showToast("请选择至少一张图片"));
            return;
        }

        // 构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (int i = 0; i < selectedBitmaps.size(); i++) {
            Bitmap bitmap = selectedBitmaps.get(i);
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
                builder.addFormDataPart("fileList", "image" + i + ".jpg", imageBody);
            } catch (Exception e) {
                Log.e("MoreFragment", "Failed to compress bitmap", e);
                getActivity().runOnUiThread(() -> showToast("压缩图片失败: " + e.getMessage()));
                return;
            }
        }

        RequestBody requestBody = builder.build();

        // 构建请求
        Request request = new Request.Builder()
                .url("https://api-store.openguet.cn/api/member/photo/image/upload")
                .headers(new Headers.Builder()
                        .add("appId", "63460c96c2fb45738d9cdc7deebcdde3")
                        .add("appSecret", "942526cc88c2a0b54411d8472919aa9ffdcfa")
                        .add("Accept", "application/json, text/plain, */*")
                        .build())
                .post(requestBody)
                .build();

        // 发送请求
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MoreFragment", "Network request failed", e);
                getActivity().runOnUiThread(() -> showToast("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    handleUploadResponse(responseBody, title, content);
                } else {
                    Log.e("MoreFragment", "Request failed: " + response.code() + " " + response.message());
                    getActivity().runOnUiThread(() -> showToast("请求失败: " + response.code() + " " + response.message()));
                }
            }
        });
    }


    private void handleUploadResponse(String responseBody, String title, String content) {
        try {
            Log.d("MoreFragment", "Response Body: " + responseBody); // 打印完整的响应体
            JSONObject jsonObject = new JSONObject(responseBody);
            String msg = jsonObject.optString("msg", "未知错误");
            int code = jsonObject.optInt("code", -1);
            JSONObject data = jsonObject.optJSONObject("data");

            if (data != null) {
                long imageCode = data.optLong("imageCode", -1);


                if (code == 200) {
                    Log.d("MoreFragment", "Image upload successful");
                    Log.d("MoreFragment", "Image Code: " + imageCode);

                    // 调用 sendPhotoShareRequest
                    sendPhotoShareRequest("https://api-store.openguet.cn/api/member/photo/share/add", title, content, imageCode);
                } else {
                    Log.e("MoreFragment", "Image upload failed: " + msg);
                    getActivity().runOnUiThread(() -> showToast("图片上传失败: " + msg));
                }
            } else {
                Log.e("MoreFragment", "Data is null in the response");
                getActivity().runOnUiThread(() -> showToast("服务器返回数据异常"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("MoreFragment", "Failed to parse JSON response", e);
            getActivity().runOnUiThread(() -> showToast("处理响应失败: " + e.getMessage()));
        }
    }
    private void sendPhotoShareRequest(String url, String title, String content, long imageCode) {
        // 确保 ViewModel 只初始化一次
        if (sharedViewModel == null) {
            sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        }

        // 在主线程中调用 observe 方法
        requireActivity().runOnUiThread(() -> {
            sharedViewModel.getUserIdLiveData().observe(getViewLifecycleOwner(), userId -> {
                // 使用用户ID
                Log.d("ShareFragment", "User ID: " + userId);

                // 构建请求体
                JSONObject requestBodyJson = new JSONObject();
                try {
                    requestBodyJson.put("title", title);
                    requestBodyJson.put("content", content);
                    requestBodyJson.put("imageCode", imageCode);
                    requestBodyJson.put("pUserId", userId);
                } catch (JSONException e) {
                    Log.e("MoreFragment", "Failed to build request body JSON", e);
                    getActivity().runOnUiThread(() -> showToast("请求体构建失败"));
                    return;
                }

                RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestBodyJson.toString());

                // 构建请求
                Request request = new Request.Builder()
                        .url(url)
                        .headers(new Headers.Builder()
                                .add("Accept", "application/json, text/plain, */*")
                                .add("Content-Type", "application/json")
                                .add("appId", "63460c96c2fb45738d9cdc7deebcdde3")
                                .add("appSecret", "942526cc88c2a0b54411d8472919aa9ffdcfa")
                                .build())
                        .post(requestBody)
                        .build();


                // 发送请求
                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("MoreFragment", "Network request failed", e);
                        getActivity().runOnUiThread(() -> showToast("网络请求失败: " + e.getMessage()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            handlePhotoShareResponse(responseBody);
                        } else {
                            Log.e("MoreFragment", "Request failed: " + response.code() + " " + response.message());
                            getActivity().runOnUiThread(() -> showToast("请求失败: " + response.code() + " " + response.message()));
                        }
                    }
                });
            });
        });
    }



    private void handlePhotoShareResponse(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            String msg = jsonObject.getString("msg");
            int code = jsonObject.getInt("code");
            Log.d("MoreFragment", "Response Body: " + responseBody); // 打印完整的响应体
            if (code == 200) {
                Log.d("MoreFragment", "Photo share successful");
                getActivity().runOnUiThread(() -> showToast("图文分享成功: " + msg));
            } else {
                Log.e("MoreFragment", "Photo share failed: " + msg);
                getActivity().runOnUiThread(() -> showToast("图文分享失败: " + msg));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("MoreFragment", "Failed to parse JSON response", e);
            getActivity().runOnUiThread(() -> showToast("处理响应失败: " + e.getMessage()));
        }
    }
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
