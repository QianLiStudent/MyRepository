package com.example.administrator.easycure.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.ProblemLvAdapter;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/5 0005.
 */

public class CustomerServiceActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView activity_customer_service_iv1;
    private TextView activity_customer_service_tv2,activity_customer_service_tv4;
    private ListView activity_customer_service_lv;

    private ProblemLvAdapter problemLvAdapter;
    private List<Map<String,String>> mList = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    problemLvAdapter = new ProblemLvAdapter(CustomerServiceActivity.this,mList);
                    activity_customer_service_lv.setAdapter(problemLvAdapter);
                    break;
            }
        }
    };

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
        activity_customer_service_tv4 = (TextView)findViewById(R.id.activity_customer_service_tv4);
        activity_customer_service_lv = (ListView)findViewById(R.id.activity_customer_service_lv);

        activity_customer_service_iv1.setOnClickListener(this);
        activity_customer_service_lv.setOnItemClickListener(this);

    }

    public void initData(){

        activity_customer_service_tv2.setText(SpUtil.getUsername(this));

        if(NetworkUsable.isNetworkConnected(this)){
            getProblemsFromServer(LangGetUtil.langGet());
        }else{
            activity_customer_service_tv4.setVisibility(View.VISIBLE);
        }


    }

    public void getProblemsFromServer(final String lang){
        new Thread(new Runnable() {

            String urlStr = "http://119.23.208.63/ECure-system/public/index.php/getProblems/";

            InputStream is;

            @Override
            public void run() {
                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    String data = "lang=" + URLEncoder.encode(lang);

                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length",data.length() + "");

                    con.setDoOutput(true);

                    OutputStream os = con.getOutputStream();

                    os.write(data.getBytes());

                    int code = con.getResponseCode();

                    if(code == 200){

                        is = con.getInputStream();

                        String jsonStr = StrUtil.stream2String(is);

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        jsonStr.replace("[","").replace("]","");

                        List<JSONObject> jsons = RegexUtil.parseJsonStr2JsonObjList(jsonStr);

                        for(JSONObject json : jsons){
                            String problem_name = json.getString("problem_name");
                            String problem_solution = json.getString("problem_solution");

                            Map<String,String> map = new HashMap<>();

                            map.put("problem_name",problem_name);
                            map.put("problem_solution",problem_solution);

                            mList.add(map);
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
            case R.id.activity_customer_service_iv1:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView tv = view.findViewById(R.id.customer_service_lv_tv2);
        if(tv.getVisibility() == View.VISIBLE){
            //表示当前控件可见，需要设置成不可见
            tv.setVisibility(View.GONE);
        }else{
            tv.setVisibility(View.VISIBLE);
        }

        List<View> views = problemLvAdapter.getAllItems();

        for(View v : views){
            if(v != view){
                TextView tmpTv = v.findViewById(R.id.customer_service_lv_tv2);
                if(tmpTv.getVisibility() == View.VISIBLE){
                    tmpTv.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }
}
