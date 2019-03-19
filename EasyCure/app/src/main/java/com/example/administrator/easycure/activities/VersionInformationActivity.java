package com.example.administrator.easycure.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.VersionInfo;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.VersionInformationAdapter;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.DBControler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/3 0003.
 */

public class VersionInformationActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_version_information_iv;
    private ListView activity_version_information_lv;

    private List<VersionInfo> list;

    private VersionInformationAdapter versionInformationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_information);

        init();

        initData();
    }

    public void init(){
        activity_version_information_iv = (ImageView)findViewById(R.id.activity_version_information_iv);
        activity_version_information_lv = (ListView)findViewById(R.id.activity_version_information_lv);

        activity_version_information_iv.setOnClickListener(this);
    }

    public void initData(){

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersionName(getResources().getString(R.string.version_name));
        versionInfo.setVersionNum(getResources().getString(R.string.version));

        list = DBControler.selectAllVersionInfo();
        System.out.println(list.size());
        list.add(0,versionInfo);

        //这里就用本地的数据库来保存版本信息吧，这样就能在非网络情况下查看版本信息了

        if(list.size() > 1){
            //表示查得到之前保存的版本信息
            versionInformationAdapter = new VersionInformationAdapter(this,list);

            activity_version_information_lv.setAdapter(versionInformationAdapter);
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_version_information_iv:
                finish();
                break;
        }
    }

    /**
     * 以下3个方法为数据库测试方法
     */
    public void click1(View view){
        Map<String,String> map = new HashMap<>();
        map.put("versionName","甜筒");
        map.put("versionNum","1.0.0");
        if(DBControler.addVersionInfoItem(map)){
            Toast.makeText(this,"插入数据成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"插入数据失败",Toast.LENGTH_SHORT).show();
        }
    }
    public void click2(View view){
        DBControler.deleteVersionInfoItem(1);
    }
    public void click3(View view){
        if(DBControler.updateVersionInfoItem(1,"甜甜圈","2.0.0")){
            Toast.makeText(this,"更新成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"更新失败",Toast.LENGTH_SHORT).show();
        }
    }
}
