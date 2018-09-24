package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import cn.bmob.v3.listener.UpdateListener;

/**
 * 修改绑定的手机号页面
 * Created by Administrator on 2018/6/1 0001.
 */

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    private EditText phone;
    private VerificationCodeEditText code;
    private Button send;
    private Button reBind;
    private String s_phone = "";
    private String s_code = "";
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        phone = findViewById(R.id.phone);
        code = findViewById(R.id.code);

        send = findViewById(R.id.send);
        send.setOnClickListener(this);
        reBind = findViewById(R.id.reBind);
        reBind.setOnClickListener(this);
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
            case R.id.reBind:
                s_code = code.getText().toString();
                s_phone = phone.getText().toString();
                if(!s_code.equals("")){
                    MyUser user = new MyUser();
                    user.setMobilePhoneNumber(s_phone);
                    user.setMobilePhoneNumberVerified(true);
                    MyUser cur = BmobUser.getCurrentUser(MyUser.class);
                    user.update(cur.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(ChangePhoneActivity.this, "手机号绑定成功", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ChangePhoneActivity.this,LoginActivity.class);
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
                                Toast.makeText(ChangePhoneActivity.this, "手机号绑定失败，该手机号已被绑定。", Toast.LENGTH_SHORT).show();
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
