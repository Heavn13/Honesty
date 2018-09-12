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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Activity.ChangeTaskActivity;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageTaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> list;
    private MyListener myListener = null;
    private String taskId,currentId;
    private MyApp app;

    public ManageTaskAdapter(Context context, List<Task> list) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_manage_task, null);
            myListener = new MyListener(i);
            //实例化对象
            holder.type = view.findViewById(R.id.type);
            holder.taskName = view.findViewById(R.id.task_name);
            holder.left = view.findViewById(R.id.previous);
            holder.signNumber = view.findViewById(R.id.signNumber);
            holder.currentNumber = view.findViewById(R.id.currentNumber);
            holder.totalNumber = view.findViewById(R.id.totalNumber);
            holder.end = view.findViewById(R.id.end);
            holder.modify = view.findViewById(R.id.modify);
            holder.delete = view.findViewById(R.id.delete);
            holder.label = view.findViewById(R.id.label);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
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

        //设置按钮的监听事件
        holder.modify.setOnClickListener(myListener);
        holder.modify.setTag(list.get(i).getObjectId());
        holder.delete.setOnClickListener(myListener);
        holder.delete.setTag(list.get(i).getObjectId());

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
            holder.end.setImageResource(R.drawable.running);
            holder.end.setVisibility(View.VISIBLE);
        }
        //任务完成时给控件赋值
        else{
            //任务正在进行中的图标
            holder.end.setImageResource(R.drawable.end);
            holder.end.setVisibility(View.VISIBLE);
            holder.left.setText("0");
            holder.signNumber.setText("0");
            holder.end.setVisibility(View.VISIBLE);
        }
        return view;
    }
    class ViewHolder{
        LinearLayout label;
        ImageView type;//标签颜色
        TextView taskName;//任务名称
        TextView left;//任务剩余天数
        TextView signNumber;//今日打卡人数
        TextView currentNumber;//当前任务报名人数
        TextView totalNumber;//任务最大人数
        ImageView end;//任务结束的图标
        Button modify,delete;//修改和删除的按钮
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
                case R.id.modify:
                    taskId = (String)v.getTag();
                    intent = new Intent(context, ChangeTaskActivity.class);
                    app.setTaskId(taskId);
                    context.startActivity(intent);
                    break;
                case R.id.delete:
                    taskId = (String)v.getTag();
                    //创建选择对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("确认是否解散该任务?(一旦解散，所有参与者都将被迫退出该任务，请谨慎操作)");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BmobQuery<Task> query = new BmobQuery<Task>();
                            query.getObject(taskId, new QueryListener<Task>() {
                                @Override
                                public void done(Task object, BmobException e) {
                                    if(e == null){
                                        //查询参与者
                                        BmobQuery<Task_User> q1 = new BmobQuery<>();
                                        q1.addWhereEqualTo("task",object);
                                        q1.include("task,participant");
                                        q1.findObjects(new FindListener<Task_User>() {
                                            @Override
                                            public void done(List<Task_User> lists, BmobException e) {
                                                //如果该任务没有参与者
                                                if(lists.size() != 0){
                                                    //批量删除
                                                    List<BmobObject> task_user_list = new ArrayList<>();
                                                    for(int i=0;i<lists.size();i++){
                                                        Task_User task_user = new Task_User();
                                                        task_user.setObjectId(lists.get(i).getObjectId());
                                                        task_user_list.add(task_user);
                                                    }
                                                    new BmobBatch().deleteBatch(task_user_list).doBatch(new QueryListListener<BatchResult>() {

                                                        @Override
                                                        public void done(List<BatchResult> o, BmobException e) {
                                                            if(e==null){
                                                                for(int i=0;i<o.size();i++){
                                                                    BatchResult result = o.get(i);
                                                                    BmobException ex = result.getError();
                                                                    if(ex==null){
                                                                        Log.e("delete","所有参与用户解散成功");
                                                                    }
                                                                }
                                                                //删除该任务
                                                                final Task task = new Task();
                                                                task.setObjectId(taskId);
                                                                task.delete(new UpdateListener() {
                                                                    @Override
                                                                    public void done(BmobException e) {
                                                                        if(e==null){
                                                                            Toast.makeText(context, "任务解散成功", Toast.LENGTH_SHORT).show();
                                                                            list.remove(position);
                                                                            notifyDataSetChanged();
                                                                        }else{
                                                                            Toast.makeText(context, "任务解散失败", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }else{
                                                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                                                Log.e("delete","所有参与用户解散失败");
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    //删除该任务
                                                    final Task task = new Task();
                                                    task.setObjectId(taskId);
                                                    task.delete(new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if(e==null){
                                                                Toast.makeText(context, "任务解散成功", Toast.LENGTH_SHORT).show();
                                                                list.remove(position);
                                                                notifyDataSetChanged();
                                                            }else{
                                                                Toast.makeText(context, "任务解散失败", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }else{
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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


}
