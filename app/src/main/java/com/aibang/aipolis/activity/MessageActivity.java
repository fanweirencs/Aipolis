package com.aibang.aipolis.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.aibang.aipolis.R;
import com.aibang.aipolis.fragment.message.AttentionFragment;
import com.aibang.aipolis.fragment.message.CommentFragment;
import com.aibang.aipolis.fragment.message.HelpFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论
 * Created by zcf on 2016/4/30.
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageButton delBtn, backBtn;

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        initEvent();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        initTabLayout();
        delBtn = (ImageButton) findViewById(R.id.id_delBtn);
        backBtn = (ImageButton) findViewById(R.id.id_backBtn);
    }

    /**
     * 初始化tablayout
     */
    private void initTabLayout() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fragments.add(new CommentFragment());
        fragments.add(new HelpFragment());
        fragments.add(new AttentionFragment());
        titles.add("评论");
        titles.add("互助");
        titles.add("关注");

        for (int i = 0; i < titles.size(); i++) {
            Log.d("size", titles.size() + "");
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        delBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_backBtn:
                finish();
                break;
            case R.id.id_delBtn:
                //// TODO: 2016/4/30
                break;
        }
    }
}
