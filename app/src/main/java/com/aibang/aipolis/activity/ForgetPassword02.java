package com.aibang.aipolis.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aibang.aipolis.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

public class ForgetPassword02 extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youxiang);
		
//	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		 
		final EditText et_youxiang = (EditText) findViewById(R.id.youxiang);
	    Button tijiaomima = (Button) findViewById(R.id.tijiaomima);
	    tijiaomima.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = et_youxiang.getText().toString();
				if(str!=null && !str.equals("")){
					Pattern a =  Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");//复杂匹配  
			        Matcher b = a.matcher(str);
			        if(b.matches()==true){
			        	BmobUser.resetPasswordByEmail(ForgetPassword02.this, str, new ResetPasswordByEmailListener() {
			        	    @Override
			        	    public void onSuccess() {
			        	        // TODO Auto-generated method stub
			        	       // toast("重置密码请求成功，请到" + email + "邮箱进行密码重置操作");
			        	    	dialog("重置密码请求成功，请到对应邮箱进行密码重置操作",true);
			        	    	
			        	    }
			        	    @Override
			        	    public void onFailure(int code, String e) {
			        	        // TODO Auto-generated method stu
			        	    	dialog("重置密码请求失败"+e,false);
			        	    }
			        	});
			        }else{
			        	dialog("输入邮箱地址不合法，请重新输入...",false);
			        }
				}else{
					dialog("请输入注册邮箱！！！",false);
				}
			}
		});
		
		
	}
	protected void dialog(String str,final boolean fanhui) {
		 Builder builder = new Builder(ForgetPassword02.this);
		 builder.setTitle("提示");
		 builder.setMessage(str);
		 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

				if(fanhui==true){
					Intent intent = new Intent(ForgetPassword02.this,LoginActivity.class);
					startActivity(intent);
				}
			}

		 });
		  builder.create().show();
     }
}
