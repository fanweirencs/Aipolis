package com.aibang.aipolis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aibang.aipolis.R;
import com.aibang.aipolis.adapter.ProvinceAndSchoolAdapter;
import com.aibang.aipolis.model.Province;
import com.aibang.aipolis.model.School;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ProvinceAndSchoolActivity extends AppCompatActivity {
    private ListView listView;
    private ProvinceAndSchoolAdapter mAdapter;
    private TextView titleTv;
    private int currentLevel;
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_SCHOOL = 2;

    List<Province> allProvince;
    List<School> schoolList;
    List<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private School selectedSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_and_school);
        initView();
        queryProvince();
    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.id_top_title);
        assert  titleTv !=null;
        titleTv.setText(R.string.select_province);
        listView = (ListView) findViewById(R.id.id_list_view);
        mAdapter = new ProvinceAndSchoolAdapter(ProvinceAndSchoolActivity.this,dataList,R.layout.list_item_province_and_school );

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = allProvince.get(position);
                    querySchool(selectedProvince.getId());
                } else {
                    selectedSchool = schoolList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("result", selectedSchool.getName());
                /*

                 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
                 */
                    setResult(1001, intent);
                    //    结束当前这个Activity对象的生命
                    finish();

                }
            }
        });
    }

    /**
     * 查询省份
     */
    private void queryProvince() {
        titleTv.setText(R.string.select_province);
        allProvince = DataSupport.findAll(Province.class);
        if (allProvince.size() > 0) {
            dataList.clear();
            for (Province province : allProvince) {
                dataList.add(province.getName());
            }
            mAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        }
    }

    /**
     * 根据id查询学校
     *
     * @param province_id 学校所属省份 id
     */
    private void querySchool(int province_id) {
        titleTv.setText(R.string.select_school);
        schoolList = DataSupport.where("province_id = ?", String.valueOf(province_id)).find(School.class);
        dataList.clear();
        for (School school : schoolList) {
            dataList.add(school.getName());
        }
        mAdapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = LEVEL_SCHOOL;
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_SCHOOL) {
            queryProvince();
        } else {
            finish();
        }
    }

    public void back(View view) {
        if (currentLevel == LEVEL_SCHOOL) {
            queryProvince();
        } else {
            finish();
        }
    }

}
