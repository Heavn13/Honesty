package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.BitmapCache;
import com.example.heavn.honesty.Util.MyApp;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<Task_User> list;
    private OnItemClickListener mOnItemClickListener;
    public UserAdapter(Context context, List<Task_User> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        holder.head.setTag(list.get(position).getParticipant().getAvater());
        holder.head.setTag(R.id.tag_first,list.get(position).getParticipant().getObjectId());
        //设置默认背景
        holder.head.setDefaultImageResId(R.drawable.nopicture);
        holder.head.setImageUrl(list.get(position).getParticipant().getAvater(), imageLoader);

        holder.userName.setText(list.get(position).getParticipant().getUsername());
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });

        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;//用户名
        NetworkImageView head;//头像

        public  ViewHolder(View view){
            super(view);
            //实例化对象
            userName =  view.findViewById(R.id.userName);
            head = view.findViewById(R.id.head);
        }

    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener = onItemClickListener;
    }
}
