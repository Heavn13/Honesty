package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.ActivityCollector;
import com.jkb.vcedittext.VerificationCodeEditText;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class FindPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText phone;
    private EditText password;
    private VerificationCodeEditText code;
    private Button send;
    private Button find;
    private String s_phone = "";
    private String s_password = "";
    private String s_code = "";
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        code = findViewById(R.id.code);

        send = findViewById(R.id.send);
        send.setOnClickListener(this);
        find = findViewById(R.id.find);
        find.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.send:
                s_phone = phone.getText().toString();
                if(!s_phone.equals("") && s_phone.length() == 11){
                    BmobSMS.requestSMSCode(s_phone, "诚信状", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e == null){
                                Toast.makeText(getApplicationContext(),"短信验证码已发送",Toast.LENGTH_SHORT).show();
                                send.setEnabled(false);
                                new CountDownTimer(60000,1000){
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        send.setText(millisUntilFinished / 1000 + "秒");
                                    }
                                    @Override
                                    public void onFinish() {
                                        send.setEnabled(true);
                                        send.setText("重新发送");
                                    }
                                }.start();
                            }
                            else
                                Toast.makeText(getApplicationContext(),"短信验证码发送失败，请稍后重试",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(this,"手机号码格式不正确",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.find:
                s_password = password.getText().toString();
                s_code = code.getText().toString();
                if(!s_password.equals("") && !s_code.equals("")){
                    BmobUser.resetPasswordBySMSCode(s_code,s_password, new UpdateListener() {
                        @Override
                        public void done(BmobException ex) {
                            if(ex==null){
                                Toast.makeText(FindPasswordActivity.this, "密码重置成功", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(FindPasswordActivity.this,LoginActivity.class);
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
                                Toast.makeText(FindPasswordActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(this,"请把内容填写完整",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
