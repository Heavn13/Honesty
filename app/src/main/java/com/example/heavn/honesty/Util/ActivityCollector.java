package com.example.heavn.honesty.Util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理类
 * Created by Administrator on 2018/5/31 0031.
 */


public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void add(Activity activity){
        activities.add(activity);
    }

    public static void remove(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
