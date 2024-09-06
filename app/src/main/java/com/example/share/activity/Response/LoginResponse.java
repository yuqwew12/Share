package com.example.share.activity.Response;

import com.example.share.activity.UserData;

import javax.sql.RowSet;

public class LoginResponse {
    private String msg;
    private int code;
    private UserData data;

    public int getCode() {
        return code;
    }

    public UserData getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }



    // Getters and setters
}
