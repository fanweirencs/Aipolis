package com.aibang.aipolis.activity;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aibang.aipolis.R;
import com.aibang.aipolis.bean.FeedBack;
import com.aibang.aipolis.bean.User;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 意见建议
 * Created by zcf on 2016/5/3.
 */
public class AboutOpinionsActivity extends AppCompatActivity {

    private EditText opinionsEt;//意见
    private EditText phoneEt;//电话
    private Button submitBtn;
    ImageView imageView;//问题截图
    private final int REQUEST_CODE = 1000;
    private String imgPath = null;

    User user;
    private GalleryFinal.OnHanlderResultCallback callback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (reqeustCode == REQUEST_CODE && resultList.size() > 0) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(resultList.get(0).getPhotoPath()));
                imgPath = resultList.get(0).getPhotoPath();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(AboutOpinionsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_opinions);
        user = BmobUser.getCurrentUser(this, User.class);
        initView();

    }

    private void initView() {
        ((TextView) findViewById(R.id.id_top_title))
                .setText(getString(R.string.opinions_suggestions));

        opinionsEt = (EditText) findViewById(R.id.id_opinions);
        submitBtn =(Button)findViewById(R.id.id_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToService();
            }
        });
        phoneEt = (EditText) findViewById(R.id.id_phone);
        imageView = (ImageView) findViewById(R.id.id_question_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openGallerySingle(REQUEST_CODE, callback);
            }
        });
    }

    /**
     * 上传包括图片
     */
    private void upLoadWithImg() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在上传···");
        dialog.show();
        final BmobFile bmobFile = new BmobFile(new File(imgPath));
        bmobFile.uploadblock(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                    dialog.dismiss();
                //bmobFile.getFileUrl(context)--返回的上传文件的完整地址
                String opinions = opinionsEt.getText().toString();
                String phone = phoneEt.getText().toString();
                FeedBack feedBack = new FeedBack();
                feedBack.setContent(opinions);//内容
                //图片地址
                feedBack.setThumnailTaskURL(bmobFile.getFileUrl(AboutOpinionsActivity.this));
                if (!TextUtils.isEmpty(phone))
                    feedBack.setContactNum(phone);
                if (user != null)
                    feedBack.setUser(user);
                feedBack.save(AboutOpinionsActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AboutOpinionsActivity.this, "您的意见已收到，祝您生活愉快", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(AboutOpinionsActivity.this, "上传失败" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }

            @Override
            public void onFailure(int code, String msg) {
                dialog.dismiss();
                Toast.makeText(AboutOpinionsActivity.this, "上传失败：" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 把 意见上传到服务器
     */
    private void saveToService() {
        String opinions = opinionsEt.getText().toString();
        String phone = phoneEt.getText().toString();
        if (TextUtils.isEmpty(opinions)) {
            Toast.makeText(this, "至少要填写意见", Toast.LENGTH_SHORT).show();
            return;
        }


        if (imgPath != null) {//上传图片
            upLoadWithImg();
        } else {//不上传图片
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在上传···");
            dialog.show();
            FeedBack feedBack = new FeedBack();
            feedBack.setContent(opinions);
            if (!TextUtils.isEmpty(phone))
                feedBack.setContactNum(phone);
            if (user != null)
                feedBack.setUser(user);
            feedBack.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    Toast.makeText(AboutOpinionsActivity.this, "您的意见已收到，祝您生活愉快", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    dialog.dismiss();
                    Toast.makeText(AboutOpinionsActivity.this, "上传失败" + s, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * 返回
     */
    public void back(View view) {
        finish();
    }


}
