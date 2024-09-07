package com.example.share.activity.Request;

import com.example.share.activity.Fragment.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ShareApiService {

    @POST("api/member/photo/share/save")
    Call<com.example.share.activity.Fragment.ApiResponse>  savePhotoShare(
            @Header("appId") String appId,
            @Header("appSecret") String appSecret,
            @Body SavePhotoShareRequest request
    );

    @POST("api/member/photo/share/change")
    Call<com.example.share.activity.Fragment.ApiResponse> changePhotoShareStatus(
            @Header("appId") String appId,
            @Header("appSecret") String appSecret,
            @Body ChangePhotoShareRequest request
    );
}
