package com.example.heavn.honesty.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.MyApp;
import com.jkb.vcedittext.VerificationCodeEditText;

import java.util.Calendar;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChangeTaskActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private ImageView back;
    private Button create;
    private MyApp app;
    private EditText name,totalNumber;
    private TextView endDate,beginTime,endTime,invite_code,details;
    private VerificationCodeEditText code;
    private RadioGroup radioGroup,radioGroupVisibility;
    private RadioButton study,radio_public,radio_private;
    private String taskId, type,taskName, taskDetails,visibility="公开";
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_task);

        app = (MyApp)getApplication();
        //获取传递过来的参数
        taskId = app.getTaskId();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        create = findViewById(R.id.create);
        create.setOnClickListener(this);
        name = findViewById(R.id.task_name);
        details = findViewById(R.id.task_details);
        totalNumber = findViewById(R.id.task_totalNumber);
        endDate = findViewById(R.id.task_endDate);
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
        radio_private = findViewById(R.id.radioButton_private);
        radio_public.setChecked(true);

        new Thread(runnable).start();
    }

    @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        switch (v.getId()){
            case R.id.back:
                finish();
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
                final String beginTime = this.beginTime.getText().toString();
                final String endTime = this.endTime.getText().toString();
                final String code = this.code.getText().toString();
                if(!taskName.equals("") && !taskDetails.equals("") &&!s_number.equals("") && !type.equals("")){
                    Task task = new Task();
                    task.setName(taskName);
                    task.setDetails(taskDetails);
                    task.setBeginTime(beginTime);
                    task.setEndTime(endTime);
                    task.setType(type);
                    task.setVisibility(visibility);
                    task.setCode(code);
                    task.setAuthor(user);
                    task.setTotalNumber(Integer.parseInt(s_number));
                    task.update(taskId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(ChangeTaskActivity.this, "任务修改成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChangeTaskActivity.this, "任务修改失败", Toast.LENGTH_SHORT).show();
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

    private void initView(){
        //初始化信息
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.getObject(taskId, new QueryListener<Task>() {
            @Override
            public void done(Task object, BmobException e) {
                if (e == null) {
                    name.setText(object.getName());
                    details.setText(object.getDetails());
                    beginTime.setText(object.getBeginTime());
                    endTime.setText(object.getEndTime());
                    endDate.setText(object.getEndDate());
                    totalNumber.setText("" + object.getTotalNumber());
                    visibility = object.getVisibility();
                    if(visibility.equals("私密")){
                        radio_private.setChecked(true);
                        code.setText(object.getCode());
                    }else{
                        radio_public.setChecked(true);
                    }

                }
            }
        });
    }
}
