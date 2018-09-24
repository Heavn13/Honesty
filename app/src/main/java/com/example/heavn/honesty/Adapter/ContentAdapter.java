package com.example.heavn.honesty.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Activity.UserDetailActivity;
import com.example.heavn.honesty.Bean.Content;
import com.example.heavn.honesty.Bean.ContentComment;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.ImageDownloadTask;
import com.example.heavn.honesty.Util.MyApp;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 说说内容的adapter
 * Created by Administrator on 2018/6/1 0001.
 */

public class ContentAdapter extends BaseAdapter {
    private Context context;
    private List<Content> list;
    private List<ContentComment> comments = new ArrayList<>();
    private ViewHolder holder = null;
    private MyListener myListener = null;
    private MyItemListener myItemListener = null;
    private CommentListAdapter commentListAdapter = null;
    private MyApp app;
    private PopupWindow popupWindow = null;
    private Button pop_send;
    private EditText pop_edit;
    private String s_comment = "";

    public ContentAdapter(Context context, List<Content> list) {
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
            view = View.inflate(context, R.layout.item_content, null);
            holder = new ViewHolder();
            myListener = new MyListener(i);
            myItemListener = new MyItemListener(i,holder);
            //实例化对象
            holder.head = view.findViewById(R.id.head);
            holder.head.setTag(R.id.tag_first,i);
            holder.username = view.findViewById(R.id.username);
            holder.content = view.findViewById(R.id.content);
            holder.date = view.findViewById(R.id.date);
            holder.good = view.findViewById(R.id.image_good);
            holder.good.setOnClickListener(myListener);
            holder.good.setTag(R.id.tag_first,list.get(i).getObjectId());
            holder.good.setTag(R.id.tag_second,list.get(i).getCount());
            holder.comment = view.findViewById(R.id.image_comment);
            holder.comment.setTag(R.id.tag_holder,holder);
            holder.well_done = view.findViewById(R.id.well_done);
            holder.well_done.setTag(i);
            holder.commentList = view.findViewById(R.id.comment_lists);
            holder.commentList.setTag(i);
            holder.commentList.setTag(R.id.tag_first,list.get(i).getComment());
            holder.commentList.setOnItemClickListener(myItemListener);
            holder.edit_comment = view.findViewById(R.id.edit_comment);
            holder.edit_comment.setTag(R.id.tag_holder,holder);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ImageDownloadTask imgTask = new ImageDownloadTask();
        int hi = (int)holder.head.getTag(R.id.tag_first);
        if(list.get(i).getAuthor().getAvater() != null && hi == i)
            imgTask.execute(list.get(i).getAuthor().getAvater(),holder.head);
        holder.head.setOnClickListener(myListener);
        holder.head.setTag(list.get(i).getAuthor().getObjectId());
        holder.username.setText(list.get(i).getAuthor().getUsername());
        holder.content.setText(list.get(i).getContent());
        holder.date.setText(list.get(i).getCreatedAt());
        holder.comment.setOnClickListener(myListener);
        holder.comment.setTag(i);
        holder.edit_comment.setOnClickListener(myListener);
        holder.edit_comment.setTag(i);
        int position = (int)holder.commentList.getTag();
        if(list.get(i).getCount()>0 && position == i){
            holder.well_done.setText("有"+list.get(i).getCount()+"人觉得很赞");
        }
        final int index = (int)holder.commentList.getTag();

        if(list.get(i).getComment() != null && index == i){
            comments = list.get(i).getComment();
            commentListAdapter = new CommentListAdapter(context,comments);
            holder.commentList.setAdapter(commentListAdapter);
            setListViewHeightBasedOnChildren(holder.commentList);
            notifyDataSetChanged();
        }
        return view;
    }

    class ViewHolder{
        ImageView head;//头像
        TextView username;//用户名
        TextView content;//内容
        TextView date;//时间
        TextView well_done;//点赞状态
        ListView commentList;//评论列表
        ImageView good,comment;//点赞和评论按钮
        TextView edit_comment;//评论编辑框
    }

    //自定义监听器，包括查看用户详情，点赞评论等功能
    private class MyListener implements View.OnClickListener{
        int position;
        public MyListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.head:
                    String userId = (String)v.getTag();
                    app.setUserId(userId);
                    intent = new Intent(context, UserDetailActivity.class);
                    context.startActivity(intent);
                    break;
                case R.id.image_good:
                    String id = (String)v.getTag(R.id.tag_first);
                    int count = (int)v.getTag(R.id.tag_second);
                    Content item1 = (Content)getItem(position);
                    item1.setCount(++count);
                    //holder.well_done.setText("有"+(++count)+"人觉得很赞");
                    Content content = new Content();
                    content.increment("count");
                    content.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                            }else{
                            }
                        }
                    });
                    notifyDataSetChanged();
                    break;
                case R.id.image_comment:
                    int i = (int)v.getTag();
                    ViewHolder holder = (ViewHolder)v.getTag(R.id.tag_holder);
                    Content item = (Content)getItem(i);
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        initPopupWindowView(item,item.getAuthor().getUsername(),holder);
                        popupWindow.showAtLocation(v,Gravity.CENTER,v.getHeight(),v.getWidth());
                    }
                    break;
                case R.id.edit_comment:
                    int j = (int)v.getTag();
                    ViewHolder holder2 = (ViewHolder)v.getTag(R.id.tag_holder);
                    Content item2 = (Content)getItem(j);
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        initPopupWindowView(item2,item2.getAuthor().getUsername(),holder2);
                        popupWindow.showAtLocation(v,Gravity.CENTER,v.getHeight(),v.getWidth());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //点击每个评论的点击事件，弹出框
    private class MyItemListener implements AdapterView.OnItemClickListener{
        int position;
        ViewHolder holder;
        public MyItemListener(int position,ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
//            comments = (List<ContentComment>) holder.commentList.getTag(R.id.tag_first);
            Log.e("position",""+position);
            comments = list.get(position).getComment();
            //Toast.makeText(context,comments.get(i).getComments(),Toast.LENGTH_LONG).show();
            Content content = list.get(position);
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                initPopupWindowView(content,comments.get(i).getFromName(),holder);
                popupWindow.showAtLocation(view,Gravity.CENTER,view.getHeight(),view.getWidth());
            }
        }
    }

    //    弹出窗口
    public void initPopupWindowView(final Content item, final String toName,final ViewHolder holder) {
        // // 获取自定义布局文件pop.xml的视图
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popupwindow_edit_comment, null, false);
        // 创建PopupWindow实例,让它能够适应键盘的出现和消失,注意LinearLayout.LayoutParams.FILL_PARENT,130才能达到该效果
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,130,true);
        // 自定义view添加触摸事件，设置点击其他区域弹窗消失
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });

        pop_edit = customView.findViewById(R.id.pop_edit);
        //打开软键盘
        final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // 获取编辑框焦点
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        //软键盘不会挡着popupwindow
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置菜单显示的位置
        pop_edit.setFocusable(true);
        pop_edit.requestFocus();
        pop_edit.setHint("回复 "+toName+"：");

        pop_send = customView.findViewById(R.id.pop_send);
//        发表内容
        pop_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_comment = pop_edit.getText().toString();
                if( !s_comment.equals("")){
                    MyUser user = MyUser.getCurrentUser(MyUser.class);
                    final ContentComment contentComment = new ContentComment(user.getUsername(),toName,s_comment);
                    //当评论列表为空时设置第一个
                    if(item.getComment() == null ){
                        List<ContentComment> comments = new ArrayList<>();
                        comments.add(contentComment);
                        item.setComment(comments);
                        commentListAdapter = new CommentListAdapter(context,item.getComment());
                        holder.commentList.setAdapter(commentListAdapter);
                        commentListAdapter.notifyDataSetChanged();
                    }else{
                        item.getComment().add(contentComment);
                        commentListAdapter.notifyDataSetChanged();
                    }
//                    不能new出一个新的对象再添加，否则会出现点赞数重新变为0的情况
                    item.add("comment",contentComment);
                    item.update(item.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Toast.makeText(context,"发表成功",Toast.LENGTH_SHORT).show();
//                                必须使用item.getComment()方法获取到的comments对象添加，使其显示在对应的item中
//                                 如果直接使用comments添加对象，会出现内容更新后只会显示在最后一个item上的情况
                            }
                        }
                    });
                    popupWindow.dismiss();
                }else{
                    Toast.makeText(context,"请填写内容后发表",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //计算listView的定高
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
//        Log.e("height",""+comment_lists.size());
        for (int i = 0; i < comments.size(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
//            Log.e("height",""+listItem.getMeasuredHeight());
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (comments.size()+1));
        listView.setLayoutParams(params);
    }
}
