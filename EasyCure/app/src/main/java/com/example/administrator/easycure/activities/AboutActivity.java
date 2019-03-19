package com.example.administrator.easycure.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.AboutAdapter;
import com.example.administrator.easycure.adapters.VersionInformationAdapter;
import com.example.administrator.easycure.components.RadioDialog;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/2 0002.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView activity_about_iv1;
    private TextView activity_about_tv;
    private ListView activity_about_lv;

    private AboutAdapter aboutAdapter;
    private List<Map<String,String>> list = new ArrayList<>();
    private Map<String,String> map;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();

        initData();
    }

    public void init(){
        activity_about_iv1 = (ImageView)findViewById(R.id.activity_about_iv1);
        //这个activity_about_tv是版本号信息，用来显示当前的版本号
        activity_about_tv = (TextView)findViewById(R.id.activity_about_tv);
        activity_about_tv.setText(Constant.getVersionInfo(this).get("versionName"));

        activity_about_lv = (ListView)findViewById(R.id.activity_about_lv);

        activity_about_iv1.setOnClickListener(this);

        activity_about_lv.setOnItemClickListener(this);

        aboutAdapter = new AboutAdapter(this,list);
    }

    public void initData(){
        map = new HashMap<>();
        map.put("title",getResources().getString(R.string.check_update));
        if(SpUtil.getUpdateStatus(this)){
            //表示当前是最新版本
            map.put("message",getResources().getString(R.string.latest_version));
        }else{
            //表示当前不是最新版本
            map.put("message",getResources().getString(R.string.detected_latest_version));
        }

        list.add(map);

        map = new HashMap<>();
        map.put("title",getResources().getString(R.string.release_notes));
        list.add(map);

        map = new HashMap<>();
        map.put("title",getResources().getString(R.string.version_Information));
        list.add(map);

        map = new HashMap<>();
        map.put("title",getResources().getString(R.string.service_agreement));
        list.add(map);

        map = new HashMap<>();
        map.put("title",getResources().getString(R.string.automatically_update));
        if(SpUtil.getAutomaticUpdateStatus(this).equals(getResources().getString(R.string.only_wifi))){
            map.put("message",getResources().getString(R.string.only_wifi));
        }else{
            map.put("message",getResources().getString(R.string.never));
        }

        list.add(map);

        aboutAdapter = new AboutAdapter(this,list);
        activity_about_lv.setAdapter(aboutAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_about_iv1:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:     //检测更新

                break;
            case 1:     //版本说明
                intent = new Intent(this,ReleaseNotesActivity.class);
                startActivity(intent);
                break;
            case 2:     //版本信息
                intent = new Intent(this, VersionInformationActivity.class);
                startActivity(intent);
                break;
            case 3:     //服务协议
                intent= new Intent(this,ServiceAgreement.class);
                startActivity(intent);
                break;
            case 4:     //自动下载更新
                new RadioDialog(this) {
                    @Override
                    public void setStatus(String status) {
                        list.get(list.size() - 1).put("message",status);
                        aboutAdapter.notifyDataSetChanged();
                    }
                }.show();
                break;
        }
    }
}
