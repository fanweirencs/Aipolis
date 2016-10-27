package com.aibang.aipolis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.EditText;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.ShenghuoComment;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.utils.TransitionTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 生活 评论 适配器
 * Created by zcf on 2016/5/10.
 */
public class LifeCommentAdapter extends CommonAdapter<ShenghuoComment> {
    private DisplayImageOptions options;
    private EditText commentEt;//评论的
    private User beiCommentUser = null;//被评论者

    private User mySelf;

    public void setBeiCommentUser(User beiCommentUser) {
        this.beiCommentUser = beiCommentUser;
    }

    public User getBeiCommentUser() {

        return beiCommentUser;
    }

    public LifeCommentAdapter(Context context, List<ShenghuoComment> datas, int layoutId, EditText editText) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        commentEt = editText;
        mySelf = BmobUser.getCurrentUser(context,User.class);
    }

    @Override
    public void convert(ViewHolder holder, final ShenghuoComment shenghuoComment) {
        //计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), shenghuoComment.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(shenghuoComment.getCreatedAt())));

        holder.setText(R.id.id_time, s)
        .setText(R.id.id_user_name,shenghuoComment.getCommentUser().getNickName());
        //设置评论
        //如果是回复 就显示回复人出来
        if (shenghuoComment.getBeiCommentUser() != null) {
            holder.setText(R.id.id_content, mContext.getString(R.string.empty_text));
            //加添一段html
            String html = "<font color='#0385FF'>" + shenghuoComment.getBeiCommentUser().getNickName() + ": "+"</font>";
            holder.append(R.id.id_content, mContext.getString(R.string.reply));
            holder.append(R.id.id_content, Html.fromHtml(html));
            holder.append(R.id.id_content, shenghuoComment.getComment());
        } else {
            holder.setText(R.id.id_content, shenghuoComment.getComment());

        }


        String uerHead = shenghuoComment.getCommentUser().getAutographUrl();

        //设置头像
        if (uerHead != null) {
            holder.displayImage(R.id.id_user_head_img, uerHead, options);
        } else if (shenghuoComment.getCommentUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_male);
        }

        //设置性别
        if (shenghuoComment.getCommentUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_female);
        } else if (shenghuoComment.getCommentUser().getGender().equals("male")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_male);
        }

        //监听事件

        holder.setOnClickListener(R.id.id_user_head_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", shenghuoComment.getCommentUser());
                mContext.startActivity(intent);
            }
        });

        holder.setOnClickListener(R.id.id_itemCommentLy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                commentEt.setHint(" 回复" + shenghuoComment.getCommentUser().getNickName());
                beiCommentUser = shenghuoComment.getCommentUser();
            }
        });
    }
}
