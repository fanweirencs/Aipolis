package com.aibang.aipolis.event;

/**
 * 更新头像
 * Created by zcf on 2016/5/19.
 */
public class UpdateHeadEvent {
    String headUrl;

    public UpdateHeadEvent(String headUrl) {
        this.headUrl = headUrl;

    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getHeadUrl() {

        return headUrl;
    }
}
