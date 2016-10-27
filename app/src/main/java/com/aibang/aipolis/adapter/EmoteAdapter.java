package com.aibang.aipolis.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aibang.aipolis.R;
import com.aibang.aipolis.base.BaseArrayListAdapter;
import com.aibang.aipolis.bean.FaceText;

public class EmoteAdapter extends BaseArrayListAdapter {

	public EmoteAdapter(Context context, List<FaceText> datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView
					.findViewById(R.id.v_face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText faceText = (FaceText) getItem(position);
		String key = faceText.text.substring(1);
		int resID = mContext.getResources().getIdentifier(key, "drawable","com.aibang.aipolis");
		holder.mIvImage.setImageResource(resID);
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}
}
