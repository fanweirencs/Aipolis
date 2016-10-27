package com.aibang.aipolis.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.LoginEvent;
import com.aibang.aipolis.utils.CustomToast;
import com.aibang.aipolis.view.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    private CheckBox jizhumima;

    private SharedPreferences sp;
    private Editor ed;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("crazy", MODE_WORLD_READABLE);
        ed = sp.edit();

        et_username = (EditText) findViewById(R.id.editText1);
        et_password = (EditText) findViewById(R.id.editText2);
        jizhumima = (CheckBox) findViewById(R.id.checkBox1);

        String username01 = sp.getString("username", null);
        String password01 = sp.getString("password", null);
        boolean isChecked01 = sp.getBoolean("isChecked", false);

        User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null && sp.getBoolean("tuichu", false) == false) {
            Intent intent02 = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent02);
        }

        et_username.setText(username01);
        if (isChecked01 == true) {
            jizhumima.setChecked(true);
            et_password.setText(password01);
        } else {
            et_password.setText("");
        }
    }


    /**
     * 检测输入的信息
     */

    private boolean checkInfo() {
        if (TextUtils.isEmpty(et_username.getText().toString().trim())
                || TextUtils.isEmpty(et_password.getText().toString().trim())) {

            return false;
        }

        return true;
    }

    /**
     * 登录   按钮监听事件
     *
     * @param view
     */
    public void login_btn(View view) {

        if (!checkInfo()) {
            Toast.makeText(getApplicationContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        ed.putBoolean("tuichu", false);

        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();

        if (jizhumima.isChecked()) {
            ed.putBoolean("isChecked", true);
            ed.putString("username", username.toString());
            ed.putString("password", password.toString());
            ed.commit();
        } else {
            ed.putBoolean("isChecked", false);
            ed.commit();
        }

        final User bu2 = new User();
        bu2.setUsername(username);
        bu2.setPassword(password);
        bu2.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
                CustomToast.showToast(getBaseContext(), "登录成功...", Toast.LENGTH_SHORT);
                ed.putInt("moshi", 0);
                ed.putBoolean("tuichu", false);
                ed.commit();
                //登录成功 保存设备消息 方便推送
                BmobInstallation.getCurrentInstallation(LoginActivity.this).save();



                //通知加载用户信息
                EventBus.getDefault().post(new LoginEvent());
                finish();
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
            }

            @Override
            public void onFailure(int code, String msg) {
                System.out.println(code + msg);
                Toast.makeText(LoginActivity.this, "登陆失败" + code + ":" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 注册按钮监听事件
     *
     * @param view
     */
    public void register_btn(View view) {
        Intent intent = new Intent(LoginActivity.this, GuanyuTaoActivity.class);
        startActivity(intent);
    }

    /**
     * 忘记按钮监听事件
     *
     * @param view
     */
    public void login_forgetpassword(View view) {
        dialog();
    }

    protected void dialog() {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTopTitle("Tips");
        dialog.setContentText(getString(R.string.please_choose_fin_password_style));
        dialog.setOkText("手机号码");
        dialog.setCancelText("个人邮箱");
        dialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
                startActivity(intent);
            }

            @Override
            public void onCancelClick() {
                Intent intent = new Intent(LoginActivity.this, ForgetPassword02.class);
                startActivity(intent);
            }
        });

        dialog.show();
//        Builder builder = new Builder(LoginActivity.this);
//        builder.setMessage("请选择用手机或是邮箱找回密码");
//        builder.setTitle("提示");
//        builder.setPositiveButton("手机号码", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
//                startActivity(intent);
//            }
//
//        });
//        builder.setNegativeButton("个人邮箱", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(LoginActivity.this, ForgetPassword02.class);
//                startActivity(intent);
//            }
//
//        });
//        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        ed.putInt("moshi", -1);
        ed.commit();
        finish();

    }










}
