package com.example.heavn.honesty.Bean;

/**
 * 设置
 * Created by Administrator on 2018/6/1 0001.
 */

public class Setting {
    private int image,enter;
    private String title;

    public Setting(int image, int enter, String title) {
        this.image = image;
        this.enter = enter;
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getEnter() {
        return enter;
    }

    public void setEnter(int enter) {
        this.enter = enter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
