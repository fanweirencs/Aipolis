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
import com.aibang.aipolis.bean.HelpComment;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.utils.TransitionTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * 互助 评论 适配器
 * Created by zcf on 2016/5/10.
 */
public class HelpCommentAdapter extends CommonAdapter<HelpComment> {
    private DisplayImageOptions options;
    private EditText commentEt;//评论的
    private User beiCommentUser = null;//被评论者

    public void setBeiCommentUser(User beiCommentUser) {
        this.beiCommentUser = beiCommentUser;
    }

    public User getBeiCommentUser() {

        return beiCommentUser;
    }

    public HelpCommentAdapter(Context context, List<HelpComment> datas, int layoutId, EditText editText) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        commentEt = editText;
    }

    @Override
    public void convert(ViewHolder holder, final HelpComment helpComment) {
        //计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), helpComment.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(helpComment.getCreatedAt())));

        holder.setText(R.id.id_user_name, helpComment.getCommentUser().getNickName())
                .setText(R.id.id_time, s);

        //设置评论
        //如果是回复 就显示回复人出来
        if (helpComment.getBeiCommentUser() != null) {
            holder.setText(R.id.id_content, mContext.getString(R.string.empty_text));
            //加添一段html
            String html = "<font color='#0385FF'>" + helpComment.getBeiCommentUser().getNickName() +": "+ "</font>";
            holder.append(R.id.id_content, mContext.getString(R.string.reply));
            holder.append(R.id.id_content, Html.fromHtml(html));
            holder.append(R.id.id_content, helpComment.getComment());
        } else {
            holder.setText(R.id.id_content, helpComment.getComment());

        }


        String uerHead = helpComment.getCommentUser().getAutographUrl();

        //设置头像
        if (uerHead != null) {
            holder.displayImage(R.id.id_user_head_img, uerHead, options);
        } else if (helpComment.getCommentUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.id_user_head_img, R.mipmap.default_head_male);
        }

        //设置性别
        if (helpComment.getCommentUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_female);
        } else if (helpComment.getCommentUser().getGender().equals("male")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_male);
        }


        //监听事件
        holder.setOnClickListener(R.id.id_user_head_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", helpComment.getCommentUser());
                mContext.startActivity(intent);
            }
        });

        holder.setOnClickListener(R.id.id_itemCommentLy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                commentEt.setHint(" 回复" + helpComment.getCommentUser().getNickName());
                beiCommentUser = helpComment.getCommentUser();
            }
        });
    }
}
