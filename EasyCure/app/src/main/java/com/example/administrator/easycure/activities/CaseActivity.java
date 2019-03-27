package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.administrator.easycure.FragmentSet.FragmentCase;
import com.example.administrator.easycure.JavaBean.Illness;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/20 0020.
 */

public class CaseActivity extends FragmentActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private FrameLayout activity_case_fl;

    private RadioGroup activity_case_rg;
    private RadioButton activity_case_rb1;
    private RadioButton activity_case_rb2;
    private RadioButton activity_case_rb3;

    private Intent mIntent;

    private List<Illness> mResultList;
    
    private List<FragmentCase> mfragmentList = new ArrayList<>();

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_case);

        init();

        initData();
    }

    public void init(){
        activity_case_fl = (FrameLayout)findViewById(R.id.activity_case_fl);

        activity_case_rg = (RadioGroup)findViewById(R.id.activity_case_rg);
        activity_case_rb1 = (RadioButton)findViewById(R.id.activity_case_rb1);
        activity_case_rb2 = (RadioButton)findViewById(R.id.activity_case_rb2);
        activity_case_rb3 = (RadioButton)findViewById(R.id.activity_case_rb3);

        activity_case_rg.setOnCheckedChangeListener(this);

    }

    public void initData(){
        mIntent = getIntent();

        mResultList = (List<Illness>)mIntent.getSerializableExtra("checkedIllness");

        fragmentManager = getSupportFragmentManager();

        int count = mResultList.size(); //count的值只可能是1、2、3钟的其中一个

        switch(count){
            case 1:
                activity_case_rb1.setVisibility(View.VISIBLE);
                break;
            case 2:
                activity_case_rb1.setVisibility(View.VISIBLE);
                activity_case_rb2.setVisibility(View.VISIBLE);
                break;
            case 3:
                activity_case_rb1.setVisibility(View.VISIBLE);
                activity_case_rb2.setVisibility(View.VISIBLE);
                activity_case_rb3.setVisibility(View.VISIBLE);
                break;
        }

        for(int i = 0;i < count;i++){
            FragmentCase fragment = new FragmentCase();
            Bundle bundle = new Bundle();
            bundle.putInt("count",count);
            bundle.putInt("fragmentId",i);
            bundle.putSerializable("illness",mResultList.get(i));
            fragment.setArguments(bundle);

            mfragmentList.add(fragment);
        }

        activity_case_rb1.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_case_save_btn:
                //点击按钮把用户病例通过网络请求写入数据库保存，在用户个人界面可以调出来看
                Intent intent = new Intent(this,FunctionChooseActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch(checkedId){
            case R.id.activity_case_rb1:    //对应集合中的索引0
                switchView(0);
                break;
            case R.id.activity_case_rb2:    //对应集合中的索引1
                switchView(1);
                break;
            case R.id.activity_case_rb3:    //对应集合中的索引2
                switchView(2);
                break;
        }
    }

    //切换fragment视图
    public void switchView(int index){
        fragmentManager.beginTransaction().replace(R.id.activity_case_fl,mfragmentList.get(index)).commit();
    }

    @Override
    protected void onDestroy() {
        for(int i = 0;i < 3;i++){
            SpUtil.saveCaseUploadStatusTmp(this,"fragment" + i,false);
        }

        super.onDestroy();

    }
}
