package com.aibang.aipolis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aibang.aipolis.R;


public class GuanyuTaoActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guanyutaohuayuan);
		
//		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		((TextView) findViewById(R.id.id_top_title)).setText("关于桃花源");
//		ImageButton welcomeIbt01 = (ImageButton) findViewById(R.id.welcomeIbt01);
//		welcomeIbt01.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				 Intent intent = new Intent(GuanyuTaoActivity.this, RegisterActivity.class);
//				  startActivity(intent);
//			}
//		});
	}

	/**
	 * 跳转注册界面
	 * @param view
     */
	public void register_btn(View view){
		Intent intent = new Intent(GuanyuTaoActivity.this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}


	/**
	 * 返回
	 */
	public void back(View view) {
		finish();
	}






}
