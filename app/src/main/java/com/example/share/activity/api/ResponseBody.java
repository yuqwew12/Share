package com.example.share.activity.api;

import androidx.annotation.NonNull;

/**
 * http响应体的封装协议
 * @param <T> 泛型
 */
public class ResponseBody <T> {
    private int code;
    private String msg;
    private T data;
    public ResponseBody(){}
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return data;
    }

    @NonNull
    @Override
    public String toString() {
        return "ResponseBody{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
