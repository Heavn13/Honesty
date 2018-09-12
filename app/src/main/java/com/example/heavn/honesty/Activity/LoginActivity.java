package com.example.heavn.honesty.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.MyApp;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText username;
    private EditText password;
    public static CheckBox remember;
    private TextView forget;
    private Button login;
    private Button register;
    private String susername;
    private String spassword;
    private MyApp app;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (MyApp)getApplication();

        Bmob.initialize(this, "1b783d63f7d627975f1276778ac90c8c");

        /**
         * 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                },
                MY_PERMISSION_REQUEST_CODE
        );

        MyUser user = MyUser.getCurrentUser(MyUser.class);
        if(user != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        remember = findViewById(R.id.remember);
        forget = findViewById(R.id.forget);
        forget.setOnClickListener(this);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("remember_password",false);
        //记住密码
        if(isRemember){
            String account1 = preferences.getString("username","");
            String password1 = preferences.getString("password","");
            username.setText(account1);
            password.setText(password1);
            remember.setChecked(true);
        }
        else{
            username.setText("");
            password.setText("");
            remember.setChecked(false);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //登录
            case R.id.login:
                susername = username.getText().toString();
                spassword = password.getText().toString();
                //记住密码
                editor = preferences.edit();
                if(remember.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("username",susername);
                    editor.putString("password",spassword);
                }else{
                    editor.clear();
                }
                editor.commit();
                if(!susername.equals("") && !spassword.equals("")){
                    BmobUser.loginByAccount(susername, spassword, new LogInListener<BmobUser>() {
                        @Override
                        public void done(final BmobUser user, BmobException e) {
                            if(user!=null){
                                Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        app.setUserId(user.getObjectId());
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1000);//1.5秒后执行Runnable中的run方法
                            }else{
                                Toast.makeText(getApplicationContext(),"账号或密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.forget:
                intent = new Intent(LoginActivity.this,FindPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
