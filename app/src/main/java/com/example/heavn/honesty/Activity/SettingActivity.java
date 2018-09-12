package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.heavn.honesty.Adapter.SettingAdapter;
import com.example.heavn.honesty.Bean.Setting;
import com.example.heavn.honesty.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private ListView listView;
    private List<Setting> list = new ArrayList<>();
    private SettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        listView = findViewById(R.id.settingList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(SettingActivity.this,ChangeUserDetailActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(SettingActivity.this,ChangePasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(SettingActivity.this,ChangePhoneActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        BmobUser.logOut();   //清除缓存用户对象
                        intent = new Intent(SettingActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;

                }
            }
        });

        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    private void init(){
        Setting setting;
        setting = new Setting(R.drawable.setting_info,R.drawable.arrow,"修改资料");
        list.add(setting);
        setting = new Setting(R.drawable.setting_password,R.drawable.arrow,"修改密码");
        list.add(setting);
        setting = new Setting(R.drawable.setting_tel,R.drawable.arrow,"绑定手机号");
        list.add(setting);
        setting = new Setting(R.drawable.setting_reg,R.drawable.arrow,"注销账号");
        list.add(setting);
        adapter = new SettingAdapter(this,list);
        listView.setAdapter(adapter);
    }

}
