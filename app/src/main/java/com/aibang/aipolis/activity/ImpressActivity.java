package com.aibang.aipolis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.ImpressAdapter;
import com.aibang.aipolis.bean.Impression;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.view.ISLoadMoreFooterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 印象界面
 * Created by zcf on 2016/6/4.
 */
public class ImpressActivity extends AppCompatActivity {
    private ListView mListView;
    private PtrFrameLayout mPtrFrame;//下拉刷新控件

    private LoadMoreListViewContainer loadMoreListViewContainer;// 加载更多
    private ImpressAdapter mAdapter;
    private List<Impression> impressionList = new ArrayList<>();
    private Button sendBtn;//发送评论按钮

    private EditText commentEt;//印象内容

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 20;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";//上一次加载 最后一条数据的时间

    private User me;

    private User beiUser;
    private LinearLayout commentLy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impress);
        me = BmobUser.getCurrentUser(this, User.class);
        beiUser = (User) getIntent().getSerializableExtra("user");
        boolean isHide = getIntent().getBooleanExtra("hide", false);

        initViews();

        if (isHide) {
            commentLy.setVisibility(View.GONE);
        }
        initPullToRefresh();
        initLoadMore();
        queryImpress(0, STATE_REFRESH);

    }


    private void initViews() {
        //标题
        ((TextView) findViewById(R.id.id_top_title)).setText("印象");
        commentLy = (LinearLayout) findViewById(R.id.id_commentLy);
        commentEt = (EditText) findViewById(R.id.id_comment_et);
        sendBtn = (Button) findViewById(R.id.id_send);
        mListView = (ListView) findViewById(R.id.id_list_view);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImpress();
            }
        });
    }


    /**
     * 查询评论
     */
    public void queryImpress(int page, final int actionType) {
        BmobQuery<Impression> query = new BmobQuery<>();
        query.addWhereEqualTo("beiImpress", beiUser);
        query.include("impress");
        // 按时间降序查询
        query.order("-createdAt");
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
//            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            //防止用户直接下拉 导致时间 未加载出来 报空指针异常
            if (date != null)
                query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            else
                return;
//             跳过之前页数并去掉重复数据
            query.setSkip((page - 1) * limit);
        } else {
            page = 0;
            query.setSkip(page);
        }


        query.findObjects(this, new FindListener<Impression>() {
            @Override
            public void onSuccess(List<Impression> object) {

                if (object.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        impressionList.clear();
                        // 获取最后时间
                        lastTime = object.get(object.size() - 1).getCreatedAt();
                    }

                    // 将本次查询的数据添加到bankCards中
                    for (Impression g : object) {
                        impressionList.add(g);
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
                } else if (actionType == STATE_REFRESH) {
                    // 数据加载完后，设置是否为空，是否有更多
                    loadMoreListViewContainer.loadMoreFinish(true, false);

                }
                mPtrFrame.refreshComplete();
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                showToast(getString(R.string.load_faild) + msg);
                mPtrFrame.refreshComplete();
            }
        });
    }


    /**
     * 保存印象 到服务器
     */
    private void saveImpress() {
        me = BmobUser.getCurrentUser(this, User.class);
        if (me == null) {
            startActivity(new Intent(this, LoginActivity.class));
            showToast("请登录");
            return;
        }
        if (TextUtils.isEmpty(commentEt.getText().toString())) {
            showToast(getString(R.string.input_your_impress));
            return;
        }

        Impression impression = new Impression();
        impression.setContent(commentEt.getText().toString());
        impression.setImpress(me);
        impression.setBeiImpress(beiUser);
        impression.setRead(false);
        impression.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("印象成功");
                pushImpressMsg();//推送
                commentEt.setText(R.string.empty_text);
                commentEt.setHint(R.string.input_your_impress);
                //隐藏输入法
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(ImpressActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                queryImpress(0, STATE_REFRESH);//刷新数据
//                mListView.setSelection(0);
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("印象失败 " + s);

            }
        });

    }


    /**
     * 推送 印象消息
     */
    private void pushImpressMsg() {
        BmobPushManager bmobPush = new BmobPushManager(this);
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("uid", beiUser.getObjectId());
        bmobPush.setQuery(query);
        JSONObject json = new JSONObject();
        JSONObject array = new JSONObject();
        try {
            array.put("alert", "[" + me.getNickName() + "]给你添加了印象");
            array.put("style", "101");
            json.put("aps", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bmobPush.pushMessage(json);
    }


    /**
     * 初始化 下拉刷新
     */
    private void initPullToRefresh() {
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(this);

        mAdapter = new ImpressAdapter(this, impressionList, R.layout.list_item_impress);
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
                        queryImpress(0, STATE_REFRESH);

                    }
                });
            }
        });
    }


    /**
     * 初始化 加载更多
     */
    private void initLoadMore() {
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_list_view_container);
        assert loadMoreListViewContainer != null;
//        loadMoreListViewContainer.useDefaultHeader();


        ISLoadMoreFooterView footerView = new ISLoadMoreFooterView(this);
        footerView.setVisibility(View.GONE);
        loadMoreListViewContainer.setLoadMoreView(footerView);
        loadMoreListViewContainer.setLoadMoreUIHandler(footerView);
        mListView.setAdapter(mAdapter);
        loadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //查询下一页数据
                queryImpress(curPage, STATE_MORE);
//                if (lastTime != null) {
//                    queryArticleComment(curPage, STATE_MORE);
//                }

            }
        });
    }


    /**
     * 返回
     */
    public void back(View view) {
        finish();
    }

    /**
     * 弹出 吐司
     */

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
