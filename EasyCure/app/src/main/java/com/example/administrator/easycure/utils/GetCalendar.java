package com.example.administrator.easycure.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/10/21 0021.
 */

public class GetCalendar {

    public static int year,month,day,hour,minute;
    public static Calendar calendar;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    public static String time;

    public static String getSettingTime(int year,int month,int day,int hour,int minute){
        calendar = Calendar.getInstance();
        calendar.set(year,month,day,hour,minute);
        time = simpleDateFormat.format(calendar.getTime());
        return time;
    }

    public static String getTime(){
        calendar = Calendar.getInstance();
        time = simpleDateFormat.format(calendar.getTime());
        return time;
    }

    public static Date stringToDate(String time){
        try{
          return simpleDateFormat.parse(time);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
