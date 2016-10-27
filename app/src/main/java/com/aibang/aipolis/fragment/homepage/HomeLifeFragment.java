package com.aibang.aipolis.fragment.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.PersonalHomepageActivity;
import com.aibang.aipolis.activity.ShowLifeContentActivity;
import com.aibang.aipolis.adapter.LifeAdapter;
import com.aibang.aipolis.bean.Life;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.LifeEvent;
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
 * 个人主页 生活页面
 * Created by zcf on 2016/6/3.
 */
public class HomeLifeFragment extends Fragment {
    private ListView mListView;//
    private PtrFrameLayout mPtrFrame;//下拉刷新控件
    // 加载更多
    LoadMoreListViewContainer loadMoreListViewContainer;

    private LifeAdapter mAdapter;//适配器
    private List<Life> lifeData = new ArrayList<>();//数据集

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 10;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";//上一次加载 最后一条数据的时间
    public static final String LIFE_DATA = "life_data";
    private User user;
    private int lastVisiblePosition;//上一个可见item 位置


//    private RelativeLayout topLy;//顶部监听 长按回到顶部

    //是否 只提醒一次
//    private boolean isOnlyOnceToast = true;

    //双击事件变量
    int count = 0;
    int firClick = 0;
    int secClick = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_life, container, false);
        EventBus.getDefault().register(this);
        PersonalHomepageActivity activity = (PersonalHomepageActivity) getActivity();
        user = activity.getUser();
        initView(view);
//        onDoubleClick();
        initPullToRefresh(view);
        initLoadMore(view);
        //点击
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowLifeContentActivity.class);
                intent.putExtra(LIFE_DATA, lifeData.get(position));
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
     * 双击事件监听
     */

//    private void onDoubleClick() {
//        topLy.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_DOWN == event.getAction()) {
//                    count++;
//                    if (count == 1) {
//                        firClick = (int) System.currentTimeMillis();
//
//                    } else if (count == 2) {
//                        secClick = (int) System.currentTimeMillis();
//                        if (secClick - firClick < 1000) {
//                            //双击事件
//                            if (lifeData.size() > 0)
//                                mListView.setSelection(0);
//                            Toast.makeText(getActivity(), "回到顶部", Toast.LENGTH_SHORT).show();
//                        }
//                        count = 0;
//                        firClick = 0;
//                        secClick = 0;
//                    }
//                }
//                return true;
//
//            }
//        });
//    }

    /**
     * 发布之后更新数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LifeEvent event) {
        queryLifeData(0, STATE_REFRESH);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void updateLifeData(LifeEvent event) {
//        queryLifeData(0, STATE_REFRESH);
//    }

    private void initView(View view) {
//        topLy = (RelativeLayout) view.findViewById(R.id.id_topLy);


    }


    /**
     * 初始化 下拉刷新
     */
    private void initPullToRefresh(View view) {
        mPtrFrame = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getContext());
        mListView = (ListView) view.findViewById(R.id.id_index_list);

        mAdapter = new LifeAdapter(getActivity(), lifeData, R.layout.list_item_life_with_img);
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
                        queryLifeData(0, STATE_REFRESH);

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
        //添加底部视图
        ISLoadMoreFooterView footerView = new ISLoadMoreFooterView(getContext());
        footerView.setVisibility(View.GONE);
        loadMoreListViewContainer.setLoadMoreView(footerView);
        loadMoreListViewContainer.setLoadMoreUIHandler(footerView);


        //
        mListView.setAdapter(mAdapter);

        loadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //查询下一页数据
                queryLifeData(curPage, STATE_MORE);
            }
        });
        loadMoreListViewContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //滑动停止
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                        if (lastVisiblePosition >= 30 && isOnlyOnceToast) {
//                            isOnlyOnceToast = false;
//                            Toast.makeText(getActivity(), R.string.double_to_top, Toast.LENGTH_LONG).show();
//                        }
                        break;
                    //正在滚动
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                    //手指抛动时滚动
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastVisiblePosition = firstVisibleItem + visibleItemCount;

            }
        });
    }


    /**
     * 分页获取  生活 数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryLifeData(int page, final int actionType) {
        BmobQuery<Life> query = new BmobQuery<>();
        // 按时间降序查询
        query.include("user");
        query.order("-createdAt");
        // 设置每页数据个数
        query.setLimit(limit);
        if (user != null)
            query.addWhereEqualTo("user", user);
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

            // 跳过之前页数并去掉重复数据
            query.setSkip((page - 1) * limit);
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
//            Log.d("time00",lastTime+"   跳过的数据   "+(page * limit));

        } else {
            page = 0;
            query.setSkip(page);
        }

        // 查找数据
        query.findObjects(getActivity(), new FindListener<Life>() {
            @Override
            public void onSuccess(List<Life> list) {
                if (list.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        lifeData.clear();
                        // 获取最后时间
                        lastTime = list.get(list.size() - 1).getCreatedAt();
                    }

                    // 将本次查询的数据添加到bankCards中
                    for (Life lb : list) {
                        lifeData.add(lb);
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
//                    showToast("还没有数据,快去发布吧！");

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
