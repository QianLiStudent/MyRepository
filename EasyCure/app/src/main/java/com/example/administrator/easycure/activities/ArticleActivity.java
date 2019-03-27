package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.CacheUtil;
import com.example.administrator.easycure.utils.RandomColor;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2019/3/17 0017.
 */

public class ArticleActivity extends BaseActivity implements View.OnClickListener{

    private TextView activity_article_tv_title,activity_article_tv_msg;
    private ImageView activity_article_iv,activity_article_iv2;

    private List<Map<String,String>> mapList;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        init();

        initData();
    }

    public void init(){

        mIntent = getIntent();

        activity_article_tv_title = (TextView)findViewById(R.id.activity_article_tv_title);
        activity_article_tv_msg = (TextView)findViewById(R.id.activity_article_tv_msg);

        activity_article_iv = (ImageView)findViewById(R.id.activity_article_iv);
        activity_article_iv2 = (ImageView)findViewById(R.id.activity_article_iv2);
        activity_article_iv.setOnClickListener(this);
    }

    public void initData(){

        //读缓存数据---------------------------------------------------
        int article_id = mIntent.getIntExtra("article_id",0);

        String title = (String)(CacheUtil.getCacheData(this,"article" + article_id + "_title","String"));
        String content = (String)(CacheUtil.getCacheData(this,"article" + article_id + "_content","String"));
        content = content.replaceAll("&","\n").replaceAll("@","\u3000\u3000");
        Bitmap img1 = (Bitmap)(CacheUtil.getCacheData(this,"article" + article_id + "_img1","Bitmap"));
        Bitmap img2 = (Bitmap)(CacheUtil.getCacheData(this,"article" + article_id + "_img2","Bitmap"));
        Bitmap img3 = (Bitmap)(CacheUtil.getCacheData(this,"article" + article_id + "_img3","Bitmap"));

        activity_article_tv_title.setText(title);
        activity_article_tv_msg.setText(content);
        activity_article_tv_msg.setTextColor(RandomColor.getRamdomColor());

        Bitmap[] imgs = {img1,img2,img3};

        int rand = (int)Math.round(Math.random()*2);
        activity_article_iv2.setImageBitmap(imgs[rand]);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_article_iv:
                finish();
                break;
        }
    }
}
