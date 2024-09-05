package com.example.share.activity.util;

public class StringUtils {
    public static boolean isEmpty(String str) {
        if (str == null || str.length() <= 0) {
            return true;
        } else {
            return false;
        }
    }
}
