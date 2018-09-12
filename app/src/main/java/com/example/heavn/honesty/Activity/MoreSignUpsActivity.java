package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class MoreSignUpsActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private TextView myCalendar;
    private MyApp app;
    private String taskId,currentId;
    private ListView listView;
    private int index;
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
        setContentView(R.layout.activity_more_signups);

        //获取全局的参数
        app = (MyApp) getApplication();
        taskId = app.getTaskId();
        currentId = app.getCurrentId();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        myCalendar = findViewById(R.id.myCalendar);
        myCalendar.setOnClickListener(this);

        listView = findViewById(R.id.signUpsList);
        new Thread(runnable).start();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.myCalendar:
                intent = new Intent(MoreSignUpsActivity.this,CalendarActivity.class);
                app.setTaskId(taskId);
                app.setCurrentId(currentId);
                startActivity(intent);
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
                    index = DateUtil.getIndex(object.getBeginDate());
                    BmobQuery<Task_User> q2 = new BmobQuery<>();
                    q2.addWhereEqualTo("task",object);
                    q2.include("task,participant");
                    q2.findObjects(new FindListener<Task_User>() {
                        @Override
                        public void done(List<Task_User> list, BmobException e) {
                            Log.e("size",""+list.size());
                            Task_User task_user;
                            List<Task_User> l = new ArrayList<>();
                            for(int i=0;i<list.size();i++){
                                task_user = list.get(i);
                                if(task_user.getSignUps().get(index).getIsSign().equals("今日已打卡")){
                                    l.add(task_user);
                                }
                            }
                            //今日打卡人数
                            adapter = new UserDetailAdapter(MoreSignUpsActivity.this,l);
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
