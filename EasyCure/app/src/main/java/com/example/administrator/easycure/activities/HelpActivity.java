package com.example.administrator.easycure.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.HelpAdapter;
import com.example.administrator.easycure.utils.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/5 0005.
 */

public class HelpActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView activity_help_iv1;
    private ListView activity_help_lv;

    private List<Map<String,Object>> list = new ArrayList<>();
    private Map<String,Object> map;

    private HelpAdapter helpAdapter;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        init();

        initData();
    }

    public void init(){
        activity_help_iv1 = (ImageView)findViewById(R.id.activity_help_iv1);
        activity_help_lv = (ListView)findViewById(R.id.activity_help_lv);

        activity_help_iv1.setOnClickListener(this);
        activity_help_lv.setOnItemClickListener(this);
    }

    public void initData(){
        map = new HashMap<>();
        map.put("icon",R.mipmap.customer_service);
        map.put("title",getResources().getString(R.string.problem_help));
        map.put("msg",getResources().getString(R.string.customer_service));
        list.add(map);

        map = new HashMap<>();
        map.put("icon",R.mipmap.feedback);
        map.put("title",getResources().getString(R.string.feedback));
        map.put("msg",getResources().getString(R.string.abnormal_function_suggest));
        list.add(map);

        helpAdapter = new HelpAdapter(this,list);
        activity_help_lv.setAdapter(helpAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_help_iv1:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:     //联系客服界面
                intent = new Intent(this,CustomerServiceActivity.class);
                startActivity(intent);
                break;
            case 1:     //意见反馈界面
                intent = new Intent(this,FeedbackActivity.class);
                startActivity(intent);
                break;
        }
    }
}
