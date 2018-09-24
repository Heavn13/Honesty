package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.heavn.honesty.Adapter.AllTaskAdapter;
import com.example.heavn.honesty.Adapter.TimeLineAdapter;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * 打卡时间线碎片
 * Created by Administrator on 2018/5/31 0031.
 */


public class MyTimeLineFragment extends Fragment{
    private ListView listView;
    private MyApp app;
    private TimeLineAdapter adapter;
    private String currentId;
    private List<SignUp> signUps = new ArrayList<>();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };
    private SwipeRefreshLayout refreshLayout;//刷新

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_time_line,container,false);

        listView = view.findViewById(R.id.myTimeLine);

        app = (MyApp) getActivity().getApplication();
        currentId = app.getCurrentId();

        //刷新操作
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

    private void initView(){
        BmobQuery<Task_User> query = new BmobQuery<Task_User>();
        query.getObject(currentId, new QueryListener<Task_User>() {
            @Override
            public void done(Task_User task_user, BmobException e) {
                if(e == null){
                    adapter = new TimeLineAdapter(getActivity(),task_user.getSignUps(),listView,getActivity());
                    listView.setAdapter(adapter);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
