package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import com.example.heavn.honesty.R;
/**
 * 启动界面
 * Created by Administrator on 2018/6/1 0001.
 */

public class InitActivity extends BaseActivity {
    private TextView count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        count = findViewById(R.id.count);

        new CountDownTimer(4000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                count.setText(millisUntilFinished / 1000 + "s后关闭");
            }
            @Override
            public void onFinish() {

            }
        }.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(InitActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);//1秒后执行Runnable中的run方法
    }
}
