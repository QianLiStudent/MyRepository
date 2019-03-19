package com.example.administrator.easycure.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

/**
 * Created by Administrator on 2018/11/3 0003.
 */

public class ServiceAgreement extends BaseActivity implements View.OnClickListener{

    private ImageView activity_service_agreement_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);

        init();
    }

    public void init(){
        activity_service_agreement_iv = (ImageView)findViewById(R.id.activity_service_agreement_iv);

        activity_service_agreement_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_service_agreement_iv:
                finish();
                break;
        }
    }
}
