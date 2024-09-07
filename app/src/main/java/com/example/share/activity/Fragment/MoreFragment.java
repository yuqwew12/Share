package com.example.share.activity.Fragment;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

//import com.example.share.Manifest;
import com.example.share.R;
import okhttp3.*;
import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class MoreFragment extends Fragment {

    private EditText etTitle;
    private EditText etContent;
    private Button btnSave;
    private Button btnPublish;
    private Button btnSelectImage;
    private ImageView ivSelectedImage;
    private OkHttpClient httpClient;
    private Uri selectedImageUri;
    private Bitmap selectedBitmap;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        try {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                            ivSelectedImage.setImageBitmap(selectedBitmap);
                            Log.d("MoreFragment", "Image selected and displayed");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);
        btnSave = view.findViewById(R.id.btn_save);
        btnPublish = view.findViewById(R.id.btn_publish);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        ivSelectedImage = view.findViewById(R.id.iv_selected_image);

        // 设置按钮点击事件
        btnSave.setOnClickListener(v -> savePhotoShare());
        btnPublish.setOnClickListener(v -> publishPhotoShare());
        btnSelectImage.setOnClickListener(v -> {
            Log.d("MoreFragment", "btnSelectImage clicked");
            selectImage();
        });

        // 初始化 OkHttp 客户端
        initializeOkHttpClient();
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

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    private void savePhotoShare() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        long imageCode = 123456L; // 示例值
        long pUserId = 1L; // 示例值
        sendPhotoShareRequest("https://api-store.openguet.cn/api/member/photo/share/save", title, content, imageCode, pUserId);
    }

    private void publishPhotoShare() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        long id = 1L; // 示例值
        long imageCode = 123456L; // 示例值
        long pUserId = 1L; // 示例值

        sendPhotoShareRequest("https://api-store.openguet.cn/api/member/photo/share/publish", title, content, id, imageCode, pUserId);
    }

    private void sendPhotoShareRequest(String url, String title, String content, long imageCode, long pUserId) {
        // 构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("title", title);
        builder.addFormDataPart("content", content);
        builder.addFormDataPart("imageCode", String.valueOf(imageCode));
        builder.addFormDataPart("pUserId", String.valueOf(pUserId));

        if (selectedBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
            builder.addFormDataPart("image", "image.jpg", imageBody);
        }

        RequestBody requestBody = builder.build();

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
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
                getActivity().runOnUiThread(() -> showToast("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    handleApiResponse(responseBody);
                } else {
                    getActivity().runOnUiThread(() -> showToast("请求失败: " + response.code() + " " + response.message()));
                }
            }
        });
    }

    private void sendPhotoShareRequest(String url, String title, String content, long id, long imageCode, long pUserId) {
        // 构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("title", title);
        builder.addFormDataPart("content", content);
        builder.addFormDataPart("id", String.valueOf(id));
        builder.addFormDataPart("imageCode", String.valueOf(imageCode));
        builder.addFormDataPart("pUserId", String.valueOf(pUserId));

        if (selectedBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
            builder.addFormDataPart("image", "image.jpg", imageBody);
        }

        RequestBody requestBody = builder.build();

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
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
                getActivity().runOnUiThread(() -> showToast("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    handleApiResponse(responseBody);
                } else {
                    getActivity().runOnUiThread(() -> showToast("请求失败: " + response.code() + " " + response.message()));
                }
            }
        });
    }

    private void handleApiResponse(String responseBody) {
        // 处理服务器返回的数据
        // 处理服务器返回的数据
        // 假设响应数据为 JSON 格式
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            String msg = jsonObject.getString("msg");
            int code = jsonObject.getInt("code");
            String data = jsonObject.getString("data");

            if (code == 0) {
                getActivity().runOnUiThread(() -> showToast("发布成功"));
            } else {
                getActivity().runOnUiThread(() -> showToast("发布失败: " + msg));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(() -> showToast("处理响应失败: " + e.getMessage()));
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
