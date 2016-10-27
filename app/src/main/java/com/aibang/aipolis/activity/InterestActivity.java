package com.aibang.aipolis.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.InterestGridViewAdapter;
import com.aibang.aipolis.bean.Interest;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.bean.UserInterest;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 选择 兴趣页面
 * Created by zcf on 2016/5/30.
 */
public class InterestActivity extends AppCompatActivity {

    private GridView mGridView;
    private InterestGridViewAdapter mAdapter;
    private List<Interest> dataList = new ArrayList<>();//所有的兴趣类型

    private List<Interest> chooseInterestList = new ArrayList<>();//现在重新选择的兴趣
    private List<UserInterest> selectedList = new ArrayList<>();//已经选择的兴趣
    private List<UserInterest> delList = new ArrayList<>();//需要删除的兴趣
    private List<UserInterest> newAddList = new ArrayList<>();//新添加的兴趣

    List<Interest> interestList = new ArrayList<>();
    private boolean isSelect = false;
    private int isFinish = 0;//判断添加兴趣是否完成
    private User user;

    private boolean isClickBtn = false;//判断是否点击了完成按钮


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        TextView titleTv = (TextView) findViewById(R.id.id_top_title);
        assert titleTv != null;
        titleTv.setText(R.string.please_choose_your_interest);
        mGridView = (GridView) findViewById(R.id.id_grid_view);
        mAdapter = new InterestGridViewAdapter(this, dataList, selectedList,
                R.layout.grid_item_interest);
        mGridView.setAdapter(mAdapter);
        user = BmobUser.getCurrentUser(InterestActivity.this, User.class);
        querySelectInterest();//先查询用户之前选了那些兴趣
        queryInterest();


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSelect = true;
                delList.clear();
                newAddList.clear();
                ImageView imageView = (ImageView) view.findViewById(R.id.id_select_img);

                if (chooseInterestList.contains(dataList.get(position))) {
                    imageView.setImageResource(R.drawable.interest_normal);
                    chooseInterestList.remove(dataList.get(position));
                    unSubscribeInterest(dataList.get(position).getName());//退订


                } else {
                    imageView.setImageResource(R.drawable.interest_selected);
                    chooseInterestList.add(dataList.get(position));
                    subscribeInterest(dataList.get(position).getName());//订阅
                }


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
                        dataList.add(interest);

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
     * 查询 用户选择了那些兴趣兴趣列表
     */
    private void querySelectInterest() {
        BmobQuery<UserInterest> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        query.include("interest");
        query.findObjects(this, new FindListener<UserInterest>() {
            @Override
            public void onSuccess(List<UserInterest> list) {
                selectedList.clear();
                chooseInterestList.clear();
                if (list.size() > 0) {
                    for (UserInterest ui : list) {
                        selectedList.add(ui);
                        interestList.add(ui.getInterest());
                        chooseInterestList.add(ui.getInterest());
                    }

                } else {
                    Toast.makeText(InterestActivity.this, "您还为选择任何兴趣", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(InterestActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 订阅兴趣 方便推送
     */
    private void subscribeInterest(String name) {
        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
        //订阅新的兴趣
        installation.subscribe(name);
        installation.save();


    }

    /**
     * 订阅兴趣 方便推送
     */
    private void unSubscribeInterest(String name) {
        //退订不要的兴趣
        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
        installation.unsubscribe(name);
        installation.save();
    }


    /**
     * 获取需要删除  和 新增 的兴趣
     *
     * @return
     */
    public void getDelAndNewList() {
        User user = BmobUser.getCurrentUser(this, User.class);
        if (selectedList.size() > 0) {
            for (UserInterest i : selectedList) {
                if (!chooseInterestList.contains(i.getInterest())) {
                    delList.add(i);
                }
            }

            for (Interest i : chooseInterestList) {
                if (!interestList.contains(i)) {
                    UserInterest ui = new UserInterest(user, i);
                    newAddList.add(ui);
                }

            }

        } else {

            for (Interest i : chooseInterestList) {
                UserInterest ui = new UserInterest(user, i);
                newAddList.add(ui);
            }

        }

    }

    /**
     * 返回
     */
    public void back(View view) {
        if (!isClickBtn && isSelect)
            saveToService();

        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isClickBtn && isSelect)
            saveToService();
        super.onBackPressed();
    }

    /**
     * 完成按钮  监听事件
     */
    public void finish(View view) {
        if (!isSelect) {
            Toast.makeText(InterestActivity.this, "你没有修改兴趣", Toast.LENGTH_SHORT).show();
            return;
        }

        isClickBtn = true;
        saveToService();
    }


    /**
     * 更新 兴趣的总数量
     */

    private void updateNum(Interest i) {
        //否则 自动加1
        Interest interest = new Interest();
        interest.increment("sum");
        interest.update(this, i.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
    }


    /**
     * 将兴趣  删除 或者保存 到服务器
     */
    private void saveToService() {
        getDelAndNewList();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在更新兴趣...");
        dialog.show();
        //删除不要的兴趣
        if (delList.size() > 0) {
            for (final UserInterest ui : delList) {
                ui.delete(this, new DeleteListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }
        }

        //添加新的兴趣
        if (newAddList.size() > 0) {
            for (final UserInterest ui : newAddList) {
                ui.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        isFinish++;
                        updateNum(ui.getInterest());//数量加1
                        if (isFinish >= newAddList.size()) {
                            if (dialog != null)
                                dialog.dismiss();
                            Toast.makeText(InterestActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
//                            mHandler.sendEmptyMessage(110);//订阅兴趣
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        isFinish++;
                        if (dialog != null)
                            dialog.dismiss();
//                        Toast.makeText(InterestActivity.this, "设置失败" + s, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        } else {
            if (dialog != null)
                dialog.dismiss();
            Toast.makeText(InterestActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}


//    /**
//     * 订阅兴趣 方便推送
//     */
//    private void subInterest() {
//        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
//        //订阅新的兴趣
//        if (newAddList.size()>0){
//            for (UserInterest ui : newAddList){
//                installation.subscribe(ui.getInterest().getName());
//                Log.d("xingqu007",ui.getInterest().getName());
//            }
//
////            installation.save();
//
//            installation.save(InterestActivity.this, new SaveListener() {
//                @Override
//                public void onSuccess() {
//                    Log.d("xingqu007","*****************订阅成功");
//
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    Log.d("xingqu007","*****************订阅失败 "+s);
//                }
//            });
//        }else {
//            Log.d("xingqu007","新订阅兴趣为 0");
//        }
//
//    }
//
//
//    /**
//     * 取消订阅兴趣
//     */
//    private void delInterest() {
//        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
//        //订阅新的兴趣
//        if (delList.size()>0){
//            for (UserInterest ui : delList){
//                installation.unsubscribe(ui.getInterest().getName());
//                Log.d("xingqu007",ui.getInterest().getName());
//            }
//
//            installation.save();
////            installation.save(InterestActivity.this, new SaveListener() {
////                @Override
////                public void onSuccess() {
////                    Log.d("xingqu007","-------------取消成功");
////                }
////
////                @Override
////                public void onFailure(int i, String s) {
////                    Log.d("xingqu007","---------取消失败"+s);
////                }
////            });
//        }else {
//            Log.d("xingqu007","新取消兴趣为 0");
//        }
//
//    }


//
//    /**
//     * 添加兴趣
//     */
//    private void saveInterest(UserInterest ui) {
//        ui.save(InterestActivity.this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//
//            }
//        });
//    }
//
//    /**
//     * 删除兴趣
//     */
//    private void delInterest(UserInterest ui) {
//        ui.delete(InterestActivity.this, new DeleteListener() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//
//            }
//        });
//    }