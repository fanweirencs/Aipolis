package com.aibang.aipolis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.Guanzhu;
import com.aibang.aipolis.bean.Impression;
import com.aibang.aipolis.bean.Life;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.bean.UserInterest;
import com.aibang.aipolis.utils.CustomToast;
import com.aibang.aipolis.view.ConfirmDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 他人主页
 * Created by zcf on 2016/5/16.
 */
public class PersonalOtherHomepageActivity extends AppCompatActivity {
    private CircleImageView userHeadImg;
    //                           关注 他
    private TextView signatureTv, attentionTa, ChatTa,nickNameTv, cityTv;
    private TextView interestTv, homePageTv, impressTv;//如果没选择 兴趣  或者生活没有照片 就显示文字
    private boolean ifAttention = false;//是否关注过他人
    private String objectId;//关注字段的id
    private Guanzhu guanzhu;
    TextView topTitle;
    private ImageView sexIv;
    private User otherUser;
    private User me;
    private boolean isQueryFaild = false;//判读查询是否失败 如果失败就要重新查询 （没网的时候 查询就失败了）

    private TagFlowLayout mFlowLayout;
    private ImageView bgIv;
    private final int REQUEST_CODE_GALLERY = 1001;
    private ImageView iv1, iv2, iv3, arrowIv;//主页图片
    private RelativeLayout homePageLy;//
    private LinearLayout impressLy;//

    private TagAdapter<String> mAdapter;
    //    String[] mVals = {"吃货", "运动", "兴趣", "吃货", "运动", "兴趣", "吃货", "运动", "兴趣"};
    List<String> mVals = new ArrayList<>();


    private GalleryFinal.OnHanlderResultCallback callback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (reqeustCode == REQUEST_CODE_GALLERY && resultList.size() > 0) {
                Toast.makeText(PersonalOtherHomepageActivity.this, ("正在更新背景···"), Toast.LENGTH_SHORT).show();
                Bitmap b = BitmapFactory.decodeFile(resultList.get(0).getPhotoPath());
                bgIv.setImageBitmap(b);
                updateBg(resultList.get(0).getPhotoPath());//更新背景
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };


    /**
     * 保存背景图片
     */

    private void updateBg(String picPath) {
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(PersonalOtherHomepageActivity.this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                User newUser = new User();
                //bmobFile.getFileUrl(context)--返回的上传文件的完整地址
                newUser.setBackgroundImg(bmobFile.getFileUrl(PersonalOtherHomepageActivity.this));
                User user = BmobUser.getCurrentUser(PersonalOtherHomepageActivity.this, User.class);
                newUser.update(PersonalOtherHomepageActivity.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        //通知主界面侧边栏 更新背景
                        me = BmobUser.getCurrentUser(PersonalOtherHomepageActivity.this, User.class);
                        me.setBackgroundImg(bmobFile.getFileUrl(PersonalOtherHomepageActivity.this));
                        Toast.makeText(PersonalOtherHomepageActivity.this, "更新成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(PersonalOtherHomepageActivity.this, "更新失败" + s, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(PersonalOtherHomepageActivity.this,
                        "更新失败" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_personnal_homepager);
        otherUser = (User) getIntent().getSerializableExtra("user");
        initView();
        initDatas();
        me = BmobUser.getCurrentUser(PersonalOtherHomepageActivity.this, User.class);
        queryIsAttention();
        queryUserInterest();
        queryImpress();
        queryLifeImg();

        attentionTa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (me == null) {
                    startActivity(new Intent(PersonalOtherHomepageActivity.this
                            , LoginActivity.class));
                } else {
                    //取关 或者关注
                    if (isQueryFaild) {//如果查询失败了，就重新查询
                        queryIsAttention();
                    }
                    changeAttention();
                }
            }
        });

        ChatTa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (me == null) {
                    startActivity(new Intent(PersonalOtherHomepageActivity.this
                            , LoginActivity.class));
                } else {
                    User user=otherUser;
                    BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getNickName(), user.getAutographUrl());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    startActivity(ChatActivity.class, bundle);
                }
            }
        });
    }

    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(PersonalOtherHomepageActivity.this, target);
        if (bundle != null)
            intent.putExtra(PersonalOtherHomepageActivity.this.getPackageName(), bundle);
        PersonalOtherHomepageActivity.this.startActivity(intent);
    }


    private void initView() {
        topTitle = (TextView) findViewById(R.id.id_top_title);
        attentionTa = (TextView) findViewById(R.id.id_attention_ta);
        ChatTa = (TextView) findViewById(R.id.id_chat_ta);
        userHeadImg = (CircleImageView) findViewById(R.id.id_user_head_img);
        signatureTv = (TextView) findViewById(R.id.id_signature);
        nickNameTv = (TextView) findViewById(R.id.id_nick_name);
        cityTv = (TextView) findViewById(R.id.id_city);


        homePageTv = (TextView) findViewById(R.id.id_home_page_text);
        impressTv = (TextView) findViewById(R.id.id_impress);
        interestTv = (TextView) findViewById(R.id.id_interest_text);
        sexIv = (ImageView) findViewById(R.id.id_user_sex);
        iv1 = (ImageView) findViewById(R.id.id_iv1);
        iv2 = (ImageView) findViewById(R.id.id_iv2);
        iv3 = (ImageView) findViewById(R.id.id_iv3);
        bgIv = (ImageView) findViewById(R.id.id_user_head_bg);
        arrowIv = (ImageView) findViewById(R.id.id_arrow_to);

        homePageLy = (RelativeLayout) findViewById(R.id.id_homePageLy);
        impressLy = (LinearLayout) findViewById(R.id.id_impressLy);
        impressLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalOtherHomepageActivity.this,
                        ImpressActivity.class);
                intent.putExtra("user", otherUser);
                startActivity(intent);
                finish();
            }
        });

        homePageLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalOtherHomepageActivity.this,
                        PersonalHomepageActivity.class);
                intent.putExtra("user", otherUser);
                startActivity(intent);
                finish();
            }
        });

        bgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (me != null) {
                    if (me.getObjectId().equals(otherUser.getObjectId())) {
                        ConfirmDialog dialog = new ConfirmDialog(PersonalOtherHomepageActivity.this);
                        dialog.setTopTitle("Tips");
                        dialog.setContentText("是否更换背景图片");
                        dialog.setOkText("确定");
                        dialog.setCancelText("取消");
                        dialog.show();

                        dialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                            @Override
                            public void onOKClick() {
                                //打开相册
                                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, callback);
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        });

                    }

                }


            }
        });


        mFlowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);

        mAdapter = new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(PersonalOtherHomepageActivity.this)
                        .inflate(R.layout.item_remark,
                                mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        mFlowLayout.setAdapter(mAdapter);

    }


    /**
     * 初始化数据
     */
    private void initDatas() {
        if (otherUser != null) {
            topTitle.setText(otherUser.getNickName());
            nickNameTv.setText(otherUser.getNickName());
            cityTv.setText(otherUser.getLocation());
            assert signatureTv != null;
            if (!TextUtils.isEmpty(otherUser.getQianming()))
                signatureTv.setText(otherUser.getQianming());
            String uerHead = otherUser.getAutographUrl();
            String bg = otherUser.getBackgroundImg();
            //设置头像
            assert userHeadImg != null;
            DisplayImageOptions options = new DisplayImageOptions.Builder()//
                    .cacheInMemory(true)//
                    .cacheOnDisk(true)//
                    .bitmapConfig(Bitmap.Config.RGB_565)//
                    .build();
            if (uerHead != null) {

                ImageLoader.getInstance().displayImage(uerHead, userHeadImg, options);

            } else if (otherUser.getGender().equals("female")) {
                userHeadImg.setImageResource(R.mipmap.default_head_female);
            } else {
                userHeadImg.setImageResource(R.mipmap.default_head_male);
            }

            if (bg != null) {
                ImageLoader.getInstance().displayImage(bg, bgIv, options);
            }

            //设置性别
            assert sexIv != null;
            if (otherUser.getGender().equals("female")) {
                sexIv.setImageResource(R.drawable.ic_sex_female);
            } else if (otherUser.getGender().equals("male")) {
                sexIv.setImageResource(R.drawable.ic_sex_male);
            }
        }
    }

    /**
     * 查询兴趣
     */
    private void queryUserInterest() {
        BmobQuery<UserInterest> query = new BmobQuery<>();
        query.addWhereEqualTo("user", otherUser);
        query.include("interest");
        query.findObjects(PersonalOtherHomepageActivity.this, new FindListener<UserInterest>() {
            @Override
            public void onSuccess(List<UserInterest> list) {
                if (list.size() > 0) {
                    for (UserInterest ui : list) {
                        mVals.add(ui.getInterest().getName());
                    }
                    mAdapter.notifyDataChanged();
                } else {
                    interestTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 查询印象
     */

    private void queryImpress() {
        BmobQuery<Impression> query = new BmobQuery<>();
        query.addWhereEqualTo("beiImpress", otherUser);
        query.include("impress");
        // 按时间降序查询
        query.order("-createdAt");
        // 设置每页数据个数
        query.setLimit(1);
        query.findObjects(this, new FindListener<Impression>() {
            @Override
            public void onSuccess(List<Impression> list) {
                if (list.size() > 0) {
                    impressTv.setText(list.get(0).getContent());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    /**
     * 查询生活  显示图片
     */

    private void queryLifeImg() {
        BmobQuery<Life> query = new BmobQuery<>();

        //查询username字段的值是以“http://bmob“字开头的数据
        query.addWhereStartsWith("ThumnailTaskURL", "http://bmob");
        query.addWhereEqualTo("user", otherUser);
        query.setLimit(3);
        query.order("-createdAt");
        query.findObjects(PersonalOtherHomepageActivity.this, new FindListener<Life>() {
            @Override
            public void onSuccess(List<Life> list) {
                if (list.size() > 0) {
                    String img = "";
                    for (Life l : list) {
                        img += l.getThumnailTaskURL() + "&";
                    }
                    //图片地址
                    String[] listPath = img.split("&");
                    DisplayImageOptions options = new DisplayImageOptions.Builder()//
                            .cacheInMemory(true)//
                            .cacheOnDisk(true)//
                            .bitmapConfig(Bitmap.Config.RGB_565)//
                            .build();
                    if (listPath.length >= 3) {
                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);
                        iv3.setVisibility(View.VISIBLE);
                        arrowIv.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(listPath[0], iv1, options);
                        ImageLoader.getInstance().displayImage(listPath[1], iv2, options);
                        ImageLoader.getInstance().displayImage(listPath[2], iv3, options);
                    } else if (listPath.length == 2) {
                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(listPath[0], iv1, options);
                        ImageLoader.getInstance().displayImage(listPath[1], iv2, options);
                        arrowIv.setVisibility(View.VISIBLE);
                    } else {
                        iv1.setVisibility(View.VISIBLE);
                        arrowIv.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(listPath[0], iv1, options);

                    }

                } else {
                    iv1.setVisibility(View.GONE);
                    iv2.setVisibility(View.GONE);
                    iv3.setVisibility(View.GONE);
                    homePageTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {
                iv1.setVisibility(View.GONE);
                iv2.setVisibility(View.GONE);
                iv3.setVisibility(View.GONE);
                homePageTv.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 查询是否已经关注此人
     */
    private void queryIsAttention() {
        //登录了才去查询是否关注过
        if (me != null) {
            BmobQuery<Guanzhu> query = new BmobQuery<>();
            query.addWhereEqualTo("guanzhu", me);
            query.addWhereEqualTo("beiguanzhu", otherUser);
            query.findObjects(this, new FindListener<Guanzhu>() {
                @Override
                public void onSuccess(List<Guanzhu> object) {
                    if (object.size() != 0) {
                        ifAttention = true;
                        objectId = object.get(0).getObjectId();
                        attentionTa.setText("已关注");

                    } else {
                        ifAttention = false;
                        attentionTa.setText("关注Ta");
                    }

                }

                @Override
                public void onError(int code, String msg) {
                    // TODO Auto-generated method stub
                    Toast.makeText(PersonalOtherHomepageActivity.this, msg, Toast.LENGTH_SHORT).show();
                    isQueryFaild = true;
                }
            });
        }

    }

    public void back(View view) {
        finish();
    }


    /**
     * 关注 或者取关
     */

    private void changeAttention() {

        //如果未关注，就关注他
        if (!ifAttention) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("更新中···");
            dialog.show();
            guanzhu = new Guanzhu();
            guanzhu.setGuanzhu(me);
            guanzhu.setBeiguanzhu(otherUser);
            guanzhu.setLeixing(1);
            guanzhu.setRead(false);
            guanzhu.save(getBaseContext(), new SaveListener() {

                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    // TODO Auto-generated method stub
                    objectId = guanzhu.getObjectId();
                    ifAttention = true;
                    CustomToast.showToast(PersonalOtherHomepageActivity.this, "关注成功", Toast.LENGTH_SHORT);
                    attentionTa.setText("已关注");
                    //我关注的人数加1
//                    changeFollowingNumber(me,STYLE_ADD);
                    //被关注的跟随者人数加1
//                    changeFollowersNumber(otherUser,STYLE_ADD);
                    pushAttentionMsg();//推送消息
                }

                @Override
                public void onFailure(int code, String arg0) {
                    dialog.dismiss();
                    // TODO Auto-generated method stub
                    // 添加失败
                }
            });

        } else {//取消关注

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("更新中···");
            progressDialog.show();
            Guanzhu guanzhu = new Guanzhu();
            guanzhu.setObjectId(objectId);
            guanzhu.delete(PersonalOtherHomepageActivity.this, new DeleteListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    // TODO Auto-generated method stub
                    CustomToast.showToast(PersonalOtherHomepageActivity.this, "取消关注成功", Toast.LENGTH_SHORT);
                    ifAttention = false;
                    attentionTa.setText("关注ta");
                    //我关注的人数-1
//                    changeFollowingNumber(me,STYLE_SUB);
                    //被关注者的跟随者人数-1
//                    changeFollowersNumber(otherUser,STYLE_SUB);
                }

                @Override
                public void onFailure(int code, String msg) {
                    progressDialog.dismiss();
                    // TODO Auto-generated method stub
                }
            });
        }
    }


    /**
     * 推送关注消息
     */
    private void pushAttentionMsg() {
        BmobPushManager bmobPush = new BmobPushManager(this);
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("uid", otherUser.getObjectId());
        bmobPush.setQuery(query);
        JSONObject json = new JSONObject();
        JSONObject array = new JSONObject();
        try {
            array.put("alert", me.getNickName() + getString(R.string.attention_you));
            array.put("style", "1");
            json.put("aps", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bmobPush.pushMessage(json);
    }


    /**
     * 关注我的人数加1或者减一
     */
//    private void changeFollowersNumber(User user, Boolean style) {
//
//        //把我关注的人数 加 1 或者 减去 1
//        if (user.getFollowersNumber() != null) {
//            User newUser = new User();
//            if (style) {
//                int num = user.getFollowersNumber();
//                num += 1;
//                newUser.setFollowersNumber(num);
//            } else {
//                int num = user.getFollowersNumber();
//                num -= 1;
//                newUser.setFollowersNumber(num);
//            }
//            newUser.update(this, user.getObjectId(), new UpdateListener() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//
//                }
//            });
//        } else {
//            queryFollower(user, style);
//        }
//
//    }

    /**
     * 我关注的人数加1或者减一
     */
//    private void changeFollowingNumber(User user, Boolean style) {
//
//        //把我关注的人数 加 1 或者 减去 1
//        if (user.getFollowingNumber() != null) {
//            User newUser = new User();
//            if (style) {
//                int num = user.getFollowingNumber();
//                num += 1;
//                newUser.setFollowingNumber(num);
//            } else {
//                int num = user.getFollowingNumber();
//                num -= 1;
//                newUser.setFollowingNumber(num);
//            }
//            newUser.update(this, user.getObjectId(), new UpdateListener() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//
//                }
//            });
//        } else {
//            queryFollowing(user, style);
//        }
//
//    }


    /**
     * 查询关注我的人数
     * 这是设计的时候没把这个加进去 导致出现问题
     */
//    private void queryFollower(final User user, final boolean style) {
//        BmobQuery<Guanzhu> query = new BmobQuery<>();
//        query.addWhereEqualTo("beiguanzhu", user);
//        query.count(this, Guanzhu.class, new CountListener() {
//            @Override
//            public void onSuccess(int i) {
//
//                User newUser = new User();
//                newUser.setFollowersNumber(i);
//                if (style) {
//                    int num = i + 1;
//                    newUser.setFollowersNumber(num);
//                } else {
//                    int num = i - 1;
//                    newUser.setFollowersNumber(num);
//                }
//                newUser.update(PersonalOtherHomepageActivity.this, user.getObjectId(), new UpdateListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//
//            }
//        });
//    }

    /**
     * 查询我关注的人数
     * 这是设计的时候没把这个加进去 导致出现问题
     */
//    private void queryFollowing(final User user, final boolean style) {
//        BmobQuery<Guanzhu> query = new BmobQuery<>();
//        query.addWhereEqualTo("guanzhu", user);
//        query.count(this, Guanzhu.class, new CountListener() {
//            @Override
//            public void onSuccess(int i) {
//
//                User newUser = new User();
//                if (style) {
//                    int num = i + 1;
//
//                    newUser.setFollowingNumber(num);
//                } else {
//                    int num = i - 1;
//                    newUser.setFollowingNumber(num);
//                }
//                newUser.update(PersonalOtherHomepageActivity.this, user.getObjectId(), new UpdateListener() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//
//            }
//        });
//    }


}
