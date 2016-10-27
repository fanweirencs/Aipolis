package com.aibang.aipolis.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aibang.aipolis.R;


public class MyProgressDialog extends Dialog {
	private Context context;
	private TextView titleTv,progressTv;
	private ProgressBar progressBar;

	public MyProgressDialog(Context context) {
		this(context,R.style.DialogStyle);
	}

	public MyProgressDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
		initalize();
	}

	private void initalize() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.my_progress_dialog, null);
		setContentView(view);
		initWindow();

		titleTv = (TextView) findViewById(R.id.title_name);
		progressTv= (TextView) findViewById(R.id.progress);
		progressBar = (ProgressBar) findViewById(R.id.id_progressbar);

	}

	public void setTopTitle(String s){

		titleTv.setText(s);
	}

	public void setProgress(int progress){

		progressTv.setText(progress+"%");
	}

	private void initWindow() {
		Window dialogWindow = getWindow();
		dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
		dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics();
		lp.width = (int) (d.widthPixels * 0.8); 
		lp.gravity = Gravity.CENTER;		
		dialogWindow.setAttributes(lp);
	}
	

}
