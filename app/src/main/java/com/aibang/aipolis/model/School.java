package com.aibang.aipolis.model;

import org.litepal.crud.DataSupport;

/**
 *学校 本地数据库 用litepal框架
 * Created by zcf on 2016/5/12.
 */
public class School extends DataSupport {

    /**
     * name : 新疆应用职业技术学院
     * city : 新疆维吾尔自治区
     */

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
