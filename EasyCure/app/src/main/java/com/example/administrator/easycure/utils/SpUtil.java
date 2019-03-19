package com.example.administrator.easycure.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/10/26 0026.
 */

public class SpUtil {

    public static SharedPreferences sharedPreferences;
    public static Map<String,String> map;

    //保存用户信息到SP文件中
    public static void saveUserInfo(Context context,String passwordValue,
                                    String phoneNumValue){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PASSWORD,passwordValue);
        editor.putString(Constant.PHONENUMBER,phoneNumValue);
        editor.commit();
    }

    //从SP文件中取出用户信息，这里是活的一部分，不包括绑定的用户安全号码
    public static Map<String,String> getUserInfo(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        map = new HashMap<>();
        map.put(Constant.PHONENUMBER,sharedPreferences.getString(Constant.PHONENUMBER,""));
        map.put(Constant.PASSWORD,sharedPreferences.getString(Constant.PASSWORD,""));

        return map;
    }

    //仅保存用户登录的账号
    public static void saveUserLoginAccount(Context context,String phoneNumValue){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PHONENUMBER,phoneNumValue);
        editor.commit();
    }

    //保存用户的登录状态，用来判断用户当前是否正在登陆
    public static void saveLoginStatus(Context context,boolean isLoginNow){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.ISLOGINNOW,isLoginNow);
        editor.commit();
    }

    //判断用户的登录状态，如果返回true则表示用户要默认登录，false则表示需要用户点击登录才能登录
    public static boolean getLoginStatus(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constant.ISLOGINNOW,false);
    }

    //保存安全号码
    public static void saveSecurityNumber(Context context,String securityNumberValue){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.SECURITY_NUMBER,securityNumberValue);
        editor.commit();
    }

    //--------------------------------以上写的是把用户的基本信息保存在SP文件中做二次登陆时的渲染--------------------------------------

    //保存自动更新的状态：仅WiFi下自动更新 或 从不自动更新
    public static void saveAutomaticUpdateStatus(Context context,String updateStatus){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.UPDATE_STATUS,updateStatus);
        editor.commit();
    }

    //获取自动更新的状态：仅WiFi下自动更新 或 从不自动更新
    public static String getAutomaticUpdateStatus(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        return sharedPreferences.getString(Constant.UPDATE_STATUS,"");
    }

    //记录当前版本是否为最新版本的bool值，true表示当前为最新版本，false表示不是最新版本
    public static void saveUpdateStatus(Context context,boolean isLatestVersion){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.IS_LATEST_VERSION,isLatestVersion);
        editor.commit();
    }

    //拿到当前版本是否为最新版的判断值
    public static boolean getUpdateStatus(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constant.IS_LATEST_VERSION,false);
    }

    //保存用户选择病症的历史记录
    public static void addHistoryRecord(Context context,String diseaseName){
        sharedPreferences = context.getSharedPreferences(Constant.DISEASERECORDSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(diseaseName,diseaseName);
        editor.commit();
    }

    //获得用户查询病症的历史记录
    public static List<String> getAllDiseaseHistoryRecord(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.DISEASERECORDSPFILE,context.MODE_PRIVATE);

        List<String> list = new ArrayList<>();

        Map<String,String> map = (Map<String, String>) sharedPreferences.getAll();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();

        while(iterator.hasNext()){
            list.add(map.get(iterator.next()));
        }
        return list;
    }

    //删除单个历史记录（用户点击标签右上角x的时候即删除该记录）
    public static void removeDiseaseHistoryRecord(Context context,String diseaseName){
        sharedPreferences = context.getSharedPreferences(Constant.DISEASERECORDSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(diseaseName);

        editor.commit();
    }

    //清空所有查询病症的历史记录
    public static void removeAllDiseaseHistoryRecord(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.DISEASERECORDSPFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.commit();
    }
}
