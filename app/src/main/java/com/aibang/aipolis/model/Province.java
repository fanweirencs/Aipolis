package com.aibang.aipolis.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 省份  本地数据库 用litepal框架
 * Created by zcf on 2016/5/13.
 */
public class Province extends DataSupport {
    private String name;
    private List<School> schools = new ArrayList<>();
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

    public List<School> getSchools() {

        return schools;
    }
    public List<School> getSchool(int id) {
        return DataSupport.where("news_id = ?", String.valueOf(id)).find(School.class);
    }
}
