package com.example.heavn.honesty.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.heavn.honesty.Util.ActivityCollector;

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
