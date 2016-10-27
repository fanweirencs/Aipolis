package com.aibang.aipolis.adapter;

import android.content.Context;

import com.aibang.aipolis.R;
import com.aibang.aipolis.base.CommonAdapter;
import com.aibang.aipolis.base.ViewHolder;

import java.util.List;

/**
 * 学校和省份 适配器
 * Created by zcf on 2016/5/18.
 */
public class ProvinceAndSchoolAdapter extends CommonAdapter<String> {
    public ProvinceAndSchoolAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String s) {
        holder.setText(R.id.id_school, s);
    }
}
