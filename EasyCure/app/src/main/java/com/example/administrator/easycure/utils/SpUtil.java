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

    //保存所有用户信息，用户每次登陆都会先读取该文件，如果用户修改了登陆的账号则需要重新请求身份验证，即进行网络请求，这时候这些数据需要全部替换
    public static void saveAllUserInfo(Context context,String phoneNumValue,String passwordValue,String username){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PHONENUMBER,phoneNumValue);
        editor.putString(Constant.PASSWORD,passwordValue);
        editor.putString(Constant.USERNAME,username);
        editor.commit();
    }

    public static void saveRememberPasswordState(Context context,boolean isRemember){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.REMEMBER_PASSWORD,isRemember);

        editor.commit();
    }

    //从SP文件中取出用户信息，只取用户的登录账号（手机号码）和密码
    public static Map<String,String> getUserInfo(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        map = new HashMap<>();
        map.put(Constant.PHONENUMBER,sharedPreferences.getString(Constant.PHONENUMBER,""));
        map.put(Constant.PASSWORD,sharedPreferences.getString(Constant.PASSWORD,""));
        map.put(Constant.REMEMBER_PASSWORD,String.valueOf(sharedPreferences.getBoolean(Constant.REMEMBER_PASSWORD,false)));

        return map;
    }

    //从SP文件中取出用户信息，只取用户的登录账号（密码
    public static String getPassword(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        return sharedPreferences.getString(Constant.PASSWORD,"");
    }

    //修改用户名
    public static void modifyUsername(Context context,String username){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constant.USERNAME,username);
        editor.commit();
    }

    //从SP文件中取出用户注册时临时保存的数据
    public static String getTmpData(Context context,String tmpKey){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        return sharedPreferences.getString(tmpKey,"");
    }

    //删除密码，在用户修改密码或者重置信息的时候调用
    public static void removePassword(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(Constant.PASSWORD);

        editor.commit();
    }

    //获得手机号码
    public static String getPhonenumber(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        return sharedPreferences.getString(Constant.PHONENUMBER,"136xxxxxx68");
    }

    //获得用户名
    public static String getUsername(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        return sharedPreferences.getString(Constant.USERNAME,"X-MAN");
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

    //当用户注册时候由于多有个界面的数据，所以我们为了做数据传递到最后一个界面我们就选择用SP文件来做临时存储，结束注册后把这些信息全部清空
    public static void saveUserInfoTmp(Context context,String tmpKey,String tmpVal){
        sharedPreferences = context.getSharedPreferences(Constant.USERINFOSPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(tmpKey,tmpVal);

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

    //保存病例操作状态
    public static void saveCaseUploadStatusTmp(Context context,String key,boolean isOperating){
        sharedPreferences = context.getSharedPreferences(Constant.CASESPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key,isOperating);
        editor.commit();
    }

    //获取病例操作状态
    public static boolean getCaseUploadStatusTmp(Context context,String key){
        sharedPreferences = context.getSharedPreferences(Constant.CASESPFILE,context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(key,false);
    }

    //获取所有病例操作状态
    public static boolean[] getAllCaseOperatingStatus(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.CASESPFILE,context.MODE_PRIVATE);

        return new boolean[]{sharedPreferences.getBoolean("fragment0",false),
                sharedPreferences.getBoolean("fragment1",false),
                sharedPreferences.getBoolean("fragment2",false)};
    }

    public static void removeAllCaseOperatingRecord(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.CASESPFILE,context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("fragment0",false);
        editor.putBoolean("fragment1",false);
        editor.putBoolean("fragment2",false);

        editor.commit();
    }
}
