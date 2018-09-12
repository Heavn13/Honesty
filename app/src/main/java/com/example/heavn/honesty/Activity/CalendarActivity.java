package com.example.heavn.honesty.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heavn.honesty.Adapter.MyCalendarAdapter;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.Fragment.HomeFragment;
import com.example.heavn.honesty.Fragment.MyCalendarFragment;
import com.example.heavn.honesty.Fragment.MyTimeLineFragment;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CalendarActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back,change;
    private MyCalendarFragment myCalendarFragment;
    private MyTimeLineFragment myTimeLineFragment;
    private boolean calendar = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        change = findViewById(R.id.change);
        change.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change:
                FragmentManager fragmentManager = getFragmentManager();
                //开启事务
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if(calendar){
                    if (myTimeLineFragment == null) {
                        myTimeLineFragment = new MyTimeLineFragment();
                    }
                    transaction.replace(R.id.frameLayout, myTimeLineFragment);
                    calendar = false;
                }else{
                    if (myCalendarFragment == null) {
                        myCalendarFragment = new MyCalendarFragment();
                    }
                    transaction.replace(R.id.frameLayout, myCalendarFragment);
                    calendar = true;
                }
                transaction.commit();// 事务提交

                break;
            default:
                break;
        }
    }

    private void initView(){
        FragmentManager fragmentManager = getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (myCalendarFragment == null) {
            myCalendarFragment = new MyCalendarFragment();
        }
        transaction.replace(R.id.frameLayout, myCalendarFragment);
        transaction.commit();// 事务提交
    }

}
