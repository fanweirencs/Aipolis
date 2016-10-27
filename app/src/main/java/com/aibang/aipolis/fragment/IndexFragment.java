package com.aibang.aipolis.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.MainActivity;
import com.aibang.aipolis.activity.ShowIndexContentActivity;
import com.aibang.aipolis.adapter.IndexAdapter;
import com.aibang.aipolis.bean.ShouyeArticle;
import com.aibang.aipolis.view.ISLoadMoreFooterView;

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
 * 首页
 * Created by zcf on 2016/4/28.
 */
public class IndexFragment extends Fragment {
    private ListView mListView;//
    private PtrFrameLayout mPtrFrame;//下拉刷新控件
    // 加载更多
    LoadMoreListViewContainer loadMoreListViewContainer;

    private IndexAdapter mAdapter;//适配器
    private List<ShouyeArticle> articleList = new ArrayList<>();//数据集

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 10;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";//上一次加载时间
    private boolean isFirstLoad = true;//是否是首次加载

    public static final String ARTICLE_DATA = "article_data";
    private Button openDrawerBtn;//打开抽屉


    private int lastVisiblePosition;//上一个可见item 位置

    private RelativeLayout topLy;//顶部监听 长按回到顶部
    //是否 只提醒一次
    private boolean isOnlyOnceToast = true;

    //双击事件变量
    int count = 0;
    int firClick = 0;
    int secClick = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);

        //初始化下拉刷新 自动查询首页文章
        initPullToRefresh(view);
        initLoadMore(view);
        initView(view);
        onDoubleClick();
        //点击
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowIndexContentActivity.class);
                intent.putExtra(ARTICLE_DATA, articleList.get(position));
                startActivity(intent);

            }
        });
        return view;
    }


    /**
     * 双击事件监听
     */

    private void onDoubleClick() {
        topLy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    count++;
                    if (count == 1) {
                        firClick = (int) System.currentTimeMillis();

                    } else if (count == 2) {
                        secClick = (int) System.currentTimeMillis();
                        if (secClick - firClick < 1000) {
                            //双击事件
                            if (articleList.size() > 0)
                                mListView.setSelection(0);
                            Toast.makeText(getActivity(), "回到顶部", Toast.LENGTH_SHORT).show();
                        }
                        count = 0;
                        firClick = 0;
                        secClick = 0;
                    }
                }
                return true;

            }
        });
    }

    /**
     * 初始化常用控件
     */
    private void initView(View view) {

        topLy = (RelativeLayout) view.findViewById(R.id.id_topLy);
        openDrawerBtn = (Button) view.findViewById(R.id.id_menu_btn);
        //打开抽屉
        openDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.openDrawer();
            }
        });

    }

    /**
     * 初始化 下拉刷新
     */
    private void initPullToRefresh(View view) {
        mPtrFrame = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getContext());
        mListView = (ListView) view.findViewById(R.id.id_index_list);

        mAdapter = new IndexAdapter(getActivity(), articleList, R.layout.list_item_index);
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
        }, 1);
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
                        queryArticleData(0, STATE_REFRESH);

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

        loadMoreListViewContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //滑动停止
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (lastVisiblePosition >= 30 && isOnlyOnceToast) {
                            isOnlyOnceToast = false;
                            Toast.makeText(getActivity(), R.string.double_to_top, Toast.LENGTH_LONG).show();
                        }
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

        //使用自定义 底部视图
        ISLoadMoreFooterView footerView = new ISLoadMoreFooterView(getContext());
        footerView.setVisibility(View.GONE);
        loadMoreListViewContainer.setLoadMoreView(footerView);
        loadMoreListViewContainer.setLoadMoreUIHandler(footerView);

        loadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //查询下一页数据
                queryArticleData(curPage, STATE_MORE);
            }
        });
    }

    /**
     * 分页获取  首页文章 数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryArticleData(int page, final int actionType) {
        final BmobQuery<ShouyeArticle> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        query.include("zuozhe.nickName");
        //查询指定列
//        bmobQuery.addQueryKeys("objectId");
        query.addQueryKeys("objectId,title,createdAt,zanNum,imgUrl,commentNum,createdAt,isVideo,zuozhe,videoUrl,lookSum");

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

            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 只查询小于等于最后一个item发表时间的数据
//            if (date == null) {
//                showToast("请检查网络");
//                mPtrFrame.refreshComplete();
//                return;
//            }

            // 跳过之前页数并去掉重复数据
            query.setSkip((page-1 ) * limit);
//            query.setSkip(page * limit + 1);
//            query.setSkip(page * limit + 1);
        } else {
            page = 0;
            query.setSkip(page);
        }
        // 查找数据
        query.findObjects(getActivity(), new FindListener<ShouyeArticle>() {
                    @Override
                    public void onSuccess(List<ShouyeArticle> list) {
                        if (list.size() > 0) {
                            if (actionType == STATE_REFRESH) {
                                // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                                curPage = 0;
                                articleList.clear();
                                // 获取最后时间
                                lastTime = list.get(list.size() - 1).getCreatedAt();
                            }

                            // 将本次查询的数据添加到bankCards中
                            for (ShouyeArticle lb : list) {
                                articleList.add(lb);
                            }


                            if (isFirstLoad) {
                                mListView.setAdapter(mAdapter);
                                isFirstLoad = false;
//                        showToast("第一次加载");
                            } else {
                                mAdapter.notifyDataSetChanged();
//                        showToast("不是首次次加载");
                            }


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
                            showToast("还没有数据,快去发布吧！");

                        }
                        mPtrFrame.refreshComplete();
                    }

                    @Override
                    public void onError(int arg0, String arg1) {
                        showToast(arg1);
                        mPtrFrame.refreshComplete();
                    }
                }

        );
    }

    /**
     * 弹出 吐司
     */

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}
