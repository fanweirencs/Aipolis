package com.aibang.aipolis.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.ShouyeArticle;
import com.aibang.aipolis.utils.TransitionTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 首页的Adapter 运用万能适配器 只要复写构造器和covert方法即可
 * Created by zcf on 2016/4/28.
 */
public class IndexAdapter extends CommonAdapter<ShouyeArticle> {

    private DisplayImageOptions options;

    public IndexAdapter(Context context, List<ShouyeArticle> datas, int layoutId) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
    }

    @Override
    public void convert(final ViewHolder holder, final ShouyeArticle shouyeArticle) {
        String title = shouyeArticle.getTitle();
        //计算时间差
        TransitionTime time = new TransitionTime(System.currentTimeMillis(), shouyeArticle.getCreatedAt());
        String s = time.twoDateDistance(String.valueOf(time.getTimeMills(shouyeArticle.getCreatedAt())));

        holder.setText(R.id.shouye01_title, title)
                .setText(R.id.shouye01_nickName, shouyeArticle.getZuozhe().getNickName())
                .setText(R.id.shouye01_time, s)
                .setText(R.id.shouye01_commentNum, shouyeArticle.getCommentNum() + "")
                .setText(R.id.shouye01_zanNum, shouyeArticle.getZanNum() + "")
//                .setLayoutParams(R.id.shoueye01_img, 350, 200)
                .setTag(R.id.shoueye01_img, shouyeArticle.getImgUrl())
                .setImageResource(R.id.shoueye01_img, R.drawable.shouyejiazai)
                .displayImage(R.id.shoueye01_img, shouyeArticle.getImgUrl(), options)
                .setTag(R.id.shouye_touxiang, holder.getPosition())
        ;
        //防止复用导致重复
        holder.setCompoundDrawablesWithIntrinsicBounds
                (R.id.shouye01_zanNum, R.drawable.ic_love_nomal);

        String uerHead = shouyeArticle.getZuozhe().getAutographUrl();
        if (uerHead != null) {
            holder.displayImage(R.id.shouye_touxiang, uerHead, options);
        } else if (shouyeArticle.getZuozhe().getGender().equals("female")) {
            holder.setImageResource(R.id.shouye_touxiang, R.mipmap.default_head_female);
        } else {
            holder.setImageResource(R.id.shouye_touxiang, R.mipmap.default_head_male);
        }


        //点赞监听事件
        holder.setOnClickListener(R.id.shouye01_zanNum, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = shouyeArticle.getZanNum() + 1;
                holder.setText(R.id.shouye01_zanNum, num + "");
                holder.setCompoundDrawablesWithIntrinsicBounds
                        (R.id.shouye01_zanNum, R.drawable.ic_love_selected);
                ShouyeArticle shouye = new ShouyeArticle();
                shouye.setZanNum(num);
                shouye.update(mContext, shouyeArticle.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub
                    }
                });


            }
        });

        //头像点击事件
        holder.setOnClickListener(R.id.shouye_touxiang, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalOtherHomepageActivity.class);
                intent.putExtra("user", shouyeArticle.getZuozhe());
                mContext.startActivity(intent);
            }
        });


    }

}
