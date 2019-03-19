package com.example.administrator.easycure.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2018/12/13 0013.
 */

//WiFi连接检查工具
public class WiFiConnectChecked {

    /**
     * 判断当前设备是否处于WiFi连接状态
     * @return true表示处于WiFi连接状态，false表示非WiFi连接状态
     */
    public static boolean isWiFiConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }
}
