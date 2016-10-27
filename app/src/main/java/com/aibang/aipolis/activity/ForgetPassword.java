package com.aibang.aipolis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aibang.aipolis.R;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

public class ForgetPassword extends Activity {
    String phone;
    String password;
    String surePassword;
    String yanzhengma;
    private EditText sure_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);

//	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        final EditText et_phone = (EditText) findViewById(R.id.phoneNumber);
        sure_password = (EditText) findViewById(R.id.sure_password);
        et_phone.setInputType(InputType.TYPE_CLASS_PHONE);
        final EditText et_password = (EditText) findViewById(R.id.fore_password);
        final EditText et_yaznhengma = (EditText) findViewById(R.id.et_yanzhengma);
        Button img_yanzhengma = (Button) findViewById(R.id.fore_img_yanzhengma);
        img_yanzhengma.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                phone = et_phone.getText().toString();
                if (phone.equals("")) {
                    Toast.makeText(ForgetPassword.this, "电话号码为空", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgetPassword.this, "验证码已发送，请稍后...", Toast.LENGTH_SHORT).show();
                    BmobSMS.requestSMSCode(getBaseContext(), phone, "桃花源团队", new RequestSMSCodeListener() {

                        public void done(String smsId, BmobException ex) {
                            // TODO Auto-generated method stub
                            if (ex == null) {//验证码发送成功
                                Toast.makeText(ForgetPassword.this, "短信Id" + smsId, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void done(Integer arg0, BmobException arg1) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }
        });
        Button tijiaomima = (Button) findViewById(R.id.tijiaomima);
        tijiaomima.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                password = et_password.getText().toString();
                surePassword = sure_password.getText().toString();
                yanzhengma = et_yaznhengma.getText().toString();
                if (TextUtils.isEmpty(password)||TextUtils.isEmpty(surePassword)||TextUtils.isEmpty(yanzhengma)){//密码不相同
                    Toast.makeText(ForgetPassword.this, "请完善信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isSame(password,surePassword)){//密码不相同
                    Toast.makeText(ForgetPassword.this, "两次密码不相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals("") && !yanzhengma.equals("")) {
                    BmobUser.resetPasswordBySMSCode(ForgetPassword.this, yanzhengma, password, new ResetPasswordByCodeListener() {
                        @Override
                        public void done(BmobException ex) {
                            // TODO Auto-generated method stub
                            if (ex == null) {
                                Toast.makeText(ForgetPassword.this, "密码重置成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgetPassword.this, "密码重置失败:" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgetPassword.this, "请填写验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * 检测两次密码是否相同
     */
    private boolean isSame(String psd, String surePsd) {
        return psd.equals(surePsd);
    }


}
