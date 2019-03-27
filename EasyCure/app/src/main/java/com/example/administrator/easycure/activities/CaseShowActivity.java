package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.easycure.JavaBean.Illness;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class CaseShowActivity extends BaseActivity implements View.OnClickListener{

    private TextView activity_case_show_id;
    private TextView activity_case_show_illness_name;
    private TextView activity_case_show_illness_description;
    private TextView activity_case_show_illness_polytype;
    private TextView activity_case_show_clinical_feature;
    private TextView activity_case_show_drug_recommend;
    private TextView activity_case_show_create_time;

    private Button activity_case_show_btn;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_show);

        init();

        initData();

    }

    public void init(){
        activity_case_show_id = (TextView)findViewById(R.id.activity_case_show_id);
        activity_case_show_illness_name = (TextView)findViewById(R.id.activity_case_show_illness_name);
        activity_case_show_illness_description = (TextView)findViewById(R.id.activity_case_show_illness_description);
        activity_case_show_illness_polytype = (TextView)findViewById(R.id.activity_case_show_illness_polytype);
        activity_case_show_clinical_feature = (TextView)findViewById(R.id.activity_case_show_clinical_feature);
        activity_case_show_drug_recommend = (TextView)findViewById(R.id.activity_case_show_drug_recommend);
        activity_case_show_create_time = (TextView)findViewById(R.id.activity_case_show_create_time);

        activity_case_show_btn = (Button)findViewById(R.id.activity_case_show_btn);

        activity_case_show_btn.setOnClickListener(this);
    }

    public void initData(){
        mIntent = getIntent();

        Illness illness = (Illness)mIntent.getSerializableExtra("illness");

        activity_case_show_illness_name.setText(illness.getIllnessName());
        activity_case_show_illness_description.setText("\u3000\u3000" + illness.getIllnessDescription());
        activity_case_show_illness_polytype.setText(illness.getIllnessPolytype());
        activity_case_show_clinical_feature.setText("\u3000\u3000" + illness.getClinicalFeature());

        String[] plans = illness.getDrugRecommend().split("/");

        String drugRecommend = "";

        for(int i = 0;i < plans.length;i++){
            drugRecommend += "\u3000\u3000" + getResources().getString(R.string.illness_plan) + (i + 1) + ": " + plans[i] + "\n";
        }

        activity_case_show_drug_recommend.setText(drugRecommend);

        activity_case_show_id.setText(mIntent.getStringExtra("username"));
        activity_case_show_create_time.setText(mIntent.getStringExtra("createTime"));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_case_show_btn:
                finish();
                break;
        }
    }


}
