package com.aibang.aipolis.fragment.homepage;

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
import com.aibang.aipolis.activity.PersonalHomepageActivity;
import com.aibang.aipolis.activity.ShowHelpContentActivity;
import com.aibang.aipolis.adapter.NeedInviteAdapter;
import com.aibang.aipolis.bean.Help;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.InviteEvent;
import com.aibang.aipolis.fragment.help.NeedHelpFragment;
import com.aibang.aipolis.view.ISLoadMoreFooterView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 个人主页 邀约
 * Created by zcf on 2016/6/3.
 */
public class HomeNeedInviteFragment extends Fragment {
    private ListView mListView;//
    private PtrFrameLayout mPtrFrame;//下拉刷新控件
    // 加载更多
    LoadMoreListViewContainer loadMoreListViewContainer;

    private NeedInviteAdapter mAdapter;//适配器
    private List<Help> helpData = new ArrayList<>();//数据集

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 20;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";//上一次加载 最后一条数据的时间
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_need_invite, container, false);
        EventBus.getDefault().register(this);
        PersonalHomepageActivity activity = (PersonalHomepageActivity) getActivity();
        user = activity.getUser();


        initPullToRefresh(view);
        initLoadMore(view);

        //点击
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowHelpContentActivity.class);
                intent.putExtra(NeedHelpFragment.HELP_DATA, helpData.get(position));
                startActivity(intent);
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
     * @param event 事件
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(InviteEvent event) {
        queryHelpData(0,STATE_REFRESH);
    }


    /**
     * 初始化 下拉刷新
     */
    private void initPullToRefresh(View view) {
        mPtrFrame = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getContext());
        mListView = (ListView) view.findViewById(R.id.id_index_list);

        mAdapter = new NeedInviteAdapter(getActivity(), helpData, R.layout.list_item_need_invite);
        // using string array from resource xml file
        header.initWithString("Aipolis");
//        header.initWithStringArray(R.array.storehouse);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.post(new Runnable() {
                    @Override
                    public void run() {
                        // 下拉刷新(从第一页开始装载数据)
                        queryHelpData(0, STATE_REFRESH);

                    }
                });
            }
        });
    }

    /**
     * 初始化 加载更多
     */
    private void initLoadMore(View view) {
        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_list_view_container);
        assert loadMoreListViewContainer != null;
//        loadMoreListViewContainer.useDefaultHeader();
        ISLoadMoreFooterView footerView = new ISLoadMoreFooterView(getContext());
        footerView.setVisibility(View.GONE);
        loadMoreListViewContainer.setLoadMoreView(footerView);
        loadMoreListViewContainer.setLoadMoreUIHandler(footerView);

        mListView.setAdapter(mAdapter);
        loadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //查询下一页数据
                queryHelpData(curPage, STATE_MORE);
            }
        });
    }


    /**
     * 分页获取  邀约 数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryHelpData(int page, final int actionType) {
        BmobQuery<Help> query = new BmobQuery<>();
        // 按时间降序查询
        query.include("user");
        query.order("-createdAt");
        query.addWhereEqualTo("type", false);//这个代表 邀约
        if (user!=null){
            query.addWhereEqualTo("user",user);
        }
        // 设置每页数据个数
        query.setLimit(limit);
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 只查询小于等于最后一个item发表时间的数据
//            if (date == null) {
//                showToast("请检查网络");
//                mPtrFrame.refreshComplete();
//                return;
//            }
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
//            Log.d("time00",new BmobDate(date)+"");
            // 跳过之前页数并去掉重复数据
            query.setSkip((page - 1) * limit);
//            query.setSkip(page * limit + 1);
        } else {
            page = 0;
            query.setSkip(page);
        }

        // 查找数据
        query.findObjects(getActivity(), new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                if (list.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        helpData.clear();
                        // 获取最后时间
                        lastTime = list.get(list.size() - 1).getCreatedAt();
                    }

                    // 将本次查询的数据添加到bankCards中
                    for (Help lb : list) {
                        helpData.add(lb);
                    }


                    mAdapter.notifyDataSetChanged();

                    // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                    curPage++;
                    // 数据加载完后，设置是否为空，是否有更多
                    loadMoreListViewContainer.loadMoreFinish(false, true);

//                    showToast("第" + (page + 1) + "页数据加载完成");
//                            showToast("数据加载完成");
                } else if (actionType == STATE_MORE) {
                    // 数据加载完后，设置是否为空，是否有更多
                    loadMoreListViewContainer.loadMoreFinish(false, false);
                    showToast("没有更多数据了");
                } else if (actionType == STATE_REFRESH) {
                    // 数据加载完后，设置是否为空，是否有更多
                    loadMoreListViewContainer.loadMoreFinish(true, false);

                }
                mPtrFrame.refreshComplete();
            }

            @Override
            public void onError(int arg0, String arg1) {
                showToast(arg1);
                mPtrFrame.refreshComplete();
            }
        });
    }


    /**
     * 弹出 吐司
     *
     * @param msg
     */

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}
