package com.aibang.aipolis.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.Help;
import com.aibang.aipolis.bean.Interest;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.InviteEvent;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 发布 邀约页面
 * Created by zcf on 2016/5/31.
 */
public class PubInviteActivity extends AppCompatActivity implements
        View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    //标题 内容
    private EditText titleEt, contentEt,locationEt;

    private boolean isSelect = false;//判断类型是否选择
    private Spinner spinner;
    private TextView childStyleTv;

    private LinearLayout timeLy;
    private TextView timeTv;
    private List<String> spin_array = new ArrayList<>();

    //    private String[] spin_arry = {
//            "运动", "音乐", "电影", "动漫", "游戏", "设计",
//            "桌游", "旅行", "吃货", "摄影", "读书", "兼职"
//    };
    private ArrayAdapter mAdapter;
    private String content;
    //    private String childStyle;
    private String time;
    private String leiBie = "运动";//类别

    BmobPushManager bmobPush ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_invite);
        TextView title = (TextView) findViewById(R.id.id_top_title);
        bmobPush = new BmobPushManager(this);
        title.setText("邀约");
        initView();
        initEvent();
        queryInterest();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        titleEt = (EditText) findViewById(R.id.id_title);
        contentEt = (EditText) findViewById(R.id.id_content);
        timeLy = (LinearLayout) findViewById(R.id.id_timeLy);
        timeTv = (TextView) findViewById(R.id.id_time);
        childStyleTv = (TextView) findViewById(R.id.id_childStyle);
        locationEt=(EditText) findViewById(R.id.id_location);
        spinner = (Spinner) findViewById(R.id.id_style);
        mAdapter =
                new ArrayAdapter(this, R.layout.spinner_item, spin_array);
        spinner.setAdapter(mAdapter);
    }

    /**
     * 初始化 监听事件
     */
    private void initEvent() {
        timeLy.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                leiBie = spinner.getSelectedItem().toString();
                leiBie = spin_array.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * 查询 兴趣列表
     */
    private void queryInterest() {
        BmobQuery<Interest> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Interest>() {
            @Override
            public void onSuccess(List<Interest> list) {
                if (list.size() > 0) {
                    for (Interest interest : list) {
                        spin_array.add(interest.getName());

                    }
                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    /**
     *推送消息给频道
     */

    private void pushAllMessage(){
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        List<String> channels = new ArrayList<>();
        channels.add(leiBie);
        Log.d("leibie007",leiBie);
        query.addWhereEqualTo("channels", channels);
        bmobPush.setQuery(query);
        JSONObject json = new JSONObject();
        JSONObject array = new JSONObject();
        try {
            array.put("alert", "【"+leiBie +"】"+" 有人发起了邀约");
            array.put("style","101");
            json.put("aps", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bmobPush.pushMessage(json);
    }




    /**
     * 发布 监听事件
     */
    public void pub(View view) {

        User user = BmobUser.getCurrentUser(PubInviteActivity.this, User.class);
        if (user == null) {
            startActivity(new Intent(PubInviteActivity.this, LoginActivity.class));
            Toast.makeText(PubInviteActivity.this, "请登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        String childStyle = childStyleTv.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(PubInviteActivity.this, "请填写标题内容", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(childStyle)) {
            Toast.makeText(PubInviteActivity.this, "请填写 子类别", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(PubInviteActivity.this, "请填写事件内容", Toast.LENGTH_SHORT).show();
            return;
        }

        String myTime = timeTv.getText().toString();
        if (TextUtils.isEmpty(myTime)) {
            Toast.makeText(PubInviteActivity.this, "请选择时间", Toast.LENGTH_SHORT).show();
            return;
        }
        String diDian = locationEt.getText().toString();
        if (TextUtils.isEmpty(diDian)) {
            Toast.makeText(PubInviteActivity.this, "请填写具体地点", Toast.LENGTH_SHORT).show();
            return;
        }

        leiBie = spinner.getSelectedItem().toString();
        Help help = new Help();
        help.setUser(user);
        help.setBelongPhonenumber(user.getMobilePhoneNumber());
        help.setCommentNumber(0);
        help.setTitle(title);
        help.setContent(content);
        help.setIsDone(false);
        help.setType(false);
        help.setLookSum(1);
        help.setLeiBie(leiBie);//类别
        help.setDiDian(diDian);//地点
        help.setYaoYueTime(myTime);//邀约时间
        help.setZiLeibie(childStyle);//子类别
        help.save(getBaseContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                pushAllMessage();
                Toast.makeText(PubInviteActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new InviteEvent());

                finish();
            }

            @Override
            public void onFailure(int code, String arg0) {
                Toast.makeText(PubInviteActivity.this, "发布失败" + arg0, Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 返回
     */
    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_timeLy:
                showDate();
                break;
        }
    }


    /**
     * 显示时间  年日月
     */
    private void showDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog tpd = DatePickerDialog.newInstance(
                PubInviteActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
//                tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
//                tpd.enableSeconds(true);
        tpd.setAccentColor(Color.parseColor("#FF03A9F4"));
        tpd.setTitle("请选择时间");
        tpd.setOkText("确定");
        tpd.setCancelText("取消");
        tpd.show(getFragmentManager(), "Datepickerdialog");
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                time = "";
            }
        });
    }

    /**
     * 显示时间  时分秒
     */
    private void showTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                PubInviteActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true
        );
//                tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
//                tpd.enableSeconds(true);
        tpd.setAccentColor(Color.parseColor("#FF03A9F4"));
        tpd.setTitle("请选择时间");
        tpd.setOkText("确定");
        tpd.setCancelText("取消");
        tpd.show(getFragmentManager(), "Timepickerdialog");
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                time = "";
            }
        });
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String myMinute;
        if (minute < 10) {
            myMinute = "0" + minute;
        } else {
            myMinute = String.valueOf(minute);
        }
        time += hourOfDay + ":" + myMinute;
        timeTv.setText(time);
    }


    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        time = month + "-" + dayOfMonth + "  ";
        showTime();
    }
}
