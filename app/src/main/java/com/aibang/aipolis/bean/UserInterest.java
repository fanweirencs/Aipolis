package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

/**
 * 用户选择了那些兴趣
 * Created by zcf on 2016/5/30.
 */
public class UserInterest extends BmobObject {
    private User user;
    private Interest interest;

    public UserInterest() {

    }

    public UserInterest(User user, Interest i) {
        this.user = user;
        this.interest = i;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }

    public User getUser() {

        return user;
    }

    public Interest getInterest() {
        return interest;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInterest that = (UserInterest) o;
        return interest.getName().equals(that.interest.getName());

    }

}
