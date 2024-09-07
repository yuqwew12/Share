package com.example.share.activity.Request;

import com.google.gson.Gson;

public class ChangePhotoShareRequest {
    private String title;
    private String content;
    private long id;
    private long imageCode;
    private long pUserId;

    public ChangePhotoShareRequest(String title, String content, long id, long imageCode, long pUserId) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.imageCode = imageCode;
        this.pUserId = pUserId;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}