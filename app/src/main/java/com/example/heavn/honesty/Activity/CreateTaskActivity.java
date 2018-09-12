package com.example.heavn.honesty.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.Content;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Sign;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;
import com.jkb.vcedittext.VerificationCodeEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class CreateTaskActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private ImageView back;
    private Button create;
    private EditText name,totalNumber;
    private TextView endDate,beginTime,endTime,invite_code,details;
    private VerificationCodeEditText code;
    private RadioGroup radioGroup,radioGroupVisibility;
    private RadioButton study,radio_public;
    private String type,taskName, beginDate,taskDetails,visibility="公开";
    private int days;
    private List<Sign> signs = new ArrayList<>();
    private List<SignUp> signUps = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        create = findViewById(R.id.create);
        create.setOnClickListener(this);
        name = findViewById(R.id.task_name);
        details = findViewById(R.id.task_details);
        totalNumber = findViewById(R.id.task_totalNumber);
        endDate = findViewById(R.id.task_endDate);
        endDate.setOnClickListener(this);
        beginTime = findViewById(R.id.task_beginTime);
        beginTime.setOnClickListener(this);
        endTime = findViewById(R.id.task_endTime);
        endTime.setOnClickListener(this);
        invite_code = findViewById(R.id.invite_code);

        code = findViewById(R.id.code);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        study = findViewById(R.id.study);
        study.setChecked(true);
        radioGroupVisibility  = findViewById(R.id.radioGroupVisibility);
        radioGroupVisibility.setOnCheckedChangeListener(this);
        radio_public = findViewById(R.id.radioButton_public);
        radio_public.setChecked(true);

        beginDate = DateUtil.getCurrentDate();
        endDate.setText(DateUtil.getCurrentDate());
    }

    @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.task_endDate:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if(monthOfYear < 10 && dayOfMonth < 10){
                            endDate.setText(year+"-0"+(monthOfYear+1)+"-0"+dayOfMonth);
                        }else if(monthOfYear < 10 && dayOfMonth >= 10){
                            endDate.setText(year+"-0"+(monthOfYear+1)+"-"+dayOfMonth);
                        }else if(monthOfYear >= 10 && dayOfMonth < 10){
                            endDate.setText(year+"-"+(monthOfYear+1)+"-0"+dayOfMonth);
                        }else{
                            endDate.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.task_beginTime:
                new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay < 10 && minute < 10){
                            beginTime.setText("0"+hourOfDay+":0"+minute);
                        }else if(hourOfDay < 10 && minute >= 10){
                            beginTime.setText("0"+hourOfDay+":"+minute);
                        }else if(hourOfDay >= 10 && minute < 10){
                            beginTime.setText(""+hourOfDay+":0"+minute);
                        }else{
                            beginTime.setText(hourOfDay+":"+minute);
                        }
                    }
                },0, 0,true).show();
                break;
            case R.id.task_endTime:
                new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay < 10 && minute < 10){
                            endTime.setText("0"+hourOfDay+":0"+minute);
                        }else if(hourOfDay < 10 && minute >= 10){
                            endTime.setText("0"+hourOfDay+":"+minute);
                        }else if(hourOfDay >= 10 && minute < 10){
                            endTime.setText(""+hourOfDay+":0"+minute);
                        }else{
                            endTime.setText(hourOfDay+":"+minute);
                        }
                    }
                },23, 59,true).show();
                break;
            case R.id.create:
                taskName = name.getText().toString();
                taskDetails = details.getText().toString();
                final MyUser user = MyUser.getCurrentUser(MyUser.class);
                String s_number = totalNumber.getText().toString();
                final String endDate = this.endDate.getText().toString();
                final String beginTime = this.beginTime.getText().toString();
                final String endTime = this.endTime.getText().toString();
                final String code = this.code.getText().toString();
                if(!taskName.equals("") && !taskDetails.equals("") &&!s_number.equals("") && !type.equals("")){
                    //任务结束时间不能小于当前日期
                    if(DateUtil.compareDate(DateUtil.getCurrentDate(),endDate)){
                        Toast.makeText(this, "任务结束时间无效，请重新选择", Toast.LENGTH_SHORT).show();
                        this.endDate.setText(DateUtil.getCurrentDate());
                    }else{
                        final Task task = new Task();
                        initSigns(endDate);
                        task.setName(taskName);
                        task.setDetails(taskDetails);
                        task.setBeginDate(beginDate);
                        task.setEndDate(endDate);
                        task.setBeginTime(beginTime);
                        task.setEndTime(endTime);
                        task.setType(type);
                        task.setVisibility(visibility);
                        task.setCode(code);
                        task.setAuthor(user);
                        task.setCurrentNumber(1);
                        task.setDays(days);
                        task.setTotalNumber(Integer.parseInt(s_number));
                        task.setComplete(false);
                        task.setSigns(signs);
                        task.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e == null){
                                    initSignUps(endDate);
                                    Task_User task_user = new Task_User();
                                    task_user.setParticipant(user);
                                    task_user.setTotalSign(0);
                                    task_user.setMyComplete(false);
                                    task_user.setSignUps(signUps);
                                    task_user.setTask(task);
                                    task_user.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e == null){
                                                Toast.makeText(CreateTaskActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                                MyUser user = MyUser.getCurrentUser(MyUser.class);
                                                Content content = new Content();
                                                content.setAuthor(user);
                                                content.setContent("我创建了一个任务："+task.getName()+"，在接下来的"+task.getDays()+"天的日子里，让我们一起好好完成这项任务！");
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
                                                Toast.makeText(CreateTaskActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(CreateTaskActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(this,"请把内容填写完整",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.habit:
                type = "习惯";
                break;
            case R.id.study:
                type = "学习";
                break;
            case R.id.exercise:
                type = "运动";
                break;
            case R.id.entertainment:
                type = "娱乐";
                break;
                //公开
            case R.id.radioButton_public:
                visibility = "公开";
                invite_code.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
                break;
                //私密
            case R.id.radioButton_private:
                visibility = "私密";
                invite_code.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }
    }

    //初始化sign数组
    public void initSigns(String endDate){
        days = DateUtil.getDays(beginDate,endDate);
        Log.e("days",""+days);
        for(int i=0;i<days;i++){
            Sign sign = new Sign();
            sign.setDate(DateUtil.calDays(beginDate,i));
            sign.setSignNumber(0);
            signs.add(sign);
            Log.e("sign",""+sign.getDate());
        }
    }
    //初始化signUp数组
    public void initSignUps(String endDate){
        days = DateUtil.getDays(beginDate,endDate);
        Log.e("days",""+days);
        for(int i=0;i<days;i++){
            SignUp signUp = new SignUp();
            signUp.setDate(DateUtil.calDays(beginDate,i));
            signUp.setIsSign("今日未打卡");
            signUp.setLocation("打卡地点");
            signUp.setProveImage("");
            signUps.add(signUp);
        }
    }
}
