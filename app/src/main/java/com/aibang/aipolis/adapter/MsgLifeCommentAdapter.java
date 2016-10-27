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
import com.aibang.aipolis.bean.ShenghuoComment;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.utils.TransitionTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * 消息的Adapter 运用万能适配器 只要复写构造器和covert方法即可
 * Created by zcf on 2016/4/28.
 */
public class MsgLifeCommentAdapter extends CommonAdapter<ShenghuoComment> {

    private DisplayImageOptions options;
    private User user;

    public MsgLifeCommentAdapter(Context context, List<ShenghuoComment> datas, int layoutId, User user) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        this.user = user;
    }

    @Override
    public void convert(final ViewHolder holder, final ShenghuoComment comment) {

        //计算时间差
        //计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), comment.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(comment.getCreatedAt())));

        holder.setText(R.id.id_comment_time, s)
                .setText(R.id.id_comment_name, comment.getCommentUser().getNickName());

        if (comment.getBeiCommentUser() != user) {//评论
            holder.setText(R.id.id_life_content,
                    mContext.getString(R.string.reply_your));
        }else {//回复
            if (TextUtils.isEmpty(comment.getShenghuo().getContent())){
                holder.setText(R.id.id_life_content,
                        mContext.getString(R.string.comment_your)+"生活照片");
            }else {
                holder.setText(R.id.id_life_content,
                        mContext.getString(R.string.comment_your)+comment.getShenghuo().getContent());
            }

        }


        //设置评论
        holder.setText(R.id.id_content, comment.getComment());

        String uerHead = comment.getCommentUser().getAutographUrl();
        //设置头像
        if (uerHead != null) {
            holder.displayImage(R.id.id_user_head_img, uerHead, options);
        } else if (comment.getCommentUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_male);
        }


        holder.setOnClickListener(R.id.id_user_head_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", comment.getCommentUser());
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
