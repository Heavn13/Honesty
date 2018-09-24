package com.example.heavn.honesty.Bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 用户的任务，即我的任务
 * Created by Administrator on 2018/6/1 0001.
 */

public class Task_User extends BmobObject {
    private Task task;
    private MyUser participant;
    private Integer totalSign;
    private boolean myComplete;
    private List<SignUp> signUps;
    private boolean hide = false;

    public Integer getTotalSign() {
        return totalSign;
    }

    public void setTotalSign(Integer totalSign) {
        this.totalSign = totalSign;
    }

    public boolean isMyComplete() {
        return myComplete;
    }

    public void setMyComplete(boolean myComplete) {
        this.myComplete = myComplete;
    }

    public List<SignUp> getSignUps() {
        return signUps;
    }

    public void setSignUps(List<SignUp> signUps) {
        this.signUps = signUps;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public MyUser getParticipant() {
        return participant;
    }

    public void setParticipant(MyUser participant) {
        this.participant = participant;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }
}
