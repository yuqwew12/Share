package com.example.share.activity.Response;

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

    public static class UserData {
        private String appKey;
        private String avatar;
        private long createTime;
        private int id;
        private String introduce;
        private long lastUpdateTime;
        private String password;
        private int sex;
        private String username;

        public String getAppKey() {
            return appKey;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUsername() {
            return username;
        }


        // Getters and setters
    }

    // Getters and setters
}
