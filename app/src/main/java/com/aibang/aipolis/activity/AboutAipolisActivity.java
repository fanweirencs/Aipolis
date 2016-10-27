package com.aibang.aipolis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aibang.aipolis.R;

/**
 * 关于桃花源
 * Created by zcf on 2016/5/2.
 */
public class AboutAipolisActivity extends AppCompatActivity implements View.OnClickListener {
    //简介      协议    隐私  关于我们  意见建议 priviceTv
    private TextView breifTv, protocolTv , aboutUsTv, opinionsTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_aipolis);
        initView();
        initEvent();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        breifTv = (TextView) findViewById(R.id.id_aipolis_brief);
        protocolTv = (TextView) findViewById(R.id.id_aipolis_protocol);
//        priviceTv = (TextView) findViewById(R.id.id_privacy_policy);
        aboutUsTv = (TextView) findViewById(R.id.id_about_us);
        opinionsTv = (TextView) findViewById(R.id.id_opinions_suggestions);
    }

    /**
     * 初始化 监听事件
     */
    private void initEvent() {
        breifTv.setOnClickListener(this);
        protocolTv.setOnClickListener(this);
//        priviceTv.setOnClickListener(this);
        aboutUsTv.setOnClickListener(this);
        opinionsTv.setOnClickListener(this);
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
            case R.id.id_aipolis_brief:
                startActivity(new Intent(AboutAipolisActivity.this, AboutBriefActivity.class));
                break;
            case R.id.id_aipolis_protocol:
                startActivity(new Intent(AboutAipolisActivity.this, AboutProtocolActivity.class));
                break;
            case R.id.id_about_us:
                startActivity(new Intent(AboutAipolisActivity.this, AboutUsActivity.class));
                break;
            case R.id.id_opinions_suggestions:
                startActivity(new Intent(AboutAipolisActivity.this, AboutOpinionsActivity.class));
                break;
        }
    }
}
