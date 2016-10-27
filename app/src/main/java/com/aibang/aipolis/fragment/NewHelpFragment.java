package com.aibang.aipolis.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.LoginActivity;
import com.aibang.aipolis.activity.MainActivity;
import com.aibang.aipolis.activity.PubHelpActivity;
import com.aibang.aipolis.activity.PubInviteActivity;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.fragment.help.NeedHelpFragment;
import com.aibang.aipolis.fragment.help.NeedInviteFragment;
import com.aibang.aipolis.view.ConfirmDialog;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * 帮助
 * Created by zcf on 2016/5/8.
 */
public class NewHelpFragment extends Fragment {
    private SegmentTabLayout mstLayout;//互助 邀约
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private static final String mTitles[] = {"求助", "邀约"};
    private Button openDrawerBtn;//打开抽屉
    private ImageButton pubHelpBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_help, container, false);
        initView(view);

        return view;
    }


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

        pubHelpBtn = (ImageButton)view.findViewById(R.id.id_pub) ;
        pubHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(getActivity(),User.class);
                if (user==null){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                showChooseDialog();
//                if (String.valueOf(user.getShenfen()).equals("2")){
//                    //提示被封号
//                    showCloseUserDialog();
//                }else {
//                    showChooseDialog();
//                }


            }
        });


        initViewPager(view);
        mstLayout = (SegmentTabLayout) view.findViewById(R.id.id_stLayout);
        mstLayout.setTabData(mTitles);//设置标题
        //显示未读红点
        mstLayout.showDot(1);
        mstLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }


    /**
     * 初始化view pager
     */
    private void initViewPager(View view) {
        //添加邀约 和互助 这两个fragment
        mFragments.add(new NeedHelpFragment());
        mFragments.add(new NeedInviteFragment());

        mViewPager = (ViewPager) view.findViewById(R.id.id_vp);
        mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mstLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }



    /**
     * 显示需要发布的类型 是求助还是邀约
     */
    private void showChooseDialog(){

        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContentText("请选择类型");
        dialog.setTopTitle("提示");
        dialog.setOkText("邀约");
        dialog.setCancelText("求助");
        dialog.show();
        dialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                startActivity(new Intent(getActivity(), PubInviteActivity.class));
            }

            @Override
            public void onCancelClick() {
                startActivity(new Intent(getActivity(), PubHelpActivity.class));
            }
        });
    }
    /**
     * 显示被封号
     */
//    private void showCloseUserDialog(){
//
//        ConfirmDialog dialog = new ConfirmDialog(getActivity());
//        dialog.setContentText("您的账户目前被多次举报，暂时不能发言");
//        dialog.setTopTitle("提示");
//        dialog.setCancelVisible(View.GONE);
//        dialog.setOkText("确定");
//        dialog.show();
//    }

}
