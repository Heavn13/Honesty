package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Adapter.UserAdapter;
import com.example.heavn.honesty.Bean.Content;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.SignUp;
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
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
/**
 * 加入任务
 * Created by Administrator on 2018/6/1 0001.
 */

public class JoinActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private TextView content,userName,currentDate;
    private Button join;
    private MyApp app;
    private Task task;
    private MyUser user;
    private String taskId,currentId,endDate,beginDate;
    private List<SignUp> signUpsArr = new ArrayList<>();
    private int day;
    private boolean enroll = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //获取全局参数
        app = (MyApp) getApplication();
        taskId = app.getTaskId();

        content = findViewById(R.id.content);
        userName = findViewById(R.id.userName);
        currentDate = findViewById(R.id.currentDate);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        join = findViewById(R.id.join);
        join.setOnClickListener(this);

        init(taskId);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.join:
                initSignUps(endDate);
                join.setBackgroundResource(R.drawable.red_fingerprint);
                final Task_User task_user = new Task_User();
                task_user.setParticipant(user);
                task_user.setTotalSign(0);
                task_user.setMyComplete(false);
                task_user.setSignUps(signUpsArr);
                task_user.setTask(task);
                task_user.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            currentId = s;
                            //更新后台数据currentNumber的值
                            Task task = new Task();
                            task.increment("currentNumber");
                            task.update(taskId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e == null){
                                        enroll = true;
                                        app.setCurrentId(currentId);
                                        app.setEnroll(enroll);
                                        Toast.makeText(JoinActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                                        MyUser user = MyUser.getCurrentUser(MyUser.class);
                                        Content content = new Content();
                                        content.setAuthor(user);
                                        content.setContent("我加入了任务："+task_user.getTask().getName()+"，在接下来的"+task_user.getTask().getDays()+"天的日子里，我一定会好好完成这项任务的。");
                                        content.setCount(0);
                                        content.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String objectId, BmobException e) {
                                                if(e==null){
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            finish();
                                                        }
                                                    }, 1000);//1秒后执行Runnable中的run方法
                                                }else{

                                                }
                                            }
                                        });
                                    }else{
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }else {
                            e.printStackTrace();
                        }
                    }
                });
                join.setEnabled(false);
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
                if(e==null){
                    task = object;
                    user = MyUser.getCurrentUser(MyUser.class);
                    beginDate = object.getBeginDate();
                    endDate = object.getEndDate();
                    content.setText("\u3000\u3000我自愿接受 "+object.getName()+" 这个任务，自 "+beginDate+" 起，至 "+endDate+" 为止，保证每天都完成任务，绝不轻易放弃!");
                    userName.setText(user.getUsername());
                    currentDate.setText(DateUtil.getCurrentDate());
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    //初始化signUp数组
    public void initSignUps(String endDate){
        day = DateUtil.getDays(beginDate,endDate);
        signUpsArr.clear();
        Log.e("days",""+day);
        for(int i=0;i<day;i++){
            SignUp signUp = new SignUp();
            signUp.setDate(DateUtil.calDays(beginDate,i));
            signUp.setIsSign("今日未打卡");
            signUp.setLocation("打卡地点");
            signUp.setProveImage("");
            signUpsArr.add(signUp);
        }
    }
}
