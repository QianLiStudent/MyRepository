package com.example.administrator.easycure.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2018/12/13 0013.
 */

public class CheckPermissionUtil {

    //一次检查传入的所有权限是否都已经获取了
    public static boolean checkPermissionAllGaanted(Context context, String[] permissions){

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                //只要一个权限没有获取就返回false
                return false;
            }
        }
        return true;
    }
}
