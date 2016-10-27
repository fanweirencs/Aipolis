package com.aibang.aipolis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.MyAttentionAdapter;
import com.aibang.aipolis.bean.Guanzhu;
import com.aibang.aipolis.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * 我关注的人
 * Created by zcf on 2016/5/2.
 */
public class MyAttentionActivity extends AppCompatActivity {
    private ListView mListView;
    private MyAttentionAdapter mAdapter;
    private List<Guanzhu> guanzhuList = new ArrayList<>();
    private ImageButton backBtn;//返回按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attenttion);
        mListView = (ListView) findViewById(R.id.id_list_view);
        backBtn = (ImageButton) findViewById(R.id.id_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter = new MyAttentionAdapter(MyAttentionActivity.this,guanzhuList,
                R.layout.list_item_my_attenttion);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyAttentionActivity.this,
                        PersonalOtherHomepageActivity.class);
                intent.putExtra("user",guanzhuList.get(position).getBeiguanzhu());
                startActivity(intent);
            }
        });

        queryMyAttention();
    }

    /**
     * 查询我关注的人
     */
    private void queryMyAttention() {
        final User user = BmobUser.getCurrentUser(this,User.class);
        BmobQuery<Guanzhu> query = new BmobQuery<>();
        query.addWhereEqualTo("guanzhu", user);
        query.include("beiguanzhu");
        query.findObjects(this, new FindListener<Guanzhu>() {
            @Override
            public void onSuccess(List<Guanzhu> object) {
                // TODO Auto-generated method stub

                if (object.size()>0){
                    for (Guanzhu g : object){
                        guanzhuList.add(g);
                    }
                    mListView.setAdapter(mAdapter);
                }


            }
            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
//                toast("查询用户失败："+msg);
            }
        });
    }


}
