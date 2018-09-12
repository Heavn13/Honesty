package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Activity.CalendarActivity;
import com.example.heavn.honesty.Activity.SignUpActivity;
import com.example.heavn.honesty.Activity.TaskDetailActivity;
import com.example.heavn.honesty.Bean.Sign;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class MyTaskAdapter extends BaseAdapter{
    private Context context;
    private List<Task_User> list;
    private ViewHolder holder = null;
    private MyListener myListener = null;
    private String taskId,currentId;
    private int index;
    private MyApp app;

    public MyTaskAdapter(Context context, List<Task_User> list) {
        this.context = context;
        this.list = list;
        app = (MyApp)context.getApplicationContext();

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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            //引入布局
            view = View.inflate(context, R.layout.item_my_task, null);
            holder = new ViewHolder();
            myListener = new MyListener(i);
            //实例化对象
            holder.type = view.findViewById(R.id.type);
            holder.taskName = view.findViewById(R.id.task_name);
            holder.left = view.findViewById(R.id.previous);
            holder.signStatus = view.findViewById(R.id.sign_state);
            holder.location = view.findViewById(R.id.location);
            holder.signNumber = view.findViewById(R.id.signNumber);
            holder.currentNumber = view.findViewById(R.id.currentNumber);
            holder.totalSign = view.findViewById(R.id.days);
            holder.sign = view.findViewById(R.id.sign);
            holder.fold = view.findViewById(R.id.fold);
            holder.fold.setTag(i);
            holder.taskDetail = view.findViewById(R.id.task_detail);
            holder.taskCalendar = view.findViewById(R.id.task_calendar);
            holder.taskShare = view.findViewById(R.id.task_share);
            holder.taskDelete = view.findViewById(R.id.task_delete);
            holder.linearLayout = view.findViewById(R.id.bottom);
            holder.complete = view.findViewById(R.id.my_complete);
            holder.label = view.findViewById(R.id.label);
            holder.bottom = view.findViewById(R.id.bottom);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        if(list.get(i).getTask().getType().equals("学习")){
            holder.type.setImageResource(R.drawable.study_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.pink));
            holder.bottom.setBackgroundColor(context.getResources().getColor(R.color.pink));
        }else if(list.get(i).getTask().getType().equals("运动")){
            holder.type.setImageResource(R.drawable.sport_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
            holder.bottom.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        }else if(list.get(i).getTask().getType().equals("习惯")){
            holder.type.setImageResource(R.drawable.habit_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.bottom.setBackgroundColor(context.getResources().getColor(R.color.green));
        }else if(list.get(i).getTask().getType().equals("娱乐")){
            holder.type.setImageResource(R.drawable.play_big);
            holder.label.setBackgroundColor(context.getResources().getColor(R.color.orange));
            holder.bottom.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }
        holder.taskName.setText(list.get(i).getTask().getName().toString());
        index = DateUtil.getIndex(list.get(i).getTask().getBeginDate().toString());
        holder.signs = list.get(i).getTask().getSigns();
        //Log.e("index",""+index);
        holder.currentNumber.setText(""+list.get(i).getTask().getCurrentNumber());
        holder.totalSign.setText(""+list.get(i).getTotalSign());
        //通过点击该按钮可以获取子项item的view
        holder.fold.setOnClickListener(myListener);
        holder.fold.setTag(holder);
        holder.taskDetail.setOnClickListener(myListener);
        holder.taskDetail.setTag(R.id.tag_first,list.get(i).getTask().getObjectId());
        holder.taskDetail.setTag(R.id.tag_second,list.get(i).getObjectId());
        holder.taskCalendar.setOnClickListener(myListener);
        holder.taskCalendar.setTag(list.get(i).getObjectId());
        holder.taskShare.setOnClickListener(myListener);
        holder.taskDelete.setOnClickListener(myListener);
        holder.taskDelete.setTag(R.id.tag_holder,holder);
        holder.taskDelete.setTag(R.id.tag_first,list.get(i).getTask().getObjectId());
        holder.taskDelete.setTag(R.id.tag_second,list.get(i).getObjectId());
        holder.taskDelete.setTag(R.id.tag_position,i);
        //初始化设置底部按钮隐藏
        holder.linearLayout.setVisibility(View.GONE);

        //任务未完成时给控件赋值
        if(!list.get(i).isMyComplete()){
            Log.e("bool",""+list.get(i).getTask().getName().toString()+list.get(i).isMyComplete());
            //若不是任务最后一天打卡结束任务，则第二天自动结束任务
            if(DateUtil.compareDate(DateUtil.getCurrentDate(),list.get(i).getTask().getEndDate())){
                Log.e("date",DateUtil.getCurrentDate());
                list.get(i).setMyComplete(true);
                holder.left.setText("0");
                holder.signStatus.setText("打卡已结束");
                holder.location.setText("打卡地点未知");
                holder.signNumber.setText("0");
                holder.sign.setVisibility(View.GONE);
                holder.complete.setVisibility(View.VISIBLE);
                Task_User task_user = new Task_User();
                task_user.setMyComplete(true);
                task_user.update(list.get(i).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){

                        }else{
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                int left = list.get(i).getTask().getDays() - index;
                holder.left.setText(""+left);
                //设置打卡按钮状态
                String status = list.get(i).getSignUps().get(index).getIsSign();
                holder.signStatus.setText(status);
                if(status.equals("今日已打卡")){
                    holder.sign.setText("已打卡");
                }else{
                    holder.sign.setText("打卡");
                }
                holder.sign.setTag(R.id.tag_holder,holder);
                holder.sign.setTag(R.id.tag_first,list.get(i).getTask().getObjectId());
                holder.sign.setTag(R.id.tag_second,list.get(i).getObjectId());
                holder.sign.setOnClickListener(myListener);
                //必须得加一个这个 否则列表第一项会显示错误
                holder.sign.setVisibility(View.VISIBLE);
                holder.complete.setVisibility(View.GONE);
                holder.location.setText(list.get(i).getSignUps().get(index).getLocation());
                holder.signNumber.setText(""+list.get(i).getTask().getSigns().get(index).getSignNumber());
            }
        }
        //任务完成时给控件赋值
        else{
            holder.left.setText("0");
            holder.signStatus.setText("打卡已结束");
            holder.location.setText("打卡地点未知");
            holder.signNumber.setText("0");
            holder.sign.setVisibility(View.GONE);
            holder.complete.setVisibility(View.VISIBLE);
        }
        return view;
    }

    class ViewHolder{
        LinearLayout label;
        LinearLayout bottom;
        ImageView type;//标签类型
        TextView taskName;//任务名称
        TextView left;//任务剩余天数
        TextView signStatus;//打卡状态
        TextView location;//打卡地点
        TextView signNumber;//签到人数
        TextView currentNumber;//当前加入任务的人数
        TextView totalSign;//总积分
        Button sign;//打卡
        ImageView fold,taskDetail,taskCalendar,taskShare,taskDelete;//底下四个以及折叠图标
        LinearLayout linearLayout;//底下放四个图标
        ImageView complete;//任务结束的图标
        List<Sign> signs = new ArrayList<>();//每个任务的签到数组
    }

    private class MyListener implements View.OnClickListener{
        int position;
        public MyListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                //打卡按钮
                case R.id.sign:
                    //通过setTag和getTag获取子项Item的view
                    holder = (ViewHolder)v.getTag(R.id.tag_holder);
                    taskId = (String)v.getTag(R.id.tag_first);
                    currentId = (String)v.getTag(R.id.tag_second);
                    if(holder.sign.getText().toString().equals("打卡")){
                        intent = new Intent(context, SignUpActivity.class);
                        app.setTaskId(taskId);
                        app.setCurrentId(currentId);
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "今日已打卡，无需重复打卡", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    //折叠按钮
                case R.id.fold:
                    //通过setTag和getTag获取子项Item的view
                    holder = (ViewHolder)v.getTag();
                    if(list.get(position).isHide() == false){
                        list.get(position).setHide(true);
                        holder.linearLayout.setVisibility(View.VISIBLE);
                        holder.fold.setImageResource(R.drawable.fold);
                    }else{
                        list.get(position).setHide(false);
                        holder.linearLayout.setVisibility(View.GONE);
                        holder.fold.setImageResource(R.drawable.unfold);
                    }
                    break;
                    //任务详情按钮
                case R.id.task_detail:
                    taskId = (String)v.getTag(R.id.tag_first);
                    currentId = (String)v.getTag(R.id.tag_second);
                    intent = new Intent(context, TaskDetailActivity.class);
                    app.setTaskId(taskId);
                    app.setCurrentId(currentId);
                    app.setEnroll(true);
                    context.startActivity(intent);
                    break;
                    //打卡日历按钮
                case R.id.task_calendar:
                    currentId = (String)v.getTag();
                    intent = new Intent(context, CalendarActivity.class);
                    app.setCurrentId(currentId);
                    context.startActivity(intent);
                    break;
                    //分享按钮
                case R.id.task_share:
                    break;
                    //删除按钮
                case R.id.task_delete:
                    //通过setTag和getTag获取子项Item的view
                    holder = (ViewHolder)v.getTag(R.id.tag_holder);
                    taskId = (String)v.getTag(R.id.tag_first);
                    currentId = (String)v.getTag(R.id.tag_second);
                    position = (int)v.getTag(R.id.tag_position);

                    //创建选择对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("确认是否退出该任务");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //更新后台数据currentNumber和signs的值
                            Task task = new Task();
                            //已打卡才减少签到人数
                            if (holder.signStatus.getText().toString().equals("今日已打卡")){
                                changeArrays(holder,index);
                                task.setSigns(holder.signs);
                                Log.e("subtract",holder.signStatus.getText().toString());
                            }
                            task.increment("currentNumber",-1);
                            task.update(taskId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    final Task_User task_user = new Task_User();
                                    task_user.setObjectId(currentId);
                                    task_user.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {

                                        }
                                    });
                                    Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT).show();
                                    //在列表中删去该项
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "少年，恭喜你守住了初心", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //显示对话框
                    AlertDialog ad = builder.create();
                    ad.show();
                    break;
                default:
                    break;
            }
        }
    }

    //修改签到数组
    private void changeArrays(ViewHolder holder,int index){
        int signNumber = holder.signs.get(index).getSignNumber() - 1;
        holder.signs.get(index).setSignNumber(signNumber);
    }
}

