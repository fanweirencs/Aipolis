package com.aibang.aipolis.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.LifeCommentAdapter;
import com.aibang.aipolis.adapter.MyNineGridViewAdapter;
import com.aibang.aipolis.bean.Life;
import com.aibang.aipolis.bean.ShenghuoComment;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.LifeEvent;
import com.aibang.aipolis.fragment.LifeFragment;
import com.aibang.aipolis.fragment.message.CommentFragment;
import com.aibang.aipolis.utils.ComUtils;
import com.aibang.aipolis.utils.DensityUtil;
import com.aibang.aipolis.utils.TransitionTime;
import com.aibang.aipolis.view.ConfirmDialog;
import com.aibang.aipolis.view.ISLoadMoreFooterView;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
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
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 生活 内容 评论
 * Created by zcf on 2016/5/9.
 */
public class ShowLifeContentActivity extends AppCompatActivity implements View.OnClickListener {
    private View headView;//listview 头部
    private NineGridView mNineView;//加载图片
    private CircleImageView headIv;//头像
    private TextView nameTv, timeTv, schoolTv, loveNumTv, commentNumTv;//名字 时间 学校 点赞数 评论数
    private TextView contentTv;
    private ImageView rubbushIv;//垃圾桶

    private EditText commentEt;//评论内容
    private Button sendBtn;//发送评论按钮

    private Life life;//生活

    private ListView mListView;
    private PtrFrameLayout mPtrFrame;//下拉刷新控件
    // 加载更多
    private LoadMoreListViewContainer loadMoreListViewContainer;
    private ImageButton msgIbtn;//消息按钮

    private LifeCommentAdapter mAdapter;//适配器
    private List<ShenghuoComment> commentData = new ArrayList<>();//数据集

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 20;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";//上一次加载 最后一条数据的时间

    private User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_life_content);
        initView();
        //获取 生活 的数据
        life = (Life)
                getIntent().getSerializableExtra(LifeFragment.LIFE_DATA);
        if (life != null) {
            initData();
        } else {
            life = (Life)
                    getIntent().getSerializableExtra(CommentFragment.COMMENT_LIFE_DATA);

            BmobQuery<Life> query = new BmobQuery<>();
            query.addWhereEqualTo("objectId", life.getObjectId());
            query.include("user");
            query.setLimit(1);
            query.findObjects(this, new FindListener<Life>() {
                @Override
                public void onSuccess(List<Life> list) {
                    if (list.size() > 0) {
                        life = list.get(0);
                        initData();
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }


        //设置发送评论按钮监听事件
        sendBtn.setOnClickListener(this);
    }


    private void initView() {
        //设置顶部标题
        ((TextView) findViewById(R.id.id_top_title)).setText(getString(R.string.life));
        commentEt = (EditText) findViewById(R.id.id_comment_et);
        sendBtn = (Button) findViewById(R.id.id_send);
        mListView = (ListView) findViewById(R.id.id_list_view);
        initHeadView();
        mListView.addHeaderView(headView);
        initPullToRefresh();
        initLoadMore();

    }

    /**
     * 初始化 list view 的头部
     */
    private void initHeadView() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headView = inflater.inflate(R.layout.head_view_life, mListView, false);
        mNineView = (NineGridView) headView.findViewById(R.id.id_nineGrid);
        nameTv = (TextView) headView.findViewById(R.id.id_nick_name);
        timeTv = (TextView) headView.findViewById(R.id.id_pub_time);
        schoolTv = (TextView) headView.findViewById(R.id.id_school);
        headIv = (CircleImageView) headView.findViewById(R.id.id_user_head_img);
        rubbushIv = (ImageView) headView.findViewById(R.id.id_delete);

        loveNumTv = (TextView) headView.findViewById(R.id.id_loveNum);
        loveNumTv.setOnClickListener(this);
        commentNumTv = (TextView) headView.findViewById(R.id.id_comment_num);
        contentTv = (TextView) headView.findViewById(R.id.id_content);
    }


    /**
     * 设置数据
     */

    private void initData() {
        if (life != null) {
            User u = BmobUser.getCurrentUser(ShowLifeContentActivity.this, User.class);
            if (u != null) {
                if (life.getUser().getObjectId().equals(u.getObjectId())) {
                    deleteLife(life);
                }
            }


            assert life.getUser().getAutographUrl() != null;
            contentTv.setText(life.getContent());
            nameTv.setText(life.getUser().getNickName());
            schoolTv.setText(life.getUser().getSchool());

            //设置点赞数目 和评论数目  注意这里是int 要转为String
            loveNumTv.setText(life.getZanNumber() + "");
            commentNumTv.setText(life.getCommentNumber() + "");
            //这两句计算时间差
            final TransitionTime time = new TransitionTime(System.currentTimeMillis(), life.getCreatedAt());
            String s = time.twoDateDistance(String.valueOf(time.getTimeMills(life.getCreatedAt())));
            timeTv.setText(s);
            String url = life.getUser().getAutographUrl();
            //设置头像
            if (url != null) {//如果有头像就从网络加载
                ImageLoader.getInstance().displayImage(url, headIv);
            } else if (life.getUser().getGender().equals("female")) {
                //如果没有 判断是女生就设置 女生头像，
                headIv.setImageResource(R.mipmap.default_head_female);
            } else {
                headIv.setImageResource(R.mipmap.default_head_male);
            }
            //加载图片
            setImages();
        }

    }

    /**
     * 删除生活
     */
    private void deleteLife(final Life life) {
        rubbushIv.setVisibility(View.VISIBLE);
        rubbushIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog dialog = new ConfirmDialog(ShowLifeContentActivity.this);
                dialog.setContentText("确定要删除该条信息以及所有评论吗");
                dialog.show();
                dialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOKClick() {
                        Life delLife = new Life();
                        delLife.setObjectId(life.getObjectId());
                        delLife.delete(ShowLifeContentActivity.this, new DeleteListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                Toast.makeText(ShowLifeContentActivity.this,
                                        "删除成功", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new LifeEvent());
                                finish();

                            }

                            @Override
                            public void onFailure(int code, String msg) {
                            }
                        });
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
            }
        });
    }



    /**
     * 加载 图片
     */
    private void setImages() {

        ArrayList<ImageInfo> urls = new ArrayList<>();//图片地址
        ComUtils.splitImageUrls(urls, life.getThumnailTaskURL());
        //设置图片
        mNineView.setSingleImageRatio(1);
        mNineView.setSingleImageSize(DensityUtil.getDeviceInfo(this)[0]);
        mNineView.setAdapter(new MyNineGridViewAdapter(this, urls));
//        mNineView.setAdapter(new MyClickNineGridViewAdapter(this, urls));

    }


    /**
     * 查询评论
     */
    public void queryLifeComment(int page, final int actionType) {
        BmobQuery<ShenghuoComment> query = new BmobQuery<>();
        query.addWhereEqualTo("shenghuo", life);
        query.include("commentUser,beiCommentUser");
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


        query.findObjects(this, new FindListener<ShenghuoComment>() {
            @Override
            public void onSuccess(List<ShenghuoComment> object) {

                if (object.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        commentData.clear();
                        // 获取最后时间
                        lastTime = object.get(object.size() - 1).getCreatedAt();
                    }

                    // 将本次查询的数据添加到bankCards中
                    for (ShenghuoComment ac : object) {
                        commentData.add(ac);
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
     * 初始化 下拉刷新
     */
    private void initPullToRefresh() {
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(this);

        mAdapter = new LifeCommentAdapter(this, commentData, R.layout.list_item_comment, commentEt);
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
                        queryLifeComment(0, STATE_REFRESH);

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
        mListView.setAdapter(mAdapter);

        ISLoadMoreFooterView footerView = new ISLoadMoreFooterView(this);
        footerView.setVisibility(View.GONE);
        loadMoreListViewContainer.setLoadMoreView(footerView);
        loadMoreListViewContainer.setLoadMoreUIHandler(footerView);
        loadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                //查询下一页数据
                queryLifeComment(curPage, STATE_MORE);
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
     *
     * @param msg
     */

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送按钮
            case R.id.id_send:
                saveComment();
                break;
            case R.id.id_loveNum:
                plusLike();
                break;
        }
    }


    /**
     * 更新评论数 +1
     */
    private void plusLike() {
        int num = life.getZanNumber() + 1;
        loveNumTv.setText(num + "");
        loveNumTv.setCompoundDrawablesWithIntrinsicBounds
                (R.drawable.ic_love_selected, 0, 0, 0);
        Life life = new Life();
        life.setZanNumber(num);
        life.update(this, life.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * 保存评论到服务器
     */
    private void saveComment() {
        user = BmobUser.getCurrentUser(this, User.class);
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if (TextUtils.isEmpty(commentEt.getText().toString())) {
            showToast(getString(R.string.please_input_comment));
            return;
        }

        ShenghuoComment sc = new ShenghuoComment();
        sc.setComment(commentEt.getText().toString());
        sc.setCommentUser(user);
        sc.setShenghuo(life);
        sc.setBeiCommentUser(mAdapter.getBeiCommentUser());
        sc.setShenghuoID(life.getObjectId());
        sc.setRead(false);
        sc.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("评论成功");
                pushAndroidMessage();//推送
                commentEt.setText(R.string.empty_text);
                commentEt.setHint(R.string.please_input_comment);

                //隐藏输入法
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(ShowLifeContentActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mAdapter.setBeiCommentUser(null);//被评论人设置为空
                queryLifeComment(0, STATE_REFRESH);//刷新数据
                mListView.setSelection(0);
                life.increment("commentNumber", 1);//评论数加1
                life.update(ShowLifeContentActivity.this);
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("评论失败 " + s);

            }
        });

    }

    /**
     * 推送消息
     */
    private void pushAndroidMessage() {
        BmobPushManager bmobPush = new BmobPushManager(this);
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        if (mAdapter.getBeiCommentUser() == null)
            query.addWhereEqualTo("uid", life.getUser().getObjectId());
        else
            query.addWhereEqualTo("uid", mAdapter.getBeiCommentUser().getObjectId());
        bmobPush.setQuery(query);

        JSONObject json = new JSONObject();
        JSONObject array = new JSONObject();
        try {
            array.put("alert", user.getNickName() + " 评论了你的生活状态:" + commentEt.getText().toString());
            array.put("style","1");
            json.put("aps", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bmobPush.pushMessage(json);


    }


}
