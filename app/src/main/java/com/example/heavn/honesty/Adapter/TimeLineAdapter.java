package com.example.heavn.honesty.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.BitmapCache;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

/**
 * 打卡时间线的adapter
 * Created by Administrator on 2018/6/1 0001.
 */

public class TimeLineAdapter extends BaseAdapter {
    private Context context;
    private List<SignUp> list;
    private ListView listView;
    private Activity activity;

    public TimeLineAdapter(Context context, List<SignUp> list,ListView listView,Activity activity) {
        this.context = context;
        this.list = list;
        this.listView = listView;
        this.activity = activity;
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
            view = View.inflate(context, R.layout.item_my_time_line, null);
            //实例化对象
            holder.date = view.findViewById(R.id.date);
            holder.location = view.findViewById(R.id.location);
            holder.proveImage = view.findViewById(R.id.proveImage);
            view.setTag(holder);
        } else {
        holder = (ViewHolder) view.getTag();
    }
        //给控件赋值
        holder.location.setText(list.get(i).getLocation().toString());
        holder.date.setText(list.get(i).getDate());
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        holder.proveImage.setTag(list.get(i).getProveImage());
        //设置默认背景
        holder.proveImage.setDefaultImageResId(R.drawable.nopicture);
        holder.proveImage.setImageUrl(list.get(i).getProveImage(), imageLoader);
        return view;
    }
    class ViewHolder{
        TextView date;
        TextView location;
        NetworkImageView proveImage;
    }
}
