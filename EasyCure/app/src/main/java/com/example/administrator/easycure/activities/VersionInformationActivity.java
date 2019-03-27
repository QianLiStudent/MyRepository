package com.example.administrator.easycure.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.VersionInfo;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.VersionInformationAdapter;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private List<VersionInfo> list = new ArrayList<>();

    private VersionInformationAdapter versionInformationAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    //表示查得到之前保存的版本信息
                    versionInformationAdapter = new VersionInformationAdapter(VersionInformationActivity.this,list);

                    activity_version_information_lv.setAdapter(versionInformationAdapter);
                    break;
            }
        }
    };

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

        list.add(versionInfo);

        if(NetworkUsable.isNetworkConnected(this)){
            //进入这里表示网络可用，就去向服务器请求版本信息的数据
            getVersionInfoFromServer();
        }else{
            //进入这里表示当前网络异常，就不显示任何信息

            Toast.makeText(this,getResources().getString(R.string.network_anomaly),Toast.LENGTH_SHORT).show();

            versionInformationAdapter = new VersionInformationAdapter(VersionInformationActivity.this,list);

            activity_version_information_lv.setAdapter(versionInformationAdapter);
        }
    }

    public void getVersionInfoFromServer(){
        new Thread(new Runnable() {

            String urlStr = "http://119.23.208.63/ECure-system/public/index.php/get_versionInfo";

            InputStream is;

            @Override
            public void run() {
                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();

                         String jsonStr = StrUtil.stream2String(is);

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        jsonStr = jsonStr.replace("[","").replace("]","");

                        List<JSONObject> jsons = RegexUtil.parseJsonStr2JsonObjList(jsonStr);

                        for(JSONObject json : jsons){
                            VersionInfo versionInfo = new VersionInfo();
                            versionInfo.setVersionName(json.getString("version_name"));
                            versionInfo.setVersionNum(json.getString("version_code"));

                            list.add(versionInfo);
                        }

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessageDelayed(msg,10);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }



            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_version_information_iv:
                finish();
                break;
        }
    }
}
