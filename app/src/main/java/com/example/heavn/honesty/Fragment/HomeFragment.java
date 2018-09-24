package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Activity.UserDetailActivity;
import com.example.heavn.honesty.Activity.WriterActivity;
import com.example.heavn.honesty.Adapter.AllTaskAdapter;
import com.example.heavn.honesty.Adapter.ContentAdapter;
import com.example.heavn.honesty.Bean.Content;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.CircleImageView;
import com.example.heavn.honesty.Util.ImageDownloadTask;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button add;
    private CircleImageView head;
    private TextView username;
    private ListView messageList;
    private ContentAdapter adapter;
    private MyApp app;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };
    private SwipeRefreshLayout refreshLayout;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        app = (MyApp)getActivity().getApplication();

        add  = view.findViewById(R.id.add);
        add.setOnClickListener(this);

        head = view.findViewById(R.id.head);
        head.setOnClickListener(this);

        username = view.findViewById(R.id.username);
        messageList = view.findViewById(R.id.messageList);

        //用线程加载数据
        getActivity().runOnUiThread(runnable);

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                refreshLayout.setRefreshing(false);
            }
        });

        scrollView = view.findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (refreshLayout != null) {
                    refreshLayout.setEnabled(scrollView.getScrollY() == 0);
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.head:
                MyUser user = MyUser.getCurrentUser(MyUser.class);
                app.setUserId(user.getObjectId());
                intent = new Intent(getActivity(), UserDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.add:
                intent = new Intent(getActivity(), WriterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void initView(){
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        username.setText(user.getUsername());
        ImageDownloadTask imgTask = new ImageDownloadTask();
        if(user.getAvater() != null)
            imgTask.execute(user.getAvater(),head);

        BmobQuery<Content> query = new BmobQuery<Content>();
        query.include("author");
        query.order("-createdAt");
        query.findObjects(new FindListener<Content>() {
            @Override
            public void done(List<Content> object, BmobException e) {
                if(e == null){
                    adapter = new ContentAdapter(getActivity(),object);
                    messageList.setAdapter(adapter);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

}
