package com.example.heavn.honesty.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.heavn.honesty.Adapter.AllTaskAdapter;
import com.example.heavn.honesty.Adapter.ManageTaskAdapter;
import com.example.heavn.honesty.Adapter.MyTaskAdapter;
import com.example.heavn.honesty.Adapter.UserDetailAdapter;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 管理任务页面
 * Created by Administrator on 2018/6/1 0001.
 */

public class ManageTaskActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private ListView listView;
    private ManageTaskAdapter adapter;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        listView = findViewById(R.id.taskList);

        new Thread(runnable).start();
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

    private void initView(){
        BmobQuery<Task> query = new BmobQuery<Task>();
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        query.addWhereEqualTo("author",user.getObjectId());
        query.order("-createdAt");
        query.findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> object, BmobException e) {
                if(e == null){
                    adapter = new ManageTaskAdapter(ManageTaskActivity.this,object);
                    listView.setAdapter(adapter);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
