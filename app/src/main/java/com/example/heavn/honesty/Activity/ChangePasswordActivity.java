package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.ActivityCollector;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 修改密码页面
 * Created by Administrator on 2018/6/1 0001.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private EditText old_password,new_password;
    private Button change;
    private TextView find;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        change = findViewById(R.id.change_password);
        change.setOnClickListener(this);
        find = findViewById(R.id.find);
        find.setOnClickListener(this);

        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change_password:
                String old = old_password.getText().toString();
                String new_ = new_password.getText().toString();
                if(!old.equals("") && !new_.equals("")){
                    BmobUser.updateCurrentUserPassword(old, new_, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(ChangePasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ChangePasswordActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        //强制下线
                                        ActivityCollector.finishAll();
                                        BmobUser.logOut();
                                        //重新设置登录界面
                                        LoginActivity.editor.clear();
                                        LoginActivity.editor.commit();
                                        LoginActivity.remember.setChecked(false);
                                    }
                                }, 1000);//1秒后执行Runnable中的run方法
                            }else{
                                Log.e("fail",e.toString());
                                Toast.makeText(ChangePasswordActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(ChangePasswordActivity.this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.find:
                Intent intent = new Intent(ChangePasswordActivity.this,FindPasswordActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
