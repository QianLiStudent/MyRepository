package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.CacheUtil;
import java.util.List;


/**
 * Created by Administrator on 2019/3/17 0017.
 */

//具体建议的显示界面
public class AdviceActivity extends BaseActivity implements View.OnClickListener{

    private Intent mIntent;

    private TextView activity_advice_tv_title,activity_advice_tv_msg;
    private ImageView activity_advice_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        init();

        initData();
    }

    public void init(){
        mIntent = getIntent();

        //大标题
        activity_advice_tv_title = (TextView)findViewById(R.id.activity_advice_tv_title);
        //正文
        activity_advice_tv_msg = (TextView)findViewById(R.id.activity_advice_tv_msg);

        //返回上一个界面
        activity_advice_iv = (ImageView)findViewById(R.id.activity_advice_iv);
        activity_advice_iv.setOnClickListener(this);
    }

    public void initData(){

        //读缓存数据---------------------------------------------------

        int type_id = mIntent.getIntExtra("type_id",0);
        String title = mIntent.getStringExtra("title");

        //设置标题
        activity_advice_tv_title.setText(title);

        List<String> results = CacheUtil.getVagueCacheData(this,"advice" + type_id);

        for(int i = results.size() - 1;i >= 0;i--){
            activity_advice_tv_msg.setText(activity_advice_tv_msg.getText().toString()
                    + results.get(i).replaceAll("@","\u3000\u3000").replaceAll("&","\n"));
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_advice_iv:
                finish();
                break;
        }
    }
}
