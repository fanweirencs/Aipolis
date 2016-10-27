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
 * 我关注的人 适配器
 * Created by zcf on 2016/5/2.
 */
public class MyAttentionAdapter extends CommonAdapter<Guanzhu> {

    private DisplayImageOptions options;

    public MyAttentionAdapter(Context context, List<Guanzhu> datas, int layoutId) {
        super(context, datas, layoutId);
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisc(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
    }

    @Override
    public void convert(ViewHolder holder, Guanzhu guanzhu) {
        holder.setText(R.id.id_user_name, guanzhu.getBeiguanzhu().getNickName());
        if (guanzhu.getBeiguanzhu().getAutographUrl() != null) {
            holder.displayImage(R.id.id_user_head_img, guanzhu.getBeiguanzhu().getAutographUrl(), options);
        }

    }
}
