package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.honesty.Activity.UserDetailActivity;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.BitmapCache;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户详细信息的adapter
 * Created by Administrator on 2018/6/1 0001.
 */

public class UserDetailAdapter extends BaseAdapter {
    private Context context;
    private List<Task_User> list;
    private MyApp app;

    public UserDetailAdapter(Context context, List<Task_User> list) {
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
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_user_detail, null);
            //实例化对象
            holder.head = view.findViewById(R.id.avater);
            holder.username = view.findViewById(R.id.username);
            holder.time = view.findViewById(R.id.time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        holder.head.setTag(list.get(i).getParticipant().getAvater());
        //设置默认背景
        holder.head.setDefaultImageResId(R.drawable.nopicture);
        holder.head.setImageUrl(list.get(i).getParticipant().getAvater(), imageLoader);
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = list.get(i).getParticipant().getObjectId();
                app.setUserId(userId);
                Intent intent = new Intent(context, UserDetailActivity.class);
                context.startActivity(intent);
            }
        });

        holder.username.setText(list.get(i).getParticipant().getUsername());
        holder.time.setText("于 "+list.get(i).getParticipant().getCreatedAt()+" 加入本任务");
        return view;
    }
    class ViewHolder{
        NetworkImageView head;
        TextView username,time;
    }
}
