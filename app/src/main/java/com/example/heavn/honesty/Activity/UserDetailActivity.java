package com.example.heavn.honesty.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.CircleImageView;
import com.example.heavn.honesty.Util.ImageDownloadTask;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 用户详情页面
 * Created by Administrator on 2018/6/1 0001.
 */

public class UserDetailActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back,sex;
    private CircleImageView head;
    private TextView username,signature,location,school,birthday,edit;
    private MyApp app;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        app = (MyApp)getApplication();

        userId = app.getUserId();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        head = findViewById(R.id.head);
        username = findViewById(R.id.username);
        sex = findViewById(R.id.sex);
        signature = findViewById(R.id.signature);
        location = findViewById(R.id.location);
        school = findViewById(R.id.school);
        birthday = findViewById(R.id.birthday);
        edit = findViewById(R.id.edit);
        edit.setOnClickListener(this);

        init();

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                Intent intent = new Intent(UserDetailActivity.this,ChangeUserDetailActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    //初始化个人信息
    private void init(){
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        //如果是当前用户
        if(userId.equals(user.getObjectId())){
            ImageDownloadTask imgTask = new ImageDownloadTask();
            if(user.getAvater() != null)
                imgTask.execute(user.getAvater(),head);
            if(user.getSex() != null && user.getSex().equals("男"))
                sex.setImageResource(R.drawable.boy);
            else if( user.getSex() != null && user.getSex().equals("女"))
                sex.setImageResource(R.drawable.girl);
            else
                sex.setImageResource(R.drawable.nosex);
            username.setText(user.getUsername());
            if(user.getSignature() != null)
                signature.setText(user.getSignature());
            if(user.getLocation() != null)
                location.setText(user.getLocation());
            if(user.getSchool() != null)
                school.setText(user.getSchool());
            if(user.getBirthday() != null)
                birthday.setText(user.getBirthday());
        }
        //如果是其他用户
        else{
            //隐藏编辑按钮
            edit.setVisibility(View.INVISIBLE);
            edit.setEnabled(false);
            //只能用这种查询方式？用单条查询还不行
            BmobQuery<MyUser> query1 = new BmobQuery<MyUser>();
            query1.addWhereEqualTo("objectId", userId);
            query1.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> object, BmobException e) {
                    if(e == null){
//                        Log.e("list",""+object.size());
                        ImageDownloadTask imgTask = new ImageDownloadTask();
                        if(object.get(0).getAvater() != null)
                            imgTask.execute(object.get(0).getAvater(),head);
                        if(object.get(0).getSex() != null && object.get(0).getSex().equals("男"))
                            sex.setImageResource(R.drawable.boy);
                        else if( object.get(0).getSex() != null && object.get(0).getSex().equals("女"))
                            sex.setImageResource(R.drawable.girl);
                        else
                            sex.setImageResource(R.drawable.nosex);
                        username.setText(object.get(0).getUsername());
                        if(object.get(0).getSignature() != null)
                            signature.setText(object.get(0).getSignature());
                        if(object.get(0).getLocation() != null)
                            location.setText(object.get(0).getLocation());
                        if(object.get(0).getSchool() != null)
                            school.setText(object.get(0).getSchool());
                        if(object.get(0).getBirthday() != null)
                            birthday.setText(object.get(0).getBirthday());
                    }else{
                        Log.e("error",e.toString());
                    }
                }
            });

        }

    }
}
