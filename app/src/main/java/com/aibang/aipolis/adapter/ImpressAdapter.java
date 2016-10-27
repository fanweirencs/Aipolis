package com.aibang.aipolis.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.Impression;
import com.aibang.aipolis.utils.TransitionTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * 消息的Adapter 运用万能适配器 只要复写构造器和covert方法即可
 * Created by zcf on 2016/6/4.
 */
public class ImpressAdapter extends CommonAdapter<Impression> {

    private DisplayImageOptions options;
    private Activity activity;
    public ImpressAdapter(Activity activity, List<Impression> datas, int layoutId) {
        super(activity, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        this.activity = activity;
    }

    @Override
    public void convert(final ViewHolder holder, final Impression impression) {

        //计算时间差
        //计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), impression.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(impression.getCreatedAt())));

        holder
                .setText(R.id.id_time, s)
                .setText(R.id.id_nick_name, impression.getImpress().getNickName())
        ;


        //设置内容
        holder.setText(R.id.id_content, impression.getContent());

        String uerHead = impression.getImpress().getAutographUrl();
        //设置头像
        if (uerHead != null) {
            holder.displayImage(R.id.id_user_head_img, uerHead, options);
        } else if (impression.getImpress().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_male);
        }


        holder.setOnClickListener(R.id.id_user_head_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", impression.getImpress());
                mContext.startActivity(intent);
                activity.finish();

            }
        });

        //设置性别
        if (impression.getImpress().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_female);
        } else if (impression.getImpress().getGender().equals("male")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_male);
        }


    }

}
