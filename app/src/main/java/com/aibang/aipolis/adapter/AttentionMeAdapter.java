package com.aibang.aipolis.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import com.aibang.aipolis.R;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;
import com.aibang.aipolis.bean.Guanzhu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * 关注我的人 适配器
 * Created by zcf on 2016/5/2.
 */
public class AttentionMeAdapter extends CommonAdapter<Guanzhu> {

    private DisplayImageOptions options;

    public AttentionMeAdapter(Context context, List<Guanzhu> datas, int layoutId) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
    }

    @Override
    public void convert(ViewHolder holder, Guanzhu guanzhu) {
        holder.setText(R.id.id_user_name, guanzhu.getGuanzhu().getNickName());
        if (guanzhu.getGuanzhu().getAutographUrl() != null) {
            holder.displayImage(R.id.id_user_head_img, guanzhu.getGuanzhu().getAutographUrl(), options);
        }

    }
}
