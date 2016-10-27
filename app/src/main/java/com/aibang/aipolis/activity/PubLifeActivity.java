package com.aibang.aipolis.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.PubLifeAdapter;
import com.aibang.aipolis.bean.Life;
import com.aibang.aipolis.bean.User;
import com.aibang.aipolis.event.LifeEvent;
import com.aibang.aipolis.utils.BitmapUtil;
import com.aibang.aipolis.utils.CustomToast;
import com.aibang.aipolis.utils.SDCardUtils;
import com.aibang.aipolis.view.MyProgressDialog;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 发布  生活页面
 * Created by zcf on 2016/5/10.
 */
public class PubLifeActivity extends AppCompatActivity {
    //              想说的话
    private EditText inputEt;
    private final int REQUEST_CODE_GALLERY = 1001;
    private List<PhotoInfo> mPhotoList = new ArrayList<>();
    private List<String> uploadImgList = new ArrayList<>();//保存压缩图片地址，用于上传
    private List<String> deleteImgList = new ArrayList<>();// 存储需要删除的图片路径，当上传完成或者返回时删掉图片;
    private String[] filePath;//保存压缩图地址，用于上传 bmob 上传需要String

    private GridView gridview;
    private PubLifeAdapter myAdapter;
    private String savePath = SDCardUtils.getSDPath() + File.separator + "Aipolis";
    private User user;
    private MyProgressDialog dialog;


    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, final List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoList.addAll(resultList);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (final PhotoInfo p : resultList) {
                            Log.d("url007", p.getPhotoPath());
                            File file = new File(p.getPhotoPath());
                            int size = (int) (file.length() / 1024);
                            Log.d("size007", "原图大小:" + size + "k");
                            String imgName = System.currentTimeMillis() + ".jpg";
                            String newSavePath = savePath + File.separator + imgName;
                            if (size >= 1024) {
//                                //压缩 到 30%
                                if (BitmapUtil.writeCompressImage2File(p.getPhotoPath(), newSavePath, 30)) {
                                    uploadImgList.add(savePath + File.separator + imgName);
                                    deleteImgList.add(savePath + File.separator + imgName);
                                }
                            } else if (size >= 600 && size < 1024) {

                                if (BitmapUtil.writeCompressImage2File(p.getPhotoPath(), newSavePath, 30)) {
                                    uploadImgList.add(savePath + File.separator + imgName);
                                    deleteImgList.add(savePath + File.separator + imgName);
                                }
                            } else if (size > 300 && size < 600) {

                                if (BitmapUtil.writeCompressImage2File(p.getPhotoPath(), newSavePath, 40)) {
                                    uploadImgList.add(savePath + File.separator + imgName);
                                }
                            } else if (size <= 300) {
                                uploadImgList.add(p.getPhotoPath());
                            }

                        }
                    }
                }).start();


                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(PubLifeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_life);
        user = BmobUser.getCurrentUser(this, User.class);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ((TextView) findViewById(R.id.id_top_title)).setText("记录生活");
        inputEt = (EditText) findViewById(R.id.id_life_text);
        gridview = (GridView) findViewById(R.id.lv_photo);
        myAdapter = new PubLifeAdapter(this, mPhotoList);
        gridview.setAdapter(myAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoList.clear();
                uploadImgList.clear();
                myAdapter.notifyDataSetChanged();
                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, 9, mOnHanlderResultCallback);

            }
        });
    }


    /**
     * 生成缩略图
     *
     * @param filePathName
     */
    public void generateThumbnail(String filePathName, int zoom) {
        Bitmap bitmap = BitmapUtil.decodeSampledBitmap(filePathName, zoom);
        String imgName = System.currentTimeMillis() + ".jpg";
        if (BitmapUtil.saveBitmapFile(bitmap, savePath, imgName)) {
            Log.d("save:", "图片保存成功");
            uploadImgList.add(savePath + File.separator + imgName);
        } else {
            Log.d("save:", "图片保存sb");
        }
    }

    /**
     * 返回
     */
    public void back(View view) {
        finish();
    }

    /**
     * 发布 生活
     */
    public void pub(View view) {


        if (user == null) {
            startActivity(new Intent(PubLifeActivity.this, LoginActivity.class));
            return;
        }

        String str = inputEt.getText().toString();
        if (TextUtils.isEmpty(str) && uploadImgList.size() == 0) {
            CustomToast.showToast(PubLifeActivity.this, "请先添加文字或图片...", Toast.LENGTH_SHORT);
        } else if (uploadImgList.size() == 0 && !TextUtils.isEmpty(str)) {
            pubLife(null, false);
        } else {

            filePath = new String[uploadImgList.size()];
            //Bmob上传文件 需要 sting[] 数组
            for (int i = 0; i < uploadImgList.size(); i++) {
                filePath[i] = uploadImgList.get(i);
            }
            //批量上传照片
            uploadImg();
        }
    }


    /**
     * 批量上传照片
     */
    public void uploadImg() {
        dialog = new MyProgressDialog(this);
        dialog.setTopTitle("正在上传···");
        dialog.setCancelable(false);
        dialog.show();


        BmobFile.uploadBatch(getBaseContext(), filePath, new UploadBatchListener() {

            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {


                // TODO Auto-generated method stub
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的服务器地址
                if (filePath.length == urls.size()) {//如果数量相等，则代表文件全部上传完成
                    pubLife(urls, true);
                    dialog.dismiss();
                }

            }

            @Override
            public void onError(int statuscode, String errormsg) {
                dialog.dismiss();
                // TODO Auto-generated method stub
                CustomToast.showToast(PubLifeActivity.this, "上传失败" + errormsg, Toast.LENGTH_SHORT);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                if (dialog.isShowing())
                    dialog.setProgress(totalPercent);
//                mProgressDialog.setProgress(totalPercent);
//                CustomToast.showToast(PubLifeActivity.this, "上传进度" + totalPercent + "%", Toast.LENGTH_LONG);

            }
        });
    }

    /**
     * 发布生活到服务器
     *
     * @param list
     * @param hasphoto
     */
    public void pubLife(List<String> list, boolean hasphoto) {
        String content = inputEt.getText().toString();
        final Life life = new Life();
        life.setContent(content);
        life.setZanNumber(0);
        life.setCommentNumber(0);
        life.setUser(user);
        //设置浏览数为0
        life.setLookSum(0);
        if (hasphoto == false) {
            life.setThumnailTaskURL("No");
            life.setImgNumber(0);
        } else {
            life.setImgNumber(list.size());
            String urls = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                urls = urls + "&" + list.get(i);
            }
            life.setThumnailTaskURL(urls);
        }
        life.save(getBaseContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                CustomToast.showToast(getApplication(), "发布成功...", Toast.LENGTH_LONG);
                EventBus.getDefault().post(new LifeEvent());
                deleteImg();//删除压缩的照片
                finish();
            }

            @Override
            public void onFailure(int code, String arg0) {
                // TODO Auto-generated method stub
                // 添加失败
                Toast.makeText(PubLifeActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 删除压缩的照片
     */
    private void deleteImg(){
        for (String p : deleteImgList){
            File file = new File(p);
            if (file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public void onBackPressed() {
        deleteImg();//删除压缩的照片
        super.onBackPressed();
    }
}
