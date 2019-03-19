package com.example.administrator.easycure;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.mob.MobSDK;

import org.litepal.LitePal;
import org.xutils.x;

/**
 * Created by Administrator on 2018/10/26 0026.
 */

public class EasyCureApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        MobSDK.init(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        x.Ext.init(this);   //初始化x类
        x.Ext.setDebug(false);    //关闭xutils调试模式，否则会影响性能

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
