package com.example.heavn.honesty.Bean;

/**
 * 说说评论
 * Created by Administrator on 2018/6/1 0001.
 */

public class ContentComment {
    private String fromName,toName,comments;

    public ContentComment(String fromName, String toName,String comments) {
        this.fromName = fromName;
        this.toName = toName;
        this.comments = comments;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
