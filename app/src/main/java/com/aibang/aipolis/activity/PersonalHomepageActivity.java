package com.aibang.aipolis.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.fragment.homepage.HomeLifeFragment;
import com.aibang.aipolis.fragment.homepage.HomeNeedHelpFragment;
import com.aibang.aipolis.fragment.homepage.HomeNeedInviteFragment;
import com.aibang.aipolis.fragment.message.ChatFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人主页
 * Created by zcf on 2016/5/16.
 */
public class PersonalHomepageActivity extends AppCompatActivity {
    private ImageView bgIv;//背景图片
    private CircleImageView userHeadImg;
    private TextView signatureTv;
    private User user;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnal_homepager);
        user = (User) getIntent().getSerializableExtra("user");
        initView();
        initData();
        initTabLayout();

    }

    private void initView() {
        TextView topTitle = (TextView) findViewById(R.id.id_top_title);
        topTitle.setText(user.getNickName());
        userHeadImg = (CircleImageView) findViewById(R.id.id_user_head_img);
        signatureTv = (TextView) findViewById(R.id.id_signature);
        bgIv = (ImageView) findViewById(R.id.id_bg);
    }

    public User getUser(){
        return user;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (user == null){
            return;
        }
        assert signatureTv != null;
        signatureTv.setText(user.getQianming());

        String uerHead = user.getAutographUrl();
        String bg = user.getBackgroundImg();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        //设置背景
        if (bg!=null){
            ImageLoader.getInstance().displayImage(bg, bgIv, options);
        }

        //设置头像
        assert userHeadImg != null;
        if (uerHead != null) {
            ImageLoader.getInstance().displayImage(uerHead, userHeadImg, options);
        } else if (user.getGender().equals("female")) {
            userHeadImg.setImageResource(R.mipmap.default_head_female);
        } else {
            userHeadImg.setImageResource(R.mipmap.default_head_male);
        }

    }

    /**
     * 初始化tablayout
     */
    private void initTabLayout() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);
        fragments.add(new HomeLifeFragment());
        fragments.add(new HomeNeedHelpFragment());
        fragments.add(new HomeNeedInviteFragment());
        titles.add("生活");
        titles.add("求助");
        titles.add("邀约");
        for (int i = 0; i < titles.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
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


    public void back(View view) {
        finish();
    }


}
