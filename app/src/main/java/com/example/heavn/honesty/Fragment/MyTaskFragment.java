package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.heavn.honesty.Activity.CreateTaskActivity;
import com.example.heavn.honesty.Adapter.MyTaskAdapter;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class MyTaskFragment extends Fragment implements View.OnClickListener{
    private Button create;
    private ListView listView;
    private MyTaskAdapter adapter;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };
    private SwipeRefreshLayout refreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_task,container,false);
        create = view.findViewById(R.id.create_task);
        create.setOnClickListener(this);
        listView = view.findViewById(R.id.mylist);

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                refreshLayout.setRefreshing(false);
            }
        });

        //用线程加载数据
        new Thread(runnable).start();

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_task:
                Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initView(){
        BmobQuery<Task_User> query = new BmobQuery<Task_User>();
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        query.addWhereEqualTo("participant",user);
        query.include("participant,task");
        query.order("-totalSign");
        query.findObjects(new FindListener<Task_User>() {
            @Override
            public void done(List<Task_User> object, BmobException e) {
                if(e == null){
                    adapter = new MyTaskAdapter(getActivity(),object);
                    listView.setAdapter(adapter);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
