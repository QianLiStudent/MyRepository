package com.example.administrator.easycure.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Window;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.SpUtil;

/**
 * Created by Administrator on 2018/11/4 0004.
 */

public class SplashActivity extends BaseActivity {

    private Intent intent;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isLoginNow = SpUtil.getLoginStatus(SplashActivity.this);
            if(isLoginNow){
                intent = new Intent(SplashActivity.this,MainActivity.class);
                intent.putExtra("welcome_back",true);
                startActivity(intent);
            }else{
                intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
            }

            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.sendEmptyMessageDelayed(0,3000);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
