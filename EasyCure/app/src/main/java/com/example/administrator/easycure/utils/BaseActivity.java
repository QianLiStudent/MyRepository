package com.example.administrator.easycure.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Administrator on 2018/10/20 0020.
 */

public class BaseActivity extends Activity {

    private BroadcastReceiver receiver;
   protected void  onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);

       //注册关闭所有界面的监听事件
       IntentFilter filter = new IntentFilter();
       filter.addAction("close_all_Activity");

       receiver = new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {
               unregisterReceiver(this);
               ((Activity)context).finish();
           }
       };

       registerReceiver(receiver,filter);
   }

   //发送关闭所有界面的广播
   public void close(){
       Intent intent = new Intent();
       intent.setAction("close_all_Activity");
       sendBroadcast(intent);
       finish();
   }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }
}
