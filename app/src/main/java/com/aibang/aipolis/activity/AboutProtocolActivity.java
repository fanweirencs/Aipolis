package com.aibang.aipolis.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aibang.aipolis.R;

/**
 * 桃花源协议
 * Created by zcf on 2016/5/3.
 */
public class AboutProtocolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_protocol);
        ((TextView) findViewById(R.id.id_top_title)).setText(getString(R.string.aipolis_protocol));
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }


}
