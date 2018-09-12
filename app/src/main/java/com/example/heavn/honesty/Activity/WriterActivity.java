package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.Content;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class WriterActivity extends BaseActivity implements View.OnClickListener{
    private Button bar_cancel;
    private Button bar_report;
    private EditText editText;
    private String s_content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_writer);

        bar_cancel = findViewById(R.id.bar_cancel);
        bar_cancel.setOnClickListener(this);
        bar_report = findViewById(R.id.bar_report);
        bar_report.setOnClickListener(this);

        editText = findViewById(R.id.editText);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            //取消，返回上一级
            case R.id.bar_cancel:
                finish();
                break;
            //保存所写的内容
            case R.id.bar_report:
                s_content = editText.getText().toString();
                if(!s_content.equals("")){
                    MyUser user = MyUser.getCurrentUser(MyUser.class);
                    Content content = new Content();
                    content.setAuthor(user);
                    content.setContent(s_content);
                    content.setCount(0);
                    content.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if(e==null){
                                Toast.makeText(WriterActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);//1秒后执行Runnable中的run方法
                            }else{
                                Toast.makeText(WriterActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(WriterActivity.this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }


}
