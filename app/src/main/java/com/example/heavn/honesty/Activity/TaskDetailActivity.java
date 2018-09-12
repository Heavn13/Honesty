package com.example.heavn.honesty.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Adapter.UserAdapter;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Sign;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.MyApp;
import com.jkb.vcedittext.VerificationCodeEditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/6/1 0001.
 */
public class TaskDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView taskName, taskDetials,beginTime,endTime,beginDate,endDate,days,totalNumber,signNumber,currentNumber, viewMoreSignUps, viewMoreMembers;
    private RecyclerView signUps, members;
    private Button joinOrExit,sign,rank;
    private String taskId,currentId,code,visibility;
    private ImageView back,end;
    private Task task;
    private MyUser user;
    private MyApp app;
    private int day,index;
    private List<SignUp> signUpsArr = new ArrayList<>();
    //用于储存当前任务的signs数组，并进行修改
    private List<Sign> signs = new ArrayList<>();
    private LinearLayout signLayout;
    public static boolean enroll;
    private UserAdapter adapter;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            init(taskId);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        app = (MyApp)getApplication();

        //获取传递过来的参数
        taskId = app.getTaskId();
        currentId = app.getCurrentId();
        enroll = app.isEnroll();

        user = MyUser.getCurrentUser(MyUser.class);

        taskName = findViewById(R.id.taskName);
        taskDetials = findViewById(R.id.taskDetails);
        beginTime = findViewById(R.id.task_beginTime);
        endTime = findViewById(R.id.task_endTime);
        beginDate = findViewById(R.id.beginDate);
        endDate = findViewById(R.id.endDate);
        days = findViewById(R.id.days);
        totalNumber = findViewById(R.id.totalNumber);
        signNumber = findViewById(R.id.signNumber);
        currentNumber = findViewById(R.id.currentNumber);
        viewMoreMembers = findViewById(R.id.viewMoreMembers);
        viewMoreMembers.setOnClickListener(this);
        viewMoreSignUps = findViewById(R.id.viewMoreSignUps);
        viewMoreSignUps.setOnClickListener(this);
        signUps = findViewById(R.id.signUps);
        members = findViewById(R.id.members);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        joinOrExit = findViewById(R.id.joinOrExit);
        joinOrExit.setOnClickListener(this);
        sign = findViewById(R.id.sign);
        sign.setOnClickListener(this);
        rank = findViewById(R.id.rank);
        rank.setOnClickListener(this);
        signLayout = findViewById(R.id.signs);
        end = findViewById(R.id.end);

        new Thread(runnable).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init(taskId);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.viewMoreMembers:
                intent = new Intent(TaskDetailActivity.this,MoreMembersActivity.class);
                app.setTaskId(taskId);
                app.setCurrentId(currentId);
                startActivity(intent);
                break;
            case R.id.viewMoreSignUps:
                intent = new Intent(TaskDetailActivity.this,MoreSignUpsActivity.class);
                app.setTaskId(taskId);
                app.setCurrentId(currentId);
                startActivity(intent);
                break;
                //任务加入或退出操作
            case R.id.joinOrExit:
                //加入
                if(!enroll){
                    //判断当前任务人数是否已满
                    if(Integer.parseInt(currentNumber.getText().toString()) < Integer.parseInt(totalNumber.getText().toString())){
                        //如果该任务是私密的，需要填写邀请码才能够加入
                        if(visibility.equals("私密")){
                            View view = getLayoutInflater().inflate(R.layout.dialog_code, null);
                            final VerificationCodeEditText editText = view.findViewById(R.id.code);
                            AlertDialog dialog = new AlertDialog.Builder(this)
                                    .setTitle("请输入该任务的邀请码")//设置对话框的标题
                                    .setView(view)
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            hideSoftKeyboard(TaskDetailActivity.this);
                                            String content = editText.getText().toString();
                                            if(content.equals(code)){
                                                Intent intent = new Intent(TaskDetailActivity.this,JoinActivity.class);
                                                app.setTaskId(taskId);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(TaskDetailActivity.this, "邀请码错误", Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.dismiss();
                                        }
                                    }).create();
                            dialog.show();
                        }else{
                            intent = new Intent(TaskDetailActivity.this,JoinActivity.class);
                            app.setTaskId(taskId);
                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(this, "当前任务人数已满，无法加入该任务", Toast.LENGTH_SHORT).show();
                    }
                }
                //退出
                else{
                    //创建选择对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("确认是否退出该任务");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           //更新后台数据currentNumber和signs的值
                            Task task = new Task();
                            //已打卡才减少签到人数
                            if (signUpsArr.get(index).getIsSign().equals("今日已打卡")){
                                changeArrays(index);
                                task.setSigns(signs);
                            }
                            task.increment("currentNumber",-1);
                            task.update(taskId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    //必须先放在task更改之后？
                                    final Task_User task_user = new Task_User();
                                    task_user.setObjectId(currentId);
                                    task_user.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e == null){
                                                joinOrExit.setText("加入");
                                                sign.setVisibility(View.GONE);
                                                enroll = false;
                                                app.setEnroll(enroll);
                                                Toast.makeText(TaskDetailActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                                //更新数据
                                                init(taskId);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(TaskDetailActivity.this, "少年，恭喜你守住了初心", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //显示对话框
                    AlertDialog ad = builder.create();
                    ad.show();
                }
                //点击按钮后刷新结果
                init(taskId);
                break;
            case R.id.sign:
                intent = new Intent(TaskDetailActivity.this,SignUpActivity.class);
                app.setCurrentId(currentId);
                app.setTaskId(taskId);
                startActivity(intent);
                break;
            case R.id.rank:
                intent = new Intent(TaskDetailActivity.this,RankActivity.class);
                app.setCurrentId(currentId);
                app.setTaskId(taskId);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

    private void init(String id){
        enroll = app.isEnroll();
        taskId = app.getTaskId();
        currentId = app.getCurrentId();
        //初始化按钮文字
        if(enroll){
            joinOrExit.setText("退出");
            sign.setVisibility(View.VISIBLE);
            rank.setVisibility(View.VISIBLE);
        }else{
            joinOrExit.setText("加入");
            sign.setVisibility(View.GONE);
            rank.setVisibility(View.GONE);
        }
        //初始化信息
         BmobQuery<Task> query = new BmobQuery<Task>();
         query.getObject(id, new QueryListener<Task>() {
            @Override
            public void done(Task object, BmobException e) {
                if(e == null){
                    task = object;
                    signs = object.getSigns();
                    index = DateUtil.getIndex(object.getBeginDate());
                    taskName.setText(object.getName());
                    taskDetials.setText(object.getDetails());
                    beginTime.setText(object.getBeginTime());
                    endTime.setText(object.getEndTime());
                    beginDate.setText(object.getBeginDate());
                    endDate.setText(object.getEndDate());
                    days.setText(""+object.getDays());
                    totalNumber.setText(""+object.getTotalNumber());

                    currentNumber.setText(""+object.getCurrentNumber());
                    visibility = object.getVisibility();
                    code = object.getCode();
                    //获取当前任务所有的参与者
                    BmobQuery<Task_User> q1 = new BmobQuery<>();
                    q1.addWhereEqualTo("task",object);
                    q1.include("task,participant");
                    q1.findObjects(new FindListener<Task_User>() {
                        @Override
                        public void done(final List<Task_User> list, BmobException e) {
                            //设置recyclerview水平滑动布局
                            LinearLayoutManager ms= new LinearLayoutManager(TaskDetailActivity.this);
                            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                            //设置RecyclerView 布局
                            members.setLayoutManager(ms);
                            adapter = new UserAdapter(TaskDetailActivity.this,list);
                            members.setAdapter(adapter);
                            //跳转到用户个人资料页面
                            adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(int position) {
                                    String userId = list.get(position).getParticipant().getObjectId();
                                    app.setUserId(userId);
//                                    Toast.makeText(TaskDetailActivity.this, list.get(position).getParticipant().getUsername(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TaskDetailActivity.this,UserDetailActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onLongClick(int position) {

                                }
                            });
                        }
                    });
                    //若该任务未完成，则显示当日打卡人数
                    if(!object.isComplete()){
                        Log.e("complete","false");
                        //获取当前任务今日签到的人
                        signNumber.setText(""+object.getSigns().get(index).getSignNumber());
                        BmobQuery<Task_User> q2 = new BmobQuery<>();
                        q2.addWhereEqualTo("task",object);
                        q2.include("task,participant");
                        q2.findObjects(new FindListener<Task_User>() {
                            @Override
                            public void done(final List<Task_User> list, BmobException e) {
                                Log.e("size",""+list.size());
                                Task_User task_user;
                                List<Task_User> l = new ArrayList<>();
                                for(int i=0;i<list.size();i++){
                                    task_user = list.get(i);
                                    if(task_user.getSignUps().get(index).getIsSign().equals("今日已打卡")){
                                        l.add(task_user);
                                    }
                                }
                                //signNumber.setText(""+l.size());
                                //设置RecyclerView水平布局
                                LinearLayoutManager ms= new LinearLayoutManager(TaskDetailActivity.this);
                                ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                                signUps.setLayoutManager(ms);
                                adapter = new UserAdapter(TaskDetailActivity.this,l);
                                signUps.setAdapter(adapter);
                                //跳转到用户个人资料页面
                                adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        String userId = list.get(position).getParticipant().getObjectId();
                                        app.setUserId(userId);
//                                        Toast.makeText(TaskDetailActivity.this, list.get(position).getParticipant().getUsername(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(TaskDetailActivity.this,UserDetailActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onLongClick(int position) {

                                    }
                                });

                            }
                        });
                    }
                    //若该任务已完成，则不显示当日打卡人数
                    else{
                        Log.e("complete","true");
                        signLayout.setVisibility(View.GONE);
                        sign.setVisibility(View.GONE);
                        joinOrExit.setVisibility(View.GONE);
                        end.setVisibility(View.VISIBLE);
                    }

                    //获取当前用户的签到数组
                    BmobQuery<Task_User> q3 = new BmobQuery<>();
                    q3.getObject(currentId, new QueryListener<Task_User>() {
                        @Override
                        public void done(Task_User task_user, BmobException e) {
                            if(e == null){
                                signUpsArr = task_user.getSignUps();
                            }else{
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    //修改签到数组
    private void changeArrays(int index){
        int signNumber = signs.get(index).getSignNumber() - 1;
        signs.get(index).setSignNumber(signNumber);
    }

    /**
     * 隐藏Dialog中的软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
    }
}
