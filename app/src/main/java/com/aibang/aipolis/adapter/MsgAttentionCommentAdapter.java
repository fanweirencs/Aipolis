package com.aibang.aipolis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.Guanzhu;
import com.aibang.aipolis.utils.TransitionTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * 消息的Adapter 运用万能适配器 只要复写构造器和covert方法即可
 * Created by zcf on 2016/5/16.
 */
public class MsgAttentionCommentAdapter extends CommonAdapter<Guanzhu> {

    private DisplayImageOptions options;

    public MsgAttentionCommentAdapter(Context context, List<Guanzhu> datas, int layoutId) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();

    }

    @Override
    public void convert(final ViewHolder holder, final Guanzhu comment) {

        //计算时间差
        //计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), comment.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(comment.getCreatedAt())));

        holder.setText(R.id.id_time, s);

        holder.setText(R.id.id_content, mContext.getString(R.string.empty_text));
        //加添一段html
        String html = "<font color='#0385FF'>" + comment.getGuanzhu().getNickName() + "  "+"</font>";
        holder.append(R.id.id_content, Html.fromHtml(html));
        holder.append(R.id.id_content, mContext.getString(R.string.attention_you));

        //设置评论

        String uerHead = comment.getGuanzhu().getAutographUrl();
        //设置头像
        if (uerHead != null) {
            holder.displayImage(R.id.id_user_head_img, uerHead, options);
        } else if (comment.getGuanzhu().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_male);
        }


        holder.setOnClickListener(R.id.id_user_head_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", comment.getGuanzhu());
                mContext.startActivity(intent);
            }
        });

        //设置性别
//        if (comment.getCommentUser().getGender().equals("female")) {
//            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_female);
//        } else if (comment.getCommentUser().getGender().equals("male")) {
//            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_male);
//        }


    }

}
