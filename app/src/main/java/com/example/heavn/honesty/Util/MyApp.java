package com.example.heavn.honesty.Util;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 全局变量类
 */

public class MyApp extends Application{
    private String taskId,currentId,userId;//任务id，当前用户的任务id，用户id
    private boolean enroll;//是否加入
    public static RequestQueue queue;
    @Override
    public final void onCreate() {
         super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public boolean isEnroll() {
        return enroll;
    }

    public void setEnroll(boolean enroll) {
        this.enroll = enroll;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static RequestQueue getHttpQueue() {
        return queue;
    }
}
