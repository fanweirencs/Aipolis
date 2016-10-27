package com.aibang.aipolis.adapter;

import android.content.Context;

import com.aibang.aipolis.R;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.Interest;
import com.aibang.aipolis.bean.UserInterest;

import java.util.List;

/**
 * 兴趣页面
 * Created by zcf on 2016/5/30.
 */
public class InterestGridViewAdapter extends CommonAdapter<Interest> {

    private int[] imgList = {
            R.drawable.interest_sports, R.drawable.interest_music, R.drawable.interest_movie,
            R.drawable.interest_cartoon, R.drawable.interest_game, R.drawable.interest_design,
            R.drawable.interest_board_game, R.drawable.interest_travel, R.drawable.interest_eat,
            R.drawable.interest_photo, R.drawable.interest_read, R.drawable.interest_part_time_job};
    private String[] nameList = {
            "运动", "音乐", "电影", "动漫", "游戏", "设计"
            , "桌游", "旅行", "吃货", "摄影", "读书", "兼职"};
    private List<UserInterest> selectList;

    public InterestGridViewAdapter(Context context, List<Interest> datas, List<UserInterest> selectList, int layoutId) {
        super(context, datas, layoutId);
        this.selectList = selectList;
    }

    @Override
    public void convert(ViewHolder holder, Interest interest) {
        holder
                .setImageResource(R.id.id_bg, imgList[holder.getPosition()])
//                .setText(R.id.id_style, nameList[holder.getPosition()])
                .setText(R.id.id_style, interest.getName())
                .setText(R.id.id_people_number, "共有" + interest.getSum() + "人选择")
        ;
        if (selectList.size() > 0) {
            for (UserInterest userInterest : selectList){
                if (interest.getObjectId().equals(userInterest.getInterest().getObjectId()))
                    holder.setImageResource(R.id.id_select_img, R.drawable.interest_selected);
            }
        }
    }

}
