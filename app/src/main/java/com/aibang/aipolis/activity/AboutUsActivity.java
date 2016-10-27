package com.aibang.aipolis.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aibang.aipolis.R;

/**
 * 关于我们
 * Created by zcf on 2016/5/3.
 */
public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ((TextView) findViewById(R.id.id_top_title)).setText(getString(R.string.about_us));
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
