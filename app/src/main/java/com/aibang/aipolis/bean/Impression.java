package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

/**
 * 印象
 * Created by zcf on 2016/6/4.
 */
public class Impression extends BmobObject{
    private User impress;
    private User beiImpress;
    private String content;
    private Boolean isRead;

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Boolean getRead() {

        return isRead;
    }

    public void setImpress(User impress) {
        this.impress = impress;
    }

    public void setBeiImpress(User beiImpress) {
        this.beiImpress = beiImpress;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getImpress() {

        return impress;
    }

    public User getBeiImpress() {
        return beiImpress;
    }

    public String getContent() {
        return content;
    }
}
