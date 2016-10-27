package com.aibang.aipolis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.Life;
import com.aibang.aipolis.utils.ComUtils;
import com.aibang.aipolis.utils.TransitionTime;
import com.lzy.ninegrid.ImageInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 互助适配器
 * Created by zcf on 2016/5/1.
 */
public class LifeAdapter extends CommonAdapter<Life> {
    private DisplayImageOptions options;

    public LifeAdapter(Context context, List<Life> datas, int layoutId) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheOnDisk(true)
                .cacheInMemory(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
    }


    @Override
    public void convert(final ViewHolder holder, final Life life) {
        //这俩句计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), life.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(life.getCreatedAt())));
        String headUrl = life.getUser().getAutographUrl();//头像地址


        holder
                .setText(R.id.id_user_name, life.getUser().getNickName())
                .setText(R.id.id_life_time, s)
                .setText(R.id.id_user_school, life.getUser().getSchool())
                .setText(R.id.id_like_num, life.getZanNumber() + "")
                .setText(R.id.id_comment_num, life.getCommentNumber() + "");


        //设置浏览量

        if (!(String.valueOf(life.getLookSum()).equals("null")))
            holder.setText(R.id.id_look_sum, life.getLookSum() + "");

        //防止复用导致重复
        holder.setCompoundDrawablesWithIntrinsicBounds
                (R.id.id_like_num, R.drawable.ic_love_nomal);



        //如果没有文字 就隐藏
        if (TextUtils.isEmpty(life.getContent())) {
            holder.setVisible(R.id.id_life_content,View.GONE);
        } else {
            holder.setVisible(R.id.id_life_content,View.VISIBLE);
            holder.setText(R.id.id_life_content, life.getContent());
        }

        //设置头像
        if (headUrl != null) {
            holder.displayImage(R.id.id_user_head_img, headUrl, options);
            holder.setTag(R.id.id_user_head_img, holder.getPosition());
        } else if (life.getUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_female);
        }else {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_male);
        }
        //设置性别
        if (life.getUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_female);
        } else {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_male);
        }

        //有图片
        if (!(life.getThumnailTaskURL().equals("No") || life.getThumnailTaskURL().equals("NO"))) {
            ArrayList<ImageInfo> urls = new ArrayList<>();//图片地址
            ComUtils.splitImageUrls(urls, life.getThumnailTaskURL());
            holder.setVisible(R.id.id_nineGrid, View.VISIBLE);
            holder.setNineAdapter(R.id.id_nineGrid, urls);//设置图片
        } else {
            //没有图片
            holder.setVisible(R.id.id_nineGrid, View.GONE);
        }


        //头像点击事件
        holder.setOnClickListener(R.id.id_user_head_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", life.getUser());
                mContext.startActivity(intent);
            }
        });


        //点赞监听事件
        holder.setOnClickListener(R.id.id_like_num, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = life.getZanNumber() + 1;
                holder.setText(R.id.id_like_num, num + "");
                holder.setCompoundDrawablesWithIntrinsicBounds
                        (R.id.id_like_num, R.drawable.ic_love_selected);
                Life l = new Life();
                l.setZanNumber(num);
                l.update(mContext, life.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });


            }
        });


    }


}

