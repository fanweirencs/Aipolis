package com.aibang.aipolis.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.UpdateHeadEvent;
import com.aibang.aipolis.view.ConfirmDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 我的资料
 * Created by zcf on 2016/5/2.
 */
public class MyInforActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_CODE_INTEREST = 1002;
    private LinearLayout birthdayLy,interestLy;//生日 兴趣
    private TextView birthTv,schoolTv;//学校
    private LinearLayout schoolLayout;
    public final int REQUEST_CODE = 1000;
    private ImageButton backBtn;//返回按钮
    private Button saveBtn;//保存按钮
    private EditText departmentEt, emailEt, cityEt, phoneEt, signatureEt;
    private TextView nickNameTv;
    private CircleImageView headIv;
    private final int REQUEST_CODE_GALLERY = 1001;
    private LinearLayout nickNameLy;
    private GalleryFinal.OnHanlderResultCallback callback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (reqeustCode == REQUEST_CODE_GALLERY && resultList.size() > 0) {
                toast("正在更新头像···");
                Bitmap b = BitmapFactory.decodeFile(resultList.get(0).getPhotoPath());
                headIv.setImageBitmap(b);
                updateHead(resultList.get(0).getPhotoPath());//更新头像
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(MyInforActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
        initEvent();
        showMyInfo();
    }


    /**
     * 初始化
     */
    private void initView() {
        nickNameLy= (LinearLayout) findViewById(R.id.id_nickNameLy);
        birthdayLy = (LinearLayout) findViewById(R.id.id_birth_ly);
        interestLy= (LinearLayout) findViewById(R.id.id_interestLy);
        birthTv = (TextView) findViewById(R.id.id_birthday);
        saveBtn = (Button) findViewById(R.id.id_saveBtn);
        backBtn = (ImageButton) findViewById(R.id.id_backBtn);
        nickNameTv = (TextView) findViewById(R.id.id_nick_name);
        schoolTv = (TextView) findViewById(R.id.id_school);
        schoolLayout = (LinearLayout) findViewById(R.id.id_school_Layout);
        departmentEt = (EditText) findViewById(R.id.id_department);
        emailEt = (EditText) findViewById(R.id.id_email);
        cityEt = (EditText) findViewById(R.id.id_city);
        phoneEt = (EditText) findViewById(R.id.id_phone);
        signatureEt = (EditText) findViewById(R.id.id_signature);
        headIv = (CircleImageView) findViewById(R.id.id_user_head_img);
    }

    /**
     * 初始化 监听事件
     */
    private void initEvent() {
        birthdayLy.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        headIv.setOnClickListener(this);
        schoolLayout.setOnClickListener(this);
        interestLy.setOnClickListener(this);
        nickNameLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog dialog = new ConfirmDialog(MyInforActivity.this);
                dialog.setTopTitle("Tips");
                dialog.setContentText("如需修改昵称，请发邮件至aipolis@qq.com");
                dialog.show();
            }
        });
    }

    /**
     * 显示我的资料
     */

    private void showMyInfo() {
        User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            nickNameTv.setText(user.getNickName());
            schoolTv.setText(user.getSchool());

            if (!TextUtils.isEmpty(user.getYuanxi())) {
                departmentEt.setText(user.getYuanxi());
            }

            if (!TextUtils.isEmpty(user.getBirthday())) {
                birthTv.setText(user.getBirthday());
            }

            if (!TextUtils.isEmpty(user.getEmail())) {
                emailEt.setText(user.getEmail());
            }

            if (!TextUtils.isEmpty(user.getLocation())) {
                cityEt.setText(user.getLocation());
            }

            if (!TextUtils.isEmpty(user.getMobilePhoneNumber())) {
                phoneEt.setText(user.getMobilePhoneNumber());
            }

            if (!TextUtils.isEmpty(user.getQianming())) {
                signatureEt.setText(user.getQianming());
            }

            //设置头像
            String url = user.getAutographUrl();
            if (!TextUtils.isEmpty(url)) {
                ImageLoader.getInstance().displayImage(user.getAutographUrl(), headIv);
            } else if (user.getGender().equals("female")) {
                headIv.setImageResource(R.mipmap.default_head_female);
            } else {
                headIv.setImageResource(R.mipmap.default_head_male);
            }
        }

    }

    /**
     * 保存头像
     */

    private void updateHead(String picPath) {
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(MyInforActivity.this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                User newUser = new User();
                //bmobFile.getFileUrl(context)--返回的上传文件的完整地址
                newUser.setAutographUrl(bmobFile.getFileUrl(MyInforActivity.this));
                User user = BmobUser.getCurrentUser(MyInforActivity.this, User.class);
                newUser.update(MyInforActivity.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        //通知主界面侧边栏 更新头像
                        EventBus.getDefault().post(new UpdateHeadEvent(
                                bmobFile.getFileUrl(MyInforActivity.this)));
                        toast("更新成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("更新失败" + s);
                    }
                });

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }

            @Override
            public void onFailure(int code, String msg) {
                toast("更新失败 " + msg);
            }
        });
    }

    /**
     * 修改信息
     */
    private void saveMyInfo() {
        User newUser = new User();
        //不能修改昵称
//        newUser.setNickName(nickNameEt.getText().toString());
        newUser.setSchool(schoolTv.getText().toString());
        newUser.setYuanxi(departmentEt.getText().toString());
        newUser.setBirthday(birthTv.getText().toString());
        newUser.setLocation(cityEt.getText().toString());
        newUser.setQianming(signatureEt.getText().toString());

        //邮箱 手机号
        newUser.setEmail(emailEt.getText().toString());
        newUser.setMobilePhoneNumber(phoneEt.getText().toString());

        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        newUser.update(this, bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("更新信息成功:");
                finish();

            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                toast("更新信息失败:" + msg);
            }
        });
    }

    /**
     * 吐司
     *
     * @param s
     */
    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    /**
     * 具体监听逻辑
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.id_backBtn:
                finish();
                break;
            //保存资料
            case R.id.id_saveBtn:
                saveMyInfo();
                break;
            //选择生日
            case R.id.id_birth_ly:
                birth();
                break;
            //打开相册
            case R.id.id_user_head_img:
                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, callback);
                break;
            case R.id.id_school_Layout:
                Intent intent = new Intent(MyInforActivity.this,ProvinceAndSchoolActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.id_interestLy:
                Intent interestIntent = new Intent(MyInforActivity.this,InterestActivity.class);
                startActivityForResult(interestIntent, REQUEST_CODE_INTEREST);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == 1001) {
            String school = data.getStringExtra("result");
            schoolTv.setText(school);
        }
    }


    /**
     *
     */


    /**
     * 弹出日期 让用户选择
     */
    private void birth() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog tpd = DatePickerDialog.newInstance(
                MyInforActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
//                tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
//                tpd.enableSeconds(true);
        tpd.setAccentColor(Color.parseColor("#FF03A9F4"));
        tpd.setTitle("请选择您的生日");
        tpd.setOkText("确定");
        tpd.setCancelText("取消");
        tpd.show(getFragmentManager(), "Datepickerdialog");
    }


    /**
     * 设置 生日
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String time = year + "-" + month + "-" + dayOfMonth;
        birthTv.setText(time);
    }


}
