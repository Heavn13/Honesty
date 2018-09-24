package com.example.heavn.honesty.Bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 说说内容
 * Created by Administrator on 2018/6/1 0001.
 */

public class Content extends BmobObject {
    private String content;
    private MyUser author;
    private int count;
    private List<ContentComment> comment;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ContentComment> getComment() {
        return comment;
    }

    public void setComment(List<ContentComment> comment) {
        this.comment = comment;
    }
}
