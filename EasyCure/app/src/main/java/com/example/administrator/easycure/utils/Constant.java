package com.example.administrator.easycure.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/23 0023.
 */

public class Constant {

    //数据库中备忘录表名
    public static final String TABLE_NAME_SCHEDULE_PLAN = "SchedulePlan";

    //月份数组
    public static final String[] MONTH_ARRAY = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};

    //保存用户某些信息的sp文件
    public static final String USERINFOSPFILE = "userInfoSpFile";

    //保存用户查询病症的历史记录的sp文件
    public static final String DISEASERECORDSPFILE = "diseaseRecodeSpFile";

    //保存病例操作记录的sp文件
    public static final String CASESPFILE = "caseSpFile";

    //用户名标识
    public static final String USERNAME = "username";

    //用户密码标识
    public static final String PASSWORD = "password";

    //用户手机号标识
    public static final String PHONENUMBER = "phonenumber";

    //用户绑定安全号码标识
    public static final String SECURITY_NUMBER = "security_number";

    //检查更新
    public static final String CHECK_UPDATE = "checkUpdate";
    //版本说明
    public static final String RELEASE_NOTES = "releaseNotes";
    //版本信息
    public static final String VERSION_INFORMATION = "versionInformation";
    //服务协议
    public static final String SERVICE_AGREEMENT = "serviceAgreement";
    //自动下载安装EasyCure更新包
    public static final String UPDATE_STATUS = "updateStatus";

    //用户的登录状态
    public static final String ISLOGINNOW = "isLoginNow";
    //记住密码状态
    public static final String REMEMBER_PASSWORD = "isRemember";

    public static final String IS_LATEST_VERSION = "isLatestVersion";

    //标签最大选中数
    public static final int MAX_SELECTED = 0;

    //拿到当前app的版本信息：包括版本号和版本名称
    public static final Map<String,String> getVersionInfo(Context context){

        Map<String,String> map = new HashMap<>();

        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            map.put("versionCode",packageInfo.versionCode + "");    //保存版本号信息
            map.put("versionName",packageInfo.versionName);     //保存保本名称信息
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
