package com.example.heavn.honesty.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.heavn.honesty.Adapter.UserAdapter;
import com.example.heavn.honesty.Adapter.UserDetailAdapter;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 更多任务参与者页面
 * Created by Administrator on 2018/6/1 0001.
 */

public class MoreMembersActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private String taskId,currentId;
    private MyApp app;
    private ListView listView;
    private List<Task_User> list = new ArrayList<>();
    private UserDetailAdapter adapter;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            init(taskId);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_menmbers);

        app = (MyApp)getApplication();

        //获取传递过来的参数
        taskId = app.getTaskId();
        currentId = app.getCurrentId();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        listView = findViewById(R.id.membersList);

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

    private void init(String id){
        //初始化信息
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.getObject(id, new QueryListener<Task>() {
            @Override
            public void done(Task object, BmobException e) {
                if(e == null){
                    BmobQuery<Task_User> q1 = new BmobQuery<>();
                    q1.addWhereEqualTo("task",object);
                    q1.include("task,participant");
                    q1.findObjects(new FindListener<Task_User>() {
                        @Override
                        public void done(List<Task_User> list, BmobException e) {
                            adapter = new UserDetailAdapter(MoreMembersActivity.this,list);
                            listView.setAdapter(adapter);
                        }
                    });
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
