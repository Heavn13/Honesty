package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.honesty.Bean.Setting;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.BitmapCache;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

/**
 * 系统设置的adapter
 * Created by Administrator on 2018/6/1 0001.
 */

public class SettingAdapter extends BaseAdapter {
    private Context context;
    private List<Setting> list;

    public SettingAdapter(Context context, List<Setting> list) {
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
            view = View.inflate(context, R.layout.item_setting, null);
            //实例化对象
            holder.image = view.findViewById(R.id.image);
            holder.enter = view.findViewById(R.id.enter);
            holder.title = view.findViewById(R.id.title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        holder.image.setImageResource(list.get(i).getImage());
        holder.enter.setImageResource(list.get(i).getEnter());
        holder.title.setText(list.get(i).getTitle());

        return view;
    }
    class ViewHolder{
        ImageView image,enter;
        TextView title;
    }
}
