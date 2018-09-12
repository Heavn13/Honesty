package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.BitmapCache;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

public class UserRankAdapter extends BaseAdapter {
    private Context context;
    private List<Task_User> list;

    public UserRankAdapter(Context context, List<Task_User> list) {
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
            view = View.inflate(context, R.layout.item_user_rank, null);
            //实例化对象
            holder.rank = view.findViewById(R.id.rank);
            holder.head = view.findViewById(R.id.avater);
            holder.username = view.findViewById(R.id.username);
            holder.goal = view.findViewById(R.id.goal);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        if(i == 0){
            holder.rank.setImageResource(R.drawable.gold_cup);
        }else if(i == 1){
            holder.rank.setImageResource(R.drawable.silver_cup);
        } else if(i == 2){
            holder.rank.setImageResource(R.drawable.blonze_cup);
        }else if(i == 3){
            holder.rank.setImageResource(R.drawable.four);
        }else{
            holder.rank.setImageResource(R.drawable.five);
        }
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        holder.head.setTag(list.get(i).getParticipant().getAvater());
        //设置默认背景
        holder.head.setDefaultImageResId(R.drawable.nopicture_circle);
        holder.head.setImageUrl(list.get(i).getParticipant().getAvater(), imageLoader);

        holder.username.setText(list.get(i).getParticipant().getUsername());
        holder.goal.setText(""+list.get(i).getTotalSign());
        return view;
    }
    class ViewHolder{
        ImageView rank;
        NetworkImageView head;
        TextView username,goal;
    }

}
