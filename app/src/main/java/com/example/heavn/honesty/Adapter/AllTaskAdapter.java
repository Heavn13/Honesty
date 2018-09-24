package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 所有任务的adapter
 * Created by Administrator on 2018/6/1 0001.
 */

public class AllTaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> list;

    public AllTaskAdapter(Context context, List<Task> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_all_task, null);
            //实例化对象
            holder.type = view.findViewById(R.id.type);
            holder.taskName = view.findViewById(R.id.task_name);
            holder.left = view.findViewById(R.id.previous);
            holder.signNumber = view.findViewById(R.id.signNumber);
            holder.currentNumber = view.findViewById(R.id.currentNumber);
            holder.totalNumber = view.findViewById(R.id.totalNumber);
            holder.end = view.findViewById(R.id.end);
            holder.label = view.findViewById(R.id.label);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        //判断任务类型
        if(list.get(i).getType().equals("学习")){
            holder.type.setImageResource(R.drawable.study_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.pink));
        }else if(list.get(i).getType().equals("运动")){
            holder.type.setImageResource(R.drawable.sport_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        }else if(list.get(i).getType().equals("习惯")){
            holder.type.setImageResource(R.drawable.habit_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.green));
        }else if(list.get(i).getType().equals("娱乐")){
            holder.type.setImageResource(R.drawable.play_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }
        holder.taskName.setText(list.get(i).getName().toString());
        int index = DateUtil.getIndex(list.get(i).getBeginDate().toString());
        holder.currentNumber.setText(""+list.get(i).getCurrentNumber());
        holder.totalNumber.setText(""+list.get(i).getTotalNumber());

        //任务未完成时给控件赋值
        if(!list.get(i).isComplete()){
            //任务最后一天的第二天自动结束任务
            if(DateUtil.compareDate(DateUtil.getCurrentDate(),list.get(i).getEndDate())){
                list.get(i).setComplete(true);
                holder.left.setText("0");
                holder.signNumber.setText("0");
                holder.end.setVisibility(View.VISIBLE);
                Task task = new Task();
                task.setComplete(true);
                task.update(list.get(i).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){

                        }else{
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                int left = list.get(i).getDays() - index;
                holder.left.setText(""+left);
                holder.signNumber.setText(""+list.get(i).getSigns().get(index).getSignNumber());
            }
        }
        //任务完成时给控件赋值
        else{
            holder.left.setText("0");
            holder.signNumber.setText("0");
            holder.end.setVisibility(View.VISIBLE);
        }
        return view;
    }
    class ViewHolder{
        LinearLayout label;
        ImageView type;//标签类型
        TextView taskName;//任务名称
        TextView left;//任务剩余天数
        TextView signNumber;//今日打卡人数
        TextView currentNumber;//当前任务报名人数
        TextView totalNumber;//任务最大人数
        ImageView end;//任务结束的图标
    }
}
