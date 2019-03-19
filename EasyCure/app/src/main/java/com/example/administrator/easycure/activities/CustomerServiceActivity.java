package com.example.administrator.easycure.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/5 0005.
 */

public class CustomerServiceActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView activity_customer_service_iv1;
    private TextView activity_customer_service_tv2;
    private ListView activity_customer_service_lv;

    private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        init();

        initData();
    }

    public void init(){

        /**
         * activity_customer_service_iv1：返回上一页
         * activity_customer_service_tv2：用户名
         */
        activity_customer_service_iv1 = (ImageView)findViewById(R.id.activity_customer_service_iv1);
        activity_customer_service_tv2 = (TextView)findViewById(R.id.activity_customer_service_tv2);
        activity_customer_service_lv = (ListView)findViewById(R.id.activity_customer_service_lv);

        activity_customer_service_iv1.setOnClickListener(this);
        activity_customer_service_lv.setOnItemClickListener(this);

    }

    public void initData(){

        List<String> list = new ArrayList<>();

        list.add(getResources().getString(R.string.modify_security_num));
        list.add(getResources().getString(R.string.modify_secret_issus));
        list.add(getResources().getString(R.string.modify_phone_num));
        list.add(getResources().getString(R.string.use_shopping_store));
        list.add(getResources().getString(R.string.use_location_services));

        arrayAdapter = new ArrayAdapter(this,R.layout.customer_service_lv_item,list);
        activity_customer_service_lv.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_customer_service_iv1:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:     //如何修改安全手机号

                break;
            case 1:     //如何修改密保问题

                break;
            case 2:     //如何更改绑定的手机号

                break;
            case 3:     //如何使用购物商店

                break;
            case 4:     //如何正确使用定位服务

                break;

        }
    }
}
