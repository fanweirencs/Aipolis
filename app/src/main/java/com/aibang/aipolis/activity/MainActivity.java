package com.aibang.aipolis.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import com.orhanobut.logger.Logger;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.Guanzhu;
import com.aibang.aipolis.bean.MyBmobInstallation;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.LoginEvent;
import com.aibang.aipolis.event.SetNotificationEvent;
import com.aibang.aipolis.event.UpdateHeadEvent;
import  com.aibang.aipolis.event.RefreshEvent;
import com.aibang.aipolis.fragment.IndexFragment;
import com.aibang.aipolis.fragment.LifeFragment;
import com.aibang.aipolis.fragment.MessageFragment;
import com.aibang.aipolis.fragment.NewHelpFragment;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.exception.BmobException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ObseverListener {
    private DrawerLayout mDrawerLayout;//抽屉
    //    private NavigationView mNavigationView;//菜单
    private ListView mListView;//菜单
    private AHBottomNavigation bottomNavigation;//底部按钮
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

    private Fragment mIndexFragment;//首页
    private Fragment mHelpFragment;//互助
    private Fragment mLifeFragment;//生活
    private Fragment mInterestFragment;//兴趣
    private int position;//保存当前的fragment位置,防止重叠

    //头像
    private CircleImageView headImg;
    //名字
    private TextView nameTv;
    //学校
    private TextView schoolTv;

    private TextView exitTv;
    //                  主页  资料   印象      邀请     关于    设置
    private LinearLayout indexLy, dataLy, impressLy, inviteLy, aboutLy, settingLy, exitLy;
    // 我关注的人  关注我的人  收藏
    private LinearLayout attentionMeLy, meAttentionLy, likeLy;
    //   关注我的人              我关注的人  收藏
    private TextView followersTv, followingTv, likeTv;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BmobUpdateAgent.update(this);
        user = BmobUser.getCurrentUser(MainActivity.this, User.class);
        EventBus.getDefault().register(this);
        connect();
        initView();
        initDrawer();
        initBottom();
        showUserInfo();
    }
    public void connect(){
        if(user!=null){
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getBaseContext(),"connect success",Toast.LENGTH_SHORT).show();
                        // Logger.i("connect success");
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        EventBus.getDefault().post(new RefreshEvent());
                    } else {
                        Toast.makeText(getBaseContext(),"connect fail",Toast.LENGTH_SHORT).show();
                        Logger.e(e.getErrorCode() + "/" + e.getMessage());
                    }
                }
            });
        }
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
               Toast.makeText(getBaseContext(),"" + status.getMsg(),Toast.LENGTH_SHORT).show();
                if(status.getMsg().equals("connect fail"))
                    connect();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String s = getIntent().getStringExtra("style");
        if (s!=null){
            Log.d("style",s);
            if (s.equals("102")){
                bottomNavigation.setCurrentItem(1);
                setSelect(1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        BmobIM.getInstance().clear();
    }

    /**
     * 用户缓存存在 就显示用户信息
     */
    private void showUserInfo() {

        if (user != null) {
            nameTv.setText(user.getNickName());
            schoolTv.setText(user.getSchool());

            exitTv.setText("退出登录");
            if (!TextUtils.isEmpty(user.getAutographUrl()))
                ImageLoader.getInstance().displayImage(user.getAutographUrl(), headImg);
            queryFollower(user);
            queryFollowing(user);
//            realTimeListener();//监听user表
        } else {
            schoolTv.setText(getString(R.string.aipolis));
            exitTv.setText("点击登录");
        }

    }
    /**
     * 加载 用户头像 姓名 个性签名
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LoginEvent event) {
        user = BmobUser.getCurrentUser(MainActivity.this, User.class);
        updateUid();
        nameTv.setText(user.getNickName());
        schoolTv.setText(user.getSchool());
        exitTv.setText("退出登录");
        if (!TextUtils.isEmpty(user.getAutographUrl()))
            ImageLoader.getInstance().displayImage(user.getAutographUrl(), headImg);
        // followingNumber;//我关注的人数
        //followersNumber;//关注我的人数
        queryFollower(user);
        queryFollowing(user);
    }
    /**
     * 更新 用户头像
     *
     * @param event 事件
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateHeadEvent event) {
        String url = event.getHeadUrl();
        user = BmobUser.getCurrentUser(MainActivity.this, User.class);
        user.setAutographUrl(url);
        ImageLoader.getInstance().displayImage(url, headImg);
    }
    /**
     * 更新 消息 切换到消息界面
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SetNotificationEvent event) {
        bottomNavigation.setNotification(1, 3);
    }
    /**
     * 查询关注我的人数
     * 这是设计的时候没把这个加进去 导致出现问题
     */
    private void queryFollower(final User user) {
        BmobQuery<Guanzhu> query = new BmobQuery<>();
        query.addWhereEqualTo("beiguanzhu", user);
        query.count(this, Guanzhu.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                followersTv.setText(i + "");
                User newUser = new User();
                newUser.setFollowersNumber(i);
                newUser.update(MainActivity.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 查询我关注的人数
     * 这是设计的时候没把这个加进去 导致出现问题
     */
    private void queryFollowing(final User user) {
        BmobQuery<Guanzhu> query = new BmobQuery<>();
        query.addWhereEqualTo("guanzhu", user);
        query.count(this, Guanzhu.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                followingTv.setText(i + "");
                User newUser = new User();
                newUser.setFollowersNumber(i);
                newUser.update(MainActivity.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }


    /**
     * 更新uid 方便推送
     */
    private void updateUid() {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    MyBmobInstallation mbi = object.get(0);
                    String str = User.getCurrentUser(MainActivity.this).getObjectId();
                    mbi.setUid(str);
                    mbi.update(MainActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新失败:" + msg);
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }


    /**
     * 找到所有控件
     */
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mListView = (ListView) findViewById(R.id.id_list_view);
        exitTv = (TextView) findViewById(R.id.id_exitTv);

//        mNavigationView = (NavigationView) findViewById(R.id.id_nv_menu);
//        setupDrawerContent(mNavigationView);
    }


    /**
     * 初始化 抽屉里的所有空间
     */
    private void initDrawer() {

        followersTv = (TextView) findViewById(R.id.id_follower_me);
        followingTv = (TextView) findViewById(R.id.id_me_follow);
        likeTv = (TextView) findViewById(R.id.id_likes);

        indexLy = (LinearLayout) findViewById(R.id.id_menu_indexLy);
        dataLy = (LinearLayout) findViewById(R.id.id_menu_dataLy);
        impressLy = (LinearLayout) findViewById(R.id.id_menu_impressLy);
        inviteLy = (LinearLayout) findViewById(R.id.id_menu_inviteLy);
        aboutLy = (LinearLayout) findViewById(R.id.id_menu_aboutLy);
//        settingLy = (LinearLayout) findViewById(R.id.id_menu_settingLy);

        exitLy = (LinearLayout) findViewById(R.id.id_menu_exitLy);

        attentionMeLy = (LinearLayout) findViewById(R.id.id_menu_attention_meLy);
        meAttentionLy = (LinearLayout) findViewById(R.id.id_my_attention_Ly);
        likeLy = (LinearLayout) findViewById(R.id.id_menu_myLikeLy);

        indexLy.setOnClickListener(this);
        dataLy.setOnClickListener(this);
        impressLy.setOnClickListener(this);
        inviteLy.setOnClickListener(this);
        aboutLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutAipolisActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
//        settingLy.setOnClickListener(this);
        exitLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    BmobUser.logOut(MainActivity.this);   //清除缓存用户对象
                    user = BmobUser.getCurrentUser(MainActivity.this, User.class);
                    nameTv.setText("未登录");
                    followingTv.setText("0");
                    followersTv.setText("0");
                    schoolTv.setText("桃花源");
                    headImg.setImageResource(R.mipmap.default_head_female);
                    exitTv.setText("点击登录");
                    Toast.makeText(getApplicationContext(), "退出成功 ", Toast.LENGTH_SHORT).show();
                } else {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

            }
        });

        attentionMeLy.setOnClickListener(this);
        meAttentionLy.setOnClickListener(this);
        likeLy.setOnClickListener(this);


        headImg = (CircleImageView) findViewById(R.id.id_user_head_img);
        nameTv = (TextView) findViewById(R.id.id_user_name);
        schoolTv = (TextView) findViewById(R.id.id_school);
        headImg.setOnClickListener(this);


    }

    /**
     * 初始化底部的按钮
     */
    private void initBottom() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_index, R.mipmap.ic_index, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_help, R.mipmap.ic_help, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_life, R.mipmap.ic_life, R.color.color_tab_3);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_msg, R.mipmap.ic_message, R.color.color_tab_4);


        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigationItems.add(item4);

        bottomNavigation.addItems(bottomNavigationItems);
        // Use colored navigation with circle reveal effect
//        设置底部背景是否变色
        bottomNavigation.setColored(true);
        bottomNavigation.setCurrentItem(0);

        setSelect(0);
        bottomNavigation.setAccentColor(Color.parseColor("#385fff"));
        //     底部图标的颜色
        bottomNavigation.setInactiveColor(Color.parseColor("#b0b0b0"));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Add or remove notification for each item 添加提示
//        bottomNavigation.setNotification(4, 1);

        // Force the titles to be displayed (against Material Design guidelines!)

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                setSelect(position);
                switch (position) {
                    case 0:
                        break;

                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:
                        bottomNavigation.setNotification(0, 3);
                        break;

                }
            }
        });


    }

    /**
     * 设置当前的fragment
     *
     * @param i
     */
    private void setSelect(int i) {
//        记录position
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);

        switch (i) {
            case 0:
                if (mIndexFragment == null) {
                    mIndexFragment = new IndexFragment();
                    transaction.add(R.id.id_content, mIndexFragment);
                } else {
                    transaction.show(mIndexFragment);
                }
                break;
            case 1:
                if (mHelpFragment == null) {
                    mHelpFragment = new NewHelpFragment();
                    transaction.add(R.id.id_content, mHelpFragment);
                } else {
                    transaction.show(mHelpFragment);

                }

                break;
            case 2:
                if (mLifeFragment == null) {
                    mLifeFragment = new LifeFragment();
                    transaction.add(R.id.id_content, mLifeFragment);
                } else {
                    transaction.show(mLifeFragment);
                }
                break;
            case 3:
                if (mInterestFragment == null) {
                    mInterestFragment = new MessageFragment();
                    transaction.add(R.id.id_content, mInterestFragment);
                } else {
                    transaction.show(mInterestFragment);
                }
                break;

            default:
                break;
        }
        transaction.commit();
//        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏所有的fragment
     *
     * @param transaction
     */

    private void hideFragment(FragmentTransaction transaction) {
        if (mIndexFragment != null) {
            transaction.hide(mIndexFragment);
        }
        if (mHelpFragment != null) {
            transaction.hide(mHelpFragment);
        }
        if (mLifeFragment != null) {
            transaction.hide(mLifeFragment);
        }
        if (mInterestFragment != null) {
            transaction.hide(mInterestFragment);
        }
    }

    /**
     * 打开或关闭抽屉
     */

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return false;
//        return super.onOptionsItemSelected(item);
    }

    /**
     * 物理菜单 按钮监听 打开或者关闭抽屉
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 在这里做你想做的事情
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回 true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
    }

    //    解决Fragment重叠的两个方法onRestoreInstanceState(),onSaveInstanceState()
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        position = savedInstanceState.getInt("position");
        setSelect(position);
        super.onRestoreInstanceState(savedInstanceState);
    }

    //解决Fragment重叠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //记录当前的position
        outState.putInt("position", position);
    }

    @Override
    public void onClick(View v) {
        mDrawerLayout.closeDrawer(GravityCompat.START);//关闭抽屉
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }
        switch (v.getId()) {
            //头像
            case R.id.id_user_head_img:
                Intent intent2 = new Intent(MainActivity.this, PersonalOtherHomepageActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                break;
            case R.id.id_menu_indexLy:
                Intent intent = new Intent(MainActivity.this, PersonalHomepageActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.id_menu_dataLy:
                startActivity(new Intent(MainActivity.this, MyInforActivity.class));
                break;
            case R.id.id_menu_impressLy:
                Intent intent3 = new Intent(MainActivity.this, ImpressActivity.class);
                intent3.putExtra("user", user);
                intent3.putExtra("hide", true);
                startActivity(intent3);
                break;
            case R.id.id_menu_inviteLy:
                showShare();
                break;

            //关注我的人
            case R.id.id_menu_attention_meLy:
                startActivity(new Intent(MainActivity.this, AttentionMeActivity.class));
                break;
            //我关注的人
            case R.id.id_my_attention_Ly:
                startActivity(new Intent(MainActivity.this, MyAttentionActivity.class));
                break;
            // 喜欢
            case R.id.id_menu_myLikeLy:
                Toast.makeText(MainActivity.this, "内容正在建设中", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    public void showShare(){
        new ShareAction(MainActivity.this)
                .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .withText("桃花源，专做大学生社交")
                .withMedia(new UMImage(getBaseContext(),R.mipmap.logo))
                .withTargetUrl("http://www.aipolis.com/download.html")
                .open();
    }
}
