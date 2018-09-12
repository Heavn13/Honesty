package com.example.heavn.honesty.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.heavn.honesty.Activity.CalendarActivity;
import com.example.heavn.honesty.Adapter.MyCalendarAdapter;
import com.example.heavn.honesty.Adapter.TimeLineAdapter;
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
import cn.bmob.v3.listener.QueryListener;

public class MyCalendarFragment extends Fragment implements View.OnClickListener {
    private GridView gridView;//定义gridView
    private MyCalendarAdapter myCalendarAdapter;//定义adapter
    private ImageView previous;//左箭头
    private ImageView next;//右箭头
    private TextView bar_title;//标题
    private MyApp app;
    private int year;
    private int month;
    private String title,currentId;
    private int[][] days = new int[6][7];
    private List<SignUp> signUps = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_calendar,container,false);

        app = (MyApp) getActivity().getApplication();
        currentId = app.getCurrentId();

        previous =  view.findViewById(R.id.previous);
        next =  view.findViewById(R.id.next);
        gridView = view.findViewById(R.id.gridView);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        bar_title =  view.findViewById(R.id.title);
        //初始化日期数据
        initData();
        //初始化组件
        initView();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous:
                days = prevMonth();
                myCalendarAdapter = new MyCalendarAdapter(getActivity(), days, year, month,signUps);
                gridView.setAdapter(myCalendarAdapter);
                myCalendarAdapter.notifyDataSetChanged();
                setTile();
                break;
            case R.id.next:
                days = nextMonth();
                myCalendarAdapter = new MyCalendarAdapter(getActivity(), days, year, month,signUps);
                gridView.setAdapter(myCalendarAdapter);
                myCalendarAdapter.notifyDataSetChanged();
                setTile();
                break;
            default:
                break;
        }
    }

    private void initData() {
        year = DateUtil.getYear();
        month = DateUtil.getMonth();
    }

    private void initView() {
        if(currentId != null && !currentId.equals("")){
            //初始化信息
            BmobQuery<Task_User> query = new BmobQuery<Task_User>();
            query.getObject(currentId, new QueryListener<Task_User>() {
                @Override
                public void done(Task_User object, BmobException e) {
                    if(e==null){
                        signUps = object.getSignUps();
                        days = DateUtil.getDayOfMonthFormat(year, month);
                        myCalendarAdapter = new MyCalendarAdapter(getActivity(), days, year, month,signUps);//传入当前月的年
                        gridView.setAdapter(myCalendarAdapter);
                        gridView.setVerticalSpacing(60);
                        gridView.setEnabled(false);
                        setTile();
                    }else{
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    /**
     * 下一个月
     *
     * @return
     */
    private int[][] nextMonth() {
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
        days = DateUtil.getDayOfMonthFormat(year, month);
        return days;
    }

    /**
     * 上一个月
     *
     * @return
     */
    private int[][] prevMonth() {
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
        days = DateUtil.getDayOfMonthFormat(year, month);
        return days;
    }

    /**
     * 设置标题
     */
    private void setTile() {
        title = year + "年" + month + "月";
        bar_title.setText(title);
    }
}
