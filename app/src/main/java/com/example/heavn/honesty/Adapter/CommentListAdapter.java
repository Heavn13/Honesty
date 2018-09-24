package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heavn.honesty.Bean.ContentComment;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.DateUtil;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户评论列表的adapter
 * Created by Administrator on 2018/6/1 0001.
 */
public class CommentListAdapter extends BaseAdapter {
    private Context context;
    private List<ContentComment> list;

    public CommentListAdapter(Context context, List<ContentComment> list) {
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
            view = View.inflate(context, R.layout.item_comment_list, null);
            //实例化对象
            holder.fromName = view.findViewById(R.id.comment_from);
            holder.toName = view.findViewById(R.id.comment_to);
            holder.content = view.findViewById(R.id.comment_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        holder.fromName.setText(list.get(i).getFromName());
        holder.toName.setText(list.get(i).getToName());
        holder.content.setText(list.get(i).getComments());

        return view;
    }
    class ViewHolder{
        TextView fromName;//来自
        TextView toName;//给予
        TextView content;//内容
    }

}
