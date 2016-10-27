package com.aibang.aipolis.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.ShowHelpContentActivity;
import com.aibang.aipolis.adapter.MsgHelpCommentAdapter;
import com.aibang.aipolis.bean.HelpComment;
import com.aibang.aipolis.bean.Life;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.MsgRefreshEvent;
import com.aibang.aipolis.utils.CustomToast;
import com.aibang.aipolis.view.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 互助  评论页面
 * Created by zcf on 2016/4/30.
 */
public class HelpFragment extends Fragment {
    private ListView commentListView;
    private MsgHelpCommentAdapter mAdapter;

    private List<HelpComment> commentList = new ArrayList<>();
    public static String COMMENT_HELP_DATA = "comment_help_data";
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_msg_help, container, false);
        initView(view);
        user = BmobUser.getCurrentUser(getActivity(), User.class);
        //不为空才查询消息
        if (user != null) {
            queryCommentMsg();//查询相互评论
            queryOnlyComment();//查询 单独评论
        } else {
            showToast(getString(R.string.please_login));
        }

        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getActivity(), ShowHelpContentActivity.class);
                intent.putExtra(COMMENT_HELP_DATA, commentList.get(position).getHelp());
                startActivity(intent);
                updateIsRead(commentList.get(position).getObjectId(), position);
            }
        });



        commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                ConfirmDialog dialog = new ConfirmDialog(getActivity());
                dialog.setTopTitle("提醒");
                dialog.setOkText("确认");
                dialog.setCancelText("取消");
                dialog.setContentText("确定要删除这条消息吗?");
                dialog.show();
                dialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOKClick() {
                        updateIsRead(commentList.get(position).getObjectId(), position);
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });

                return true;
            }
        });
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /**
     * 更新数据
     *
     * @param event 事件
     */


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgRefreshEvent event) {
        user = BmobUser.getCurrentUser(getActivity(), User.class);
        if (user != null) {
            commentList.clear();
            queryCommentMsg();//查询相互评论
            queryOnlyComment();//查询 单独评论
        }
    }

    /**
     * 更新是否已经读过该条消息
     */
    private void updateIsRead(String id, final int position) {
        HelpComment comment = new HelpComment();
        comment.setRead(true);
        comment.update(getActivity(), id, new UpdateListener() {
            @Override
            public void onSuccess() {
                commentList.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 初始化 控件
     *
     * @param view 布局
     */
    private void initView(View view) {
        commentListView = (ListView) view.findViewById(R.id.id_msg_comment_listView);
        mAdapter = new MsgHelpCommentAdapter(getActivity(), commentList, R.layout.list_item_msg_comment, user);
        commentListView.setAdapter(mAdapter);
    }


    /**
     * 查询  有人回复 的评论(即相互评论)
     */
    public void queryCommentMsg() {
        BmobQuery<HelpComment> query = new BmobQuery<>();
        query.addWhereEqualTo("beiCommentUser", user);
        query.include("commentUser,beiCommentUser.nickName,help.content");
        query.addWhereEqualTo("isRead", false);
        query.order("-createdAt");
        query.findObjects(getActivity(), new FindListener<HelpComment>() {
            @Override
            public void onSuccess(List<HelpComment> object) {
                if (object.size() > 0) {
                    for (HelpComment sc : object) {
                        if(sc!=null)
                        commentList.add(sc);
                    }
                    //添加数据
                    commentListView.setAdapter(mAdapter);
//                    EventBus.getDefault().post(new SetNotificationEvent());//通知主界面设置数字
                }
            }

            @Override
            public void onError(int code, String msg) {
                CustomToast.showToast(getActivity(), "查询消息失败", Toast.LENGTH_SHORT);
            }
        });
    }


    /**
     * 查询单独评论
     */
    private void queryOnlyComment() {
        BmobQuery<HelpComment> query = new BmobQuery<>();
        BmobQuery<Life> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("user", user);
        query.order("-createdAt");
        query.include("commentUser,beiCommentUser.nickName,help");
        query.addWhereEqualTo("isRead", false);
        //        Log.d("TAG", user.getNickName());
        // 第一个参数为评论表中的帖子字段名post
        // 第二个参数为shenghuo字段的表名，也可以直接用"L"字符串的形式
        // 第三个参数为内部查询条件
        query.addWhereMatchesQuery("help", "Help", innerQuery);
        query.findObjects(getActivity(), new FindListener<HelpComment>() {
            @Override
            public void onSuccess(List<HelpComment> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    for (HelpComment s : object) {
                        if(s.getBeiCommentUser()==null)
                        commentList.add(s);
                    }
//                    EventBus.getDefault().post(new SetNotificationEvent());//通知主界面设置数字
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                showToast("查询失败:" + msg);
            }
        });
    }

    /**
     * 土司
     *
     * @param s 内容
     */
    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }
}