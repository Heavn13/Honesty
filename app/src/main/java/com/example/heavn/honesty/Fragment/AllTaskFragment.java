package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.heavn.honesty.Activity.TaskDetailActivity;
import com.example.heavn.honesty.Adapter.AllTaskAdapter;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class AllTaskFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView;
    private AllTaskAdapter adapter;
    private MyApp app;
    private List<Task> tasks = new ArrayList<>();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_task,container,false);
        listView = view.findViewById(R.id.allTask_list);
        listView.setOnItemClickListener(this);
        app = (MyApp)getActivity().getApplication();

        //用线程加载数据
        new Thread(runnable).start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    //listView的单个点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = tasks.get(position);
        // 要获取相应参数传值
        final Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        //当前任务的id
        app.setTaskId(task.getObjectId());
        BmobQuery<Task_User> query = new BmobQuery<Task_User>();
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        query.addWhereEqualTo("participant",user);
        query.addWhereEqualTo("task",task);
        query.findObjects(new FindListener<Task_User>() {
            @Override
            public void done(List<Task_User> object, BmobException e) {
                if(e == null){
                    //当前用户报名的Task_User的id
                    app.setCurrentId(object.get(0).getObjectId());
                    app.setEnroll(true);
                }else{
                    app.setCurrentId("");
                    app.setEnroll(false);
                }
                startActivity(intent);
            }
        });

    }

    private void initView(){
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.order("-currentNumber");
        query.findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> object, BmobException e) {
                if(e == null){
                    tasks = object;
                    adapter = new AllTaskAdapter(getActivity(),tasks);
                    listView.setAdapter(adapter);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
