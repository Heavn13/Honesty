package com.example.heavn.honesty.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.heavn.honesty.Util.ActivityCollector;

/**
 * 继承活动
 * Created by Administrator on 2018/6/1 0001.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
    }
}
