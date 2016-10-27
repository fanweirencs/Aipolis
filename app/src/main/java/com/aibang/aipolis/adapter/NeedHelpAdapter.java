package com.aibang.aipolis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.Help;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.utils.TransitionTime;
import com.aibang.aipolis.view.ConfirmDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 求助适配器
 * Created by zcf on 2016/5/1.
 */
public class NeedHelpAdapter extends CommonAdapter<Help> {
    private DisplayImageOptions options;

    public NeedHelpAdapter(Context context, List<Help> datas, int layoutId) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
    }

    @Override
    public void convert(final ViewHolder holder, final Help help) {
        //计算时间差
        final TransitionTime time = new TransitionTime(System.currentTimeMillis(), help.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(help.getCreatedAt())));
        holder
                .setText(R.id.id_help_title, help.getTitle())
                .setText(R.id.id_help_name, "by:" + help.getUser().getNickName())
                .setText(R.id.id_help_time, s)
                .setText(R.id.id_help_school, help.getUser().getSchool())
                .setText(R.id.id_help_content, help.getContent())
                .setText(R.id.id_help_num, help.getCommentNumber() + "");

        //如果 已经完成 就改变状态
        if (help.getIsDone()) {
            holder.setImageResource(R.id.id_img_isDone, R.drawable.help_done);
            holder.setText(R.id.id_text_isDone, mContext.getString(R.string.already_finish));
        }else {
            holder.setImageResource(R.id.id_img_isDone, R.drawable.help_not);
            holder.setText(R.id.id_text_isDone, mContext.getString(R.string.not_finish));
        }

        //备注
        if (TextUtils.isEmpty(help.getBeizhu())){
            holder.setVisible(R.id.id_remark,View.GONE);
        }else {
            holder.setVisible(R.id.id_remark,View.VISIBLE);
            holder.setText(R.id.id_remark,help.getBeizhu());
        }

        holder.setTag(R.id.id_help_user_head, holder.getPosition()); //设置tag 以免图片混乱
        if (help.getUser().getAutographUrl() != null) {
            holder.displayImage(R.id.id_help_user_head, help.getUser().getAutographUrl(), options);
        } else if (help.getUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_help_user_head, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.id_help_user_head, R.mipmap.default_head_male);
        }

        //设置性别
        if (help.getUser().getGender().equals("female")) {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_female);
        }else {
            holder.setImageResource(R.id.id_user_sex, R.drawable.ic_sex_male);
        }



        //头像点击事件
        holder.setOnClickListener(R.id.id_help_user_head, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", help.getUser());
                mContext.startActivity(intent);
            }
        });


        /**
         * 标记已完成 或者未完成
         */
        holder.setOnClickListener(R.id.right, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(mContext,User.class);
                if (user!=null){
                    if (user.getObjectId().equals(help.getUser().getObjectId())){
                        if (help.getIsDone()) {
                            Toast.makeText(mContext, "已完成", Toast.LENGTH_SHORT).show();
                        }else {
                            //如果未完成 就标记层已经完成
                            ConfirmDialog dialog = new ConfirmDialog(mContext);
                            dialog.setTopTitle("Tips");
                            dialog.setOkText("确认");
                            dialog.setCancelText("取消");
                            dialog.setContentText("确定要标记成已完成吗");
                            dialog.show();
                            dialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                                @Override
                                public void onOKClick() {
                                    holder.setImageResource(R.id.id_img_isDone, R.drawable.help_done);
                                    holder.setText(R.id.id_text_isDone, mContext.getString(R.string.already_finish));
                                    updateIsDone(true,help.getObjectId());

                                }

                                @Override
                                public void onCancelClick() {

                                }
                            });
                        }
                    }
                }


            }
        });


    }



    /**
     * 更新是否完成
     */
    private void updateIsDone(boolean isDone,String objecctId){
        Help help = new Help();
        help.setIsDone(isDone);
        help.update(mContext, objecctId, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.i("bmob","更新成功：");
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Log.i("bmob","更新失败："+msg);
            }
        });
    }

}
