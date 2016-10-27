package com.aibang.aipolis.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by zcf on 2016/5/30.
 */
public class Interest extends BmobObject {
    private String name;//兴趣名称
    private Integer sum;//选择兴趣的人数


    public Interest() {

    }

    public Interest(String name, Integer sum) {
        this.name = name;
        this.sum = sum;
    }


    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public Integer getSum() {
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interest interest = (Interest) o;

        if (name != null ? !name.equals(interest.name) : interest.name != null) return false;
        return true;

    }

}
