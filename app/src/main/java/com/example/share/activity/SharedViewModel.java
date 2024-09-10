package com.example.share.activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> userId = new MutableLiveData<>();

     public LiveData<String> getUserIdLiveData() {
        return userId;
    }

    public void setUserId(String id) {
        userId.setValue(id);
    }

    public Long getUserId() {
        return Long.parseLong(userId.getValue());
    }
}
