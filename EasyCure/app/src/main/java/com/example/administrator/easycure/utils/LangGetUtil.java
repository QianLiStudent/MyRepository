package com.example.administrator.easycure.utils;

import android.os.Build;

import java.util.Locale;

/**
 * Created by Administrator on 2019/3/17 0017.
 */

public class LangGetUtil {
    //获取手机当前语言，zh：中文   en：英文
    public static String langGet(){
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();

        return lang;
    }
}
