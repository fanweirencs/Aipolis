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
import com.aibang.aipolis.activity.PersonalOtherHomepageActivity;
import com.aibang.aipolis.adapter.MsgAttentionCommentAdapter;
import com.aibang.aipolis.bean.Guanzhu;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.MsgRefreshEvent;
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
 * 关注页面
 * Created by zcf on 2016/5/16.
 */
public class AttentionFragment extends Fragment {
    private ListView commentListView;
    private MsgAttentionCommentAdapter mAdapter;

    private List<Guanzhu> commentList = new ArrayList<>();

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        View view = inflater.inflate(R.layout.fragment_msg_attention, container, false);
        initView(view);
        user = BmobUser.getCurrentUser(getActivity(), User.class);
        //不为空才查询消息
        if (user != null) {
            queryAttentionMe();//查询
        }
        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getActivity(), PersonalOtherHomepageActivity.class);
                intent.putExtra("user", commentList.get(position).getGuanzhu());
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

//    @Override
//    public void onStart() {
//        super.onStart();
//        //      EventBus.getDefault().register(this);
//    }

    /**
     * 更新数据
     *
     * @param event 事件
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgRefreshEvent event) {
        user = BmobUser.getCurrentUser(getActivity(), User.class);
        //不为空才查询消息
        if (user != null) {
            commentList.clear();
            queryAttentionMe();//查询
        }
    }

    /**
     * 初始化 控件
     *
     * @param view 布局
     */
    private void initView(View view) {
        commentListView = (ListView) view.findViewById(R.id.id_msg_comment_listView);

        mAdapter = new MsgAttentionCommentAdapter(getActivity(), commentList, R.layout.list_item_msg_attention);
        commentListView.setAdapter(mAdapter);
    }

    /**
     * 查询 关注我的人
     */
    private void queryAttentionMe() {
        BmobQuery<Guanzhu> query = new BmobQuery<>();
        query.addWhereEqualTo("beiguanzhu", user);
        query.order("-createdAt");
        query.include("guanzhu");
        query.addWhereEqualTo("isRead", false);

        query.findObjects(getActivity(), new FindListener<Guanzhu>() {
            @Override
            public void onSuccess(List<Guanzhu> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    for (Guanzhu s : object) {
                        commentList.add(s);
                    }
                    mAdapter.notifyDataSetChanged();
//                    EventBus.getDefault().post(new SetNotificationEvent());//通知主界面设置数字
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
     * 更新是否已经读过该条消息
     */
    private void updateIsRead(String id, final int position) {
        Guanzhu guanzhu = new Guanzhu();
        guanzhu.setRead(true);
        guanzhu.update(getActivity(), id, new UpdateListener() {
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
     * 土司
     */
    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }
}