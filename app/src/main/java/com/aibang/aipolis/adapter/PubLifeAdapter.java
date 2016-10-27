package com.aibang.aipolis.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aibang.aipolis.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.toolsfinal.DeviceUtils;

/**
 * 发布生活
 * Created by zcf on 2016/5/11.
 */
public class PubLifeAdapter extends BaseAdapter {
    private List<PhotoInfo> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mScreenWidth;
    DisplayImageOptions options;

    public PubLifeAdapter(Activity context, List<PhotoInfo> mList) {
        this.mList = mList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mScreenWidth = DeviceUtils.getScreenPix(context).widthPixels;
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_my_default_image)
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .showImageForEmptyUri(R.drawable.ic_my_default_image)
                .showImageOnLoading(R.drawable.ic_my_default_image).build();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gride_item_pub_life, parent,
                    false);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.iv);
            setHeight(holder.ivImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


            PhotoInfo photoInfo = mList.get(position);
            ImageLoader.getInstance().displayImage("file:/" + photoInfo.getPhotoPath(), holder.ivImage,options);
//            ImageLoader.getInstance().displayImage("file:/" + photoInfo.getPhotoPath(), new ImageViewAware(holder.ivImage), options, imageSize, null, null);

        return convertView;
    }


    private class ViewHolder {
        private ImageView ivImage;
    }

    private void setHeight(final View convertView) {
        int height = mScreenWidth / 3 - 24;
//        ViewGroup.LayoutParams.MATCH_PARENT
        convertView.setLayoutParams(new AbsListView.LayoutParams(height, height));
    }

}
