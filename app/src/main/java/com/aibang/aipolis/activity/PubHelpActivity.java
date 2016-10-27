package com.aibang.aipolis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.Guanzhu;
import com.aibang.aipolis.bean.Help;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.HelpEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 发布 求助页面
 * Created by zcf on 2016/5/2.
 */
public class PubHelpActivity extends AppCompatActivity implements View.OnClickListener {
    //标题 内容
    private EditText titleEt, contentEt, remarkEt;
    private String title, content;
    private boolean isSelect = false;//判断类型是否选择
    //选择的类型  是邀约还是互助  true 代表 互助 false代表 邀约

    private User me;

    private List<Guanzhu> guanzhuList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_help);
        queryAttentionMe();

        TextView title = (TextView) findViewById(R.id.id_top_title);
        title.setText("求助");
        initView();
        initEvent();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        titleEt = (EditText) findViewById(R.id.id_title);
        contentEt = (EditText) findViewById(R.id.id_content);
        remarkEt = (EditText) findViewById(R.id.id_remark);
    }

    /**
     * 初始化 监听事件
     */
    private void initEvent() {
    }


    /**
     * 查询关注我的人
     */
    private void queryAttentionMe() {
        final User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {

            BmobQuery<Guanzhu> query = new BmobQuery<>();
            query.addWhereEqualTo("beiguanzhu", user);
            query.include("guanzhu");
            query.findObjects(this, new FindListener<Guanzhu>() {
                @Override
                public void onSuccess(List<Guanzhu> object) {
                    // TODO Auto-generated method stub
                    if (object.size() > 0) {
                        for (Guanzhu g : object) {
                            guanzhuList.add(g);

                        }
                    }


                }

                @Override
                public void onError(int code, String msg) {
                    // TODO Auto-generated method stub
//                toast("查询用户失败："+msg);
                }
            });

        }
    }


//    private void queryShenFen() {
//        BmobQuery<User> query = new BmobQuery<>();
//        query.addWhereEqualTo("objectId", user.getObjectId());
//        query.addQueryKeys("shenfen");
//        query.findObjects(PubHelpActivity.this, new FindListener<User>() {
//            @Override
//            public void onSuccess(List<User> list) {
//                if (list.size() > 0) {
//                    user.setShenfen(list.get(0).getShenfen());
//                    Log.d("ss007",user.getShenfen()+"");
//                }
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//
//            }
//        });
//    }

    /**
     * 发布 监听事件
     */
    public void pub(View view) {
        me = BmobUser.getCurrentUser(PubHelpActivity.this, User.class);
        if (me == null) {
            startActivity(new Intent(PubHelpActivity.this, LoginActivity.class));
            Toast.makeText(PubHelpActivity.this, "请登录", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 查询身份 1或者空代表正常  2 代表封号 无法发布状态
         */

//        if (!(String.valueOf(user.getShenfen()).equals("null")
//                || String.valueOf(user.getShenfen()).equals("1"))) {
//            Toast.makeText(PubHelpActivity.this, "很抱歉，由于您的不良行为，您暂时无法发表", Toast.LENGTH_SHORT).show();
//            return;
//        }
        title = titleEt.getText().toString();
        content = contentEt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(PubHelpActivity.this, "请填写标题内容", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(PubHelpActivity.this, "请填写事件内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Help help = new Help();
        help.setUser(me);
        help.setBelongPhonenumber(me.getMobilePhoneNumber());
        help.setCommentNumber(0);
        help.setTitle(title);
        help.setContent(content);

        if (!TextUtils.isEmpty(remarkEt.getText().toString())) {
            help.setBeizhu(remarkEt.getText().toString());
        }

        help.setIsDone(false);
        //选择的类型  是邀约还是互助  true 代表求助 false代表 邀约
        help.setType(true);
        help.setLookSum(1);
        help.save(getBaseContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PubHelpActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        if (guanzhuList.size()>0){
                            for (int i =0;i<guanzhuList.size();i++){
                                pushAndroidMessage(guanzhuList.get(i).getGuanzhu());
                            }

                        }
                        EventBus.getDefault().post(new HelpEvent());
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String arg0) {
                        Toast.makeText(PubHelpActivity.this, "发布失败" + arg0, Toast.LENGTH_SHORT).show();
                    }
                }

        );

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


        }
    }


    /**
     * 推送消息
     */
    private void pushAndroidMessage(User user) {
        if (user!=null){
            BmobPushManager bmobPush = new BmobPushManager(this);
            BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
            query.addWhereEqualTo("uid", user.getObjectId());
            bmobPush.setQuery(query);
            JSONObject json = new JSONObject();
            JSONObject array = new JSONObject();
            try {
                array.put("alert", " 你关注的人:【" + me.getNickName() + "】发布了求助: " + title);
                array.put("style","102");
                json.put("aps", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bmobPush.pushMessage(json);
        }



    }


}
