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
import android.widget.Button;
import android.widget.TextView;

import com.aibang.aipolis.R;


public class ConfirmDialog extends Dialog {
	private Context context;
	private TextView titleTv,contentTv;
	private Button okBtn,cancelBtn;
	private View line;
	private OnDialogClickListener dialogClickListener;

	public ConfirmDialog(Context context) {
		this(context,R.style.DialogStyle);
	}

	public ConfirmDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
		initalize();
	}

	private void initalize() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.confirm_dialog, null);
		setContentView(view);
		initWindow();

		titleTv = (TextView) findViewById(R.id.title_name);
		contentTv = (TextView) findViewById(R.id.text_view);
		okBtn = (Button) findViewById(R.id.btn_ok);
		line= findViewById(R.id.id_horizontal_line);
		cancelBtn = (Button)findViewById(R.id.btn_cancel);
		okBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if(dialogClickListener != null){
					dialogClickListener.onOKClick();
				}
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if(dialogClickListener != null){
					dialogClickListener.onCancelClick();
				}
			}
		});
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


	public void setTopTitle(String s){

		titleTv.setText(s);
	}

	public void setContentText(String s){

		contentTv.setText(s);
	}

	public void setOkText(String s){

		okBtn.setText(s);
	}
	public void setOkVisible(int visible){
		okBtn.setVisibility(visible);
	}


	public void setCancelVisible(int visible){
		cancelBtn.setVisibility(visible);
	}

	public void setCancelText(String s){
		cancelBtn.setText(s);
	}

	public void setOnDialogClickListener(OnDialogClickListener clickListener){
		dialogClickListener = clickListener;
	}
	
	
	public interface OnDialogClickListener{
		void onOKClick();
		void onCancelClick();
	}
}
