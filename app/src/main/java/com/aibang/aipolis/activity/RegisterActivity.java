package com.aibang.aipolis.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.utils.Countdown;
import com.aibang.aipolis.utils.RegexUtils;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private EditText nickNameEt, passwordEt, accountEt, verifyCodeEt;
    public final int REQUEST_CODE = 1000;
    private TextView getCodeTv, schoolTv;//获取验证码  学校
    private LinearLayout veryCodeLy, schoolLy;//验证码 学校

    private String nickName, password, account, verifyCode, school;
    private ProgressDialog mDialog;

    private final boolean STYLE_EMAIL = false;//代表注册方式为邮箱注册
    private final boolean STYLE_PHONE = true;//代表注册方式为手机注册
    private boolean style;//注册方式

    private final int SEX_MALE = 0;//男生
    private final int SEX_FEMALE = 1;//女生
    private int sex = -1;//性别

    private ImageView maleIv, femaleIv;

    private TextView ruleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvents();

    }


    private void initView() {
        registerBtn = (Button) findViewById(R.id.id_regBtn);
        nickNameEt = (EditText) findViewById(R.id.id_nickname);
        passwordEt = (EditText) findViewById(R.id.id_password);
        accountEt = (EditText) findViewById(R.id.id_account);
        verifyCodeEt = (EditText) findViewById(R.id.id_veryNumber);
        getCodeTv = (TextView) findViewById(R.id.id_getVerify);
        schoolTv = (TextView) findViewById(R.id.id_school);
        veryCodeLy = (LinearLayout) findViewById(R.id.id_veryCodeLy);
        schoolLy = (LinearLayout) findViewById(R.id.id_school_Layout);
        maleIv = (ImageView) findViewById(R.id.id_male);
        femaleIv = (ImageView) findViewById(R.id.id_female);
        ruleTv = (TextView) findViewById(R.id.id_ruleTv);
    }

    private void initEvents() {
        //用户注册
        registerBtn.setOnClickListener(this);

        //获取验证码
        getCodeTv.setOnClickListener(this);

        maleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleIv.setImageResource(R.drawable.male_selected);
                femaleIv.setImageResource(R.drawable.female_normal);
                sex = SEX_MALE;
            }
        });

        femaleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleIv.setImageResource(R.drawable.female_selected);
                maleIv.setImageResource(R.drawable.male_normal);
                sex = SEX_FEMALE;
            }
        });

        //选择学校
        schoolLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, ProvinceAndSchoolActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        accountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    if (RegexUtils.isEmail(s)) {
                        veryCodeLy.setVisibility(View.GONE);
                        style = STYLE_EMAIL;// 代表邮箱
                    } else {
                        veryCodeLy.setVisibility(View.VISIBLE);
                        style = STYLE_PHONE;//代表手机
                    }
                } else {
                    veryCodeLy.setVisibility(View.GONE);
                    style = STYLE_EMAIL;// 代表邮箱
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ruleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, AboutProtocolActivity.class));
            }
        });

        nickNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                // 此处为得到焦点时的处理内容
                    toast("昵称一旦设置，不可更改");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == 1001) {
            String school = data.getStringExtra("result");
            schoolTv.setText(school);
        }
    }


    /**
     * 监测输入信息
     *
     * @return 返回信息是否完整
     */
    private boolean checkMobileInfo() {
        nickName = nickNameEt.getText().toString();
        password = passwordEt.getText().toString();
        account = accountEt.getText().toString();
        verifyCode = verifyCodeEt.getText().toString();
        school = schoolTv.getText().toString();
        //信息是否完善
        if (TextUtils.isEmpty(nickName)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(account)
                || TextUtils.isEmpty(verifyCode)
                || TextUtils.isEmpty(school)) {
            Toast.makeText(this, "请完善信息,在注册", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sex == -1) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断账号是否为手机号
//        if (!RegexUtils.isMobilePhoneNumber(account)) {
//            toast("手机号不正确");
//            return false;
//        }
        return true;
    }


    /**
     * 通过手机注册
     */
    private void registerByPhone() {
        mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setMessage("正在注册");
        mDialog.show();
        User user = new User();
        user.setUsername(account);//账号
        user.setPassword(password);//密码
        user.setNickName(nickName);//昵称
        user.setMobilePhoneNumber(account);//电话号码
        user.setMobilePhoneNumberVerified(true);//验证通过
        user.setShenfen(1);
        user.setSchool(school);
        if (sex == SEX_MALE) {
            user.setGender("male");
        } else {
            user.setGender("female");
        }
        user.setFollowersNumber(0);//关注我的人数
        user.setFollowingNumber(0);//我关注的人数
        user.setLikes(0);//收藏
        //注意：不能用save方法进行注册
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("注册成功:");
                mDialog.dismiss();
                startActivity(new Intent(RegisterActivity.this, RujuxuzhiActivity.class));
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                mDialog.dismiss();
                if (msg.equals("unique index cannot has duplicate value: " + nickName))
                    Toast.makeText(RegisterActivity.this, "注册失败：昵称重复，请更换不同昵称", Toast.LENGTH_LONG).show();
                else if (msg.contains("username")) {
                    Toast.makeText(RegisterActivity.this, "注册失败：手此账号已存在，请更换不同其他账号", Toast.LENGTH_LONG).show();
                } else {
                    toast("注册失败," + msg);
                }
            }
        });
    }

    /**
     * 通过邮箱注册
     */
    private void registerByEmail() {
        nickName = nickNameEt.getText().toString();
        password = passwordEt.getText().toString();
        account = accountEt.getText().toString();
        school = schoolTv.getText().toString();
        //信息是否完善
        if (TextUtils.isEmpty(nickName)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(account)
                || TextUtils.isEmpty(school)) {
            Toast.makeText(this, "请完善信息,在注册", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!RegexUtils.isEmail(account)) {
            toast("邮箱格式不正确");
            return;
        }
        if (sex == -1) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }


        mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setMessage("正在注册");
        mDialog.show();
        User user = new User();
        if (sex == SEX_MALE) {
            user.setGender("male");
        } else {
            user.setGender("female");
        }
        user.setUsername(account);//账号  邮箱
        user.setPassword(password);//密码
        user.setNickName(nickName);
        user.setEmail(account);//邮箱
        user.setShenfen(1);//身份1代表正常，2代表无法发表状态
        user.setFollowersNumber(0);//关注我的人数
        user.setFollowingNumber(0);//我关注的人数
        user.setLikes(0);//收藏
        //注意：不能用save方法进行注册
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("注册成功:");
                mDialog.dismiss();
                startActivity(new Intent(RegisterActivity.this, RujuxuzhiActivity.class));
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                mDialog.dismiss();
                if (msg.equals("unique index cannot has duplicate value: " + nickName))
                    Toast.makeText(RegisterActivity.this, "注册失败：昵称重复，请更换不同昵称", Toast.LENGTH_LONG).show();
                else if (msg.contains("username")) {
                    Toast.makeText(RegisterActivity.this, "注册失败：手此账号已存在，请更换不同其他账号", Toast.LENGTH_LONG).show();
                } else {
                    toast("注册失败," + msg);
                }

            }
        });
    }

    /**
     * 土司
     *
     * @param msg
     */
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //注册
            case R.id.id_regBtn:
                if (style) {//手机注册
                    if (checkMobileInfo()) {//检测手机信息是否完整
                        verifySms(account, verifyCode);//发送验证码 验证成功并注册
                    }
                } else {//邮箱注册
                    registerByEmail();
                }


                break;
            //获取验证码
            case R.id.id_getVerify:
                if (TextUtils.isEmpty(accountEt.getText().toString().trim())) {
                    toast("请输入手机号");
                    return;
                }

//                if (!RegexUtils.isMobilePhoneNumber(accountEt.getText().toString().trim())) {
//                    toast("手机号不正确,请重新输入");
//                    return;
//                }
                //请求验证码
                sendSms((accountEt.getText().toString().trim()));
                Countdown countdown = new Countdown(getCodeTv, "%s秒后重新获取短信", 60);
                countdown.start();

                break;
        }
    }

    /**
     * 发送短信并且验证
     */

    private void sendSms(String phone) {
        BmobSMS.requestSMSCode(this, phone, "桃花源团队", new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//验证码发送成功
                    Log.i("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                }
            }
        });
    }

    /**
     * 验证 验证码是否正确
     *
     * @param phone      电话号码
     * @param verifyCode 验证码
     */
    private void verifySms(String phone, String verifyCode) {
        BmobSMS.verifySmsCode(this, phone, verifyCode, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//短信验证码已验证成功
                    Log.i("bmob", "验证通过");
                    //验证码  正确性
                    registerByPhone();//注册

                } else {
                    toast("验证失败" + "msg = " + ex.getLocalizedMessage());
                    Log.i("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                }
            }
        });
    }

}
