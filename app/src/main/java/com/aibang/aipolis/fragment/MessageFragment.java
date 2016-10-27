package com.aibang.aipolis.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.MainActivity;
import com.aibang.aipolis.event.MsgRefreshEvent;
import com.aibang.aipolis.fragment.message.ChatFragment;
import com.aibang.aipolis.fragment.message.AttentionFragment;
import com.aibang.aipolis.fragment.message.CommentFragment;
import com.aibang.aipolis.fragment.message.HelpFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 消息
 * Created by zcf on 2016/4/28.
 */
public class MessageFragment extends Fragment implements View.OnClickListener {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageButton refreshBtn;

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    private Button openDrawerBtn;//打开抽屉
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messsage, container, false);
        initView(view);
        initEvent();
        return view;
    }


    /**
     * 初始化控件
     */
    private void initView(View view) {
        openDrawerBtn = (Button) view.findViewById(R.id.id_menu_btn);
        //打开抽屉
        openDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.openDrawer();
            }
        });
        initTabLayout(view);
        refreshBtn = (ImageButton) view.findViewById(R.id.id_refreshBtn);
    }

    /**
     * 初始化tablayout
     */
    private void initTabLayout(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        fragments.add(new ChatFragment());
        fragments.add(new CommentFragment());
        fragments.add(new HelpFragment());
        fragments.add(new AttentionFragment());
        titles.add("聊天");
        titles.add("评论");
        titles.add("互助");
        titles.add("关注");

        for (int i = 0; i < titles.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
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
        refreshBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_refreshBtn:
                EventBus.getDefault().post(new MsgRefreshEvent());
                break;
        }
    }

}
