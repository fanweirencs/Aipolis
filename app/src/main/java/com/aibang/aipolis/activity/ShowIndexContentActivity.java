package com.aibang.aipolis.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.IndexCommentAdapter;
import com.aibang.aipolis.bean.ArticleComment;
import com.aibang.aipolis.bean.ShouyeArticle;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.fragment.IndexFragment;
import com.aibang.aipolis.utils.TransitionTime;
import com.aibang.aipolis.view.ISLoadMoreFooterView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

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
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 首页 文章 内容
 * Created by zcf on 2016/5/3.
 */
public class ShowIndexContentActivity extends AppCompatActivity implements View.OnClickListener {
    private View headView;//listview 头部
    private WebView mWebView;//加载网页
    private CircleImageView headIv;//头像
    private TextView nameTv, timeTv, schoolTv, loveNumTv, commentNumTv,lookSumTv;//名字 时间 学校 点赞数 评论数  浏览量
    private JCVideoPlayerStandard videoPs;//视频
    private EditText commentEt;//评论内容
    private Button sendBtn;//发送评论按钮

    private ShouyeArticle article;//文章

    private ListView mListView;
    private PtrFrameLayout mPtrFrame;//下拉刷新控件
    // 加载更多
    LoadMoreListViewContainer loadMoreListViewContainer;
    private ImageButton msgIbtn;//消息按钮
    private ImageView id_top_share;

    private IndexCommentAdapter mAdapter;//适配器
    private List<ArticleComment> commentData = new ArrayList<>();//数据集

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 20;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";//上一次加载 最后一条数据的时间

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_index_content);

        //获取首页文章的数据
        article = (ShouyeArticle)
                getIntent().getSerializableExtra(IndexFragment.ARTICLE_DATA);
        initView();
        queryConntent();
        initData();


        //设置发送评论按钮监听事件
        sendBtn.setOnClickListener(this);
        id_top_share.setOnClickListener(this);
    }


    /**
     * 查询具体内容
     */
    private void queryConntent() {
        BmobQuery<ShouyeArticle> query = new BmobQuery<>();
        // 按时间降序查询
        query.addWhereEqualTo("objectId", article.getObjectId());
        //查询指定列
        query.addQueryKeys("content");
        query.findObjects(this, new FindListener<ShouyeArticle>() {
            @Override
            public void onSuccess(List<ShouyeArticle> list) {
                if (list.size() > 0) {
                    String content = list.get(0).getContent();
                    Log.d("content007",content);
                    //设置网页内容
                    WebSettings webSettings = mWebView.getSettings(); // webView: 类WebView的实例
                    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  //就是这句
                    if (article.getIsVideo() != null) {
                        //如果是视频 就裁剪掉视频这一段内容
                        String contentCrop = content.substring(content.indexOf("</video>") + 8);
                        Log.d("contentCrop", contentCrop);
                        mWebView.loadDataWithBaseURL(null, contentCrop, "text/html", "utf-8", null);

                    } else {
                        mWebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                    }
                }

            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowIndexContentActivity.this,"文章加载失败"+ s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        //设置顶部标题
        ((TextView) findViewById(R.id.id_top_title)).setText("首页");
        commentEt = (EditText) findViewById(R.id.id_comment_et);
        id_top_share = (ImageView) findViewById(R.id.id_top_share);
        id_top_share.setVisibility(View.VISIBLE);
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
        headView = inflater.inflate(R.layout.head_view_index, mListView, false);
        mWebView = (WebView) headView.findViewById(R.id.id_wb_content);
        nameTv = (TextView) headView.findViewById(R.id.id_nick_name);
        timeTv = (TextView) headView.findViewById(R.id.id_pub_time);
        schoolTv = (TextView) headView.findViewById(R.id.id_school);
        headIv = (CircleImageView) headView.findViewById(R.id.id_user_head_img);
        videoPs = (JCVideoPlayerStandard) headView.findViewById(R.id.id_video);
        loveNumTv = (TextView) headView.findViewById(R.id.id_loveNum);
        commentNumTv = (TextView) headView.findViewById(R.id.id_comment_num);
        lookSumTv = (TextView) headView.findViewById(R.id.id_look_sum);
    }


    /**
     * 设置数据
     */

    private void initData() {
        if (article != null) {
            assert article.getZuozhe().getAutographUrl() != null;
            nameTv.setText(article.getZuozhe().getNickName());
            schoolTv.setText(article.getZuozhe().getSchool());

            //更新浏览量

            addLookSumNumber();



            //设置点赞数目 和评论数目  注意这里是int 要转为String
            loveNumTv.setText(article.getZanNum() + "");
            loveNumTv.setOnClickListener(this);
            commentNumTv.setText(article.getCommentNum() + "");
            //这两句计算时间差
            final TransitionTime time = new TransitionTime(System.currentTimeMillis(), article.getCreatedAt());
            String s = time.twoDateDistance(String.valueOf(time.getTimeMills(article.getCreatedAt())));
            timeTv.setText(s);
            String url = article.getZuozhe().getAutographUrl();
            //设置头像
            if (url != null) {//如果有头像就从网络加载
                ImageLoader.getInstance().displayImage(url, headIv);
            } else {//如果没有 判断是女生就设置 女生头像，因为默认头像是男生
                if (article.getZuozhe().getGender().equals("female"))
                    headIv.setImageResource(R.mipmap.default_head_female);
            }

            //判断是否包含视频
            if (article.getIsVideo() != null) {
                if (article.getIsVideo()) {
                    videoPs.setVisibility(View.VISIBLE);
                    //视频地址
                    videoPs.setUp(article.getVideoUrl(), article.getTitle());
                    //缩略图地址
                    ImageLoader.getInstance().displayImage(article.getPreloadImgUrl(), videoPs.ivThumb);
                    Log.d("videoUrl", article.getVideoUrl());
                }

            }

        }
    }

    public void shareWithUmeng(){
//        UMShareListener umShareListener = new UMShareListener() {
//            @Override
//            public void onResult(SHARE_MEDIA share_media) {
//                Toast.makeText(getBaseContext(),share_media.name() + " 分享成功...", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                Toast.makeText(getBaseContext(),share_media.name() + " 分享失败...", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancel(SHARE_MEDIA share_media) {
//                Toast.makeText(getBaseContext(),share_media.name() + " 取消分享...", Toast.LENGTH_SHORT).show();
//            }
//        };

        new ShareAction(ShowIndexContentActivity.this)
                .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .withTitle(article.getTitle())
                .withText("来自桃花源")
                .withMedia(new UMImage(getBaseContext(),R.mipmap.logo))
                .withTargetUrl("http://www.aipolis.com/homePage/secondPage.html?id="+article.getObjectId())
//                .setCallback(umShareListener)
                .open();
    }
    /**
     * 更新浏览量
     */

    private void addLookSumNumber( ){
        //如果为空 后台设置为1
        if (String.valueOf(article.getLookSum()).equals("null")){
            ShouyeArticle sa = new ShouyeArticle();
            sa.setLookSum(1);
            lookSumTv.setText("1");
            sa.update(this, article.getObjectId(), new UpdateListener() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
//                    Log.i("looksum", "更新成功：");
                }

                @Override
                public void onFailure(int code, String msg) {
                    // TODO Auto-generated method stub
//                    Log.i("looksum", "更新失败：" + msg);
                }
            });
        }else {
            int num = article.getLookSum()+1;
            lookSumTv.setText(num+"");
            //否则 自动加1
            ShouyeArticle sa = new ShouyeArticle();
            sa.increment("lookSum");
            sa.update(this, article.getObjectId(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
//                    Log.i("looksum", "更新成功：");
                }

                @Override
                public void onFailure(int code, String msg) {
                    // TODO Auto-generated method stub
//                    Log.i("looksum", "更新失败：" + msg);
                }
            });
        }

    }


    /**
     * 查询评论
     */
    public void queryArticleComment(int page, final int actionType) {
        BmobQuery<ArticleComment> query = new BmobQuery<>();
        query.addWhereEqualTo("articleID", article.getObjectId());
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
            if (date != null) {
                query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
                loadMoreListViewContainer.loadMoreFinish(true, false);
            } else
                return;


//            Log.d("time00",new BmobDate(date)+"");
            // 跳过之前页数并去掉重复数据
            query.setSkip((page - 1) * limit);
        } else {
            page = 0;
            query.setSkip(page);
        }


        query.findObjects(this, new FindListener<ArticleComment>() {
            @Override
            public void onSuccess(List<ArticleComment> object) {

                if (object.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        commentData.clear();
                        // 获取最后时间
                        lastTime = object.get(object.size() - 1).getCreatedAt();
                    }

                    // 将本次查询的数据添加到bankCards中
                    for (ArticleComment ac : object) {
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

        mAdapter = new IndexCommentAdapter(this, commentData, R.layout.list_item_comment, commentEt);
        // using string array from resource xml file
        header.initWithString("Aipolis");
//        header.initWithStringArray(R.array.storehouse);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
//        mPtrFrame.autoRefresh();
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
                        queryArticleComment(0, STATE_REFRESH);

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
                queryArticleComment(curPage, STATE_MORE);
//                if (lastTime != null) {
//                    queryArticleComment(curPage, STATE_MORE);
//                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        //释放视频资源
        JCVideoPlayer.releaseAllVideos();
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
            case R.id.id_top_share:
                shareWithUmeng();
                break;
        }
    }

    /**
     * 更新评论数 +1
     */
    private void plusLike() {
        int num = article.getZanNum() + 1;
        loveNumTv.setText(num + "");
        loveNumTv.setCompoundDrawablesWithIntrinsicBounds
                (R.drawable.ic_love_selected, 0, 0, 0);
        ShouyeArticle shouye = new ShouyeArticle();
        shouye.setZanNum(num);
        shouye.update(this, article.getObjectId(), new UpdateListener() {
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
        ArticleComment cm = new ArticleComment();
        cm.setComment(commentEt.getText().toString());
        cm.setCommentUser(user);
        cm.setArticle(article);
        cm.setBeiCommentUser(mAdapter.getBeiCommentUser());
        cm.setArticleID(article.getObjectId());
        cm.setRead(false);
        cm.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("评论成功");
//                pushAndroidMessage();//暂时不推送首页文章
                commentEt.setText(R.string.empty_text);
                commentEt.setHint(R.string.please_input_comment);
//                commentEt.clearFocus();
//                commentEt.setFocusable(false);
                //隐藏输入法
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(ShowIndexContentActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mAdapter.setBeiCommentUser(null);//被评论人设置为空
                queryArticleComment(0, STATE_REFRESH);//刷新数据
                mListView.setSelection(0);
                article.increment("commentNum", 1);//评论数加1
                article.update(ShowIndexContentActivity.this);
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
            query.addWhereEqualTo("uid", article.getZuozhe().getObjectId());
        else
            query.addWhereEqualTo("uid", mAdapter.getBeiCommentUser().getObjectId());
        bmobPush.setQuery(query);

        JSONObject json = new JSONObject();
        JSONObject array = new JSONObject();
        try {
            array.put("alert", user.getNickName() + " 回复了你的首页文章信息:" + commentEt.getText().toString());
            array.put("style","1");
            json.put("aps", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bmobPush.pushMessage(json);
    }
}
