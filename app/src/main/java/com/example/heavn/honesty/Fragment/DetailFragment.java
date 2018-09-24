package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.heavn.honesty.Activity.LoginActivity;
import com.example.heavn.honesty.Activity.ManageTaskActivity;
import com.example.heavn.honesty.Activity.SettingActivity;
import com.example.heavn.honesty.Activity.UserDetailActivity;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.ActivityCollector;
import com.example.heavn.honesty.Util.CircleImageView;
import com.example.heavn.honesty.Util.ImageDownloadTask;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 个人中心的碎片
 * Created by Administrator on 2018/5/29 0029.
 */

public class DetailFragment extends Fragment implements View.OnClickListener {
    private ImageView sex;
    private CircleImageView head;
    private TextView username,signature;
    private Button logout,manage,setting;
    private LinearLayout detail;
    private MyApp app;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail,container,false);

        app = (MyApp)getActivity().getApplication();

        head = view.findViewById(R.id.head);
        username = view.findViewById(R.id.username);
        sex = view.findViewById(R.id.sex);
        signature = view.findViewById(R.id.signature);

        detail = view.findViewById(R.id.background);
        detail.setOnClickListener(this);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        manage = view.findViewById(R.id.manage);
        manage.setOnClickListener(this);

        setting = view.findViewById(R.id.setting);
        setting.setOnClickListener(this);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.background:
                MyUser user = MyUser.getCurrentUser(MyUser.class);
                app.setUserId(user.getObjectId());
                intent = new Intent(getActivity(),UserDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                BmobUser.logOut();   //清除缓存用户对象
                ActivityCollector.finishAll();
                intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.manage:
                intent = new Intent(getActivity(),ManageTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //初始化个人信息
    private void init(){
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        ImageDownloadTask imgTask = new ImageDownloadTask();
        if(user.getAvater() != null)
            imgTask.execute(user.getAvater(),head);
        if(user.getSex() != null && user.getSex().equals("男"))
            sex.setImageResource(R.drawable.boy);
        else if( user.getSex() != null && user.getSex().equals("女"))
            sex.setImageResource(R.drawable.girl);
        else
            sex.setImageResource(R.drawable.boy);
        username.setText(user.getUsername());
        if(user.getSignature() != null)
            signature.setText(user.getSignature());

    }
}
