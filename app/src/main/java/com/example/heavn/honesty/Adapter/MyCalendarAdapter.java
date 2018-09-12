package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Activity.TaskDetailActivity;
import com.example.heavn.honesty.Bean.Sign;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 91905 on 2016/8/24 0024.
 */

public class MyCalendarAdapter extends BaseAdapter {
    private int[] days = new int[42];
    private Context context;
    private List<SignUp> list;
    private int year;
    private int month;

    public MyCalendarAdapter(Context context, int[][] days, int year, int month, List<SignUp> list) {
        this.context = context;
        int dayNum = 0;
        //将二维数组转化为一维数组，方便使用
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < days[i].length; j++) {
                this.days[dayNum] = days[i][j];
                dayNum++;
            }
        }
        this.year = year;
        this.month = month;
        this.list = list;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int i) {
        return days[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_date, null);
            viewHolder = new ViewHolder();
            viewHolder.date_item = view.findViewById(R.id.date_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.date_item.setText(days[i] + "");
        //其他填充日期不可见
        if (viewHolder.date_item.getText().equals("0"))
            viewHolder.date_item.setVisibility(View.INVISIBLE);
        //判断日历中的哪些日期已经打卡，改变其背景颜色
        for(int j = 0;j<list.size();j++){
            int y = DateUtil.getYearOfDate(list.get(j).getDate());
            int m = DateUtil.getMonthOfDate(list.get(j).getDate());
            int d = DateUtil.getDayOfDate(list.get(j).getDate());
            if(y == year && m == month && d == days[i] && list.get(j).getIsSign().equals("今日已打卡")){
                viewHolder.date_item.setBackgroundResource(R.drawable.round);
            }
        }
        return view;
    }

    /**
     * 优化Adapter
     */
    class ViewHolder {
        TextView date_item;
    }
}