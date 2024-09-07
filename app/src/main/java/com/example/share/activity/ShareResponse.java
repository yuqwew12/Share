package com.example.share.activity;

import com.example.share.ShareItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ShareResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private ShareData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ShareData getData() {
        return data;
    }

    public void setData(ShareData data) {
        this.data = data;
    }

    public static class ShareData {
        @SerializedName("current")
        private int current;

        @SerializedName("records")
        private List<ShareItem> records;

        @SerializedName("total")
        private long total;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public List<ShareItem> getRecords() {
            return records;
        }

        public void setRecords(List<ShareItem> records) {
            this.records = records;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}
