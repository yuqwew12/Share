package com.example.share.activity.Request;

import com.google.gson.Gson;

public class SavePhotoShareRequest {
    private String title;
    private String content;
    private long imageCode;
    private long pUserId;

    public SavePhotoShareRequest(String title, String content, long imageCode, long pUserId) {
        this.title = title;
        this.content = content;
        this.imageCode = imageCode;
        this.pUserId = pUserId;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
