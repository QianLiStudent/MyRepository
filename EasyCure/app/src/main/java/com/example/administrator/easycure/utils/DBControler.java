package com.example.administrator.easycure.utils;

import android.database.Cursor;
import android.os.Build;

import com.example.administrator.easycure.JavaBean.SchedulePlan;
import com.example.administrator.easycure.JavaBean.User;
import com.example.administrator.easycure.JavaBean.VersionInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/23 0023.
 */

public class DBControler {

    public static Cursor cursor = null;

    //    与列表对应，所点击的item在列表中的第几行我们就从数据库找第几行

    public static int getId(int index,String table){
        cursor = DataSupport.findBySQL("select * from (select * from " + table +
                " order by id limit 0," + index + ") a order by id desc limit 0,1");
        if(cursor.moveToFirst()){
            index = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            cursor.close();
        }
        return index;
    }

    public static void setScheduleItemArgs(SchedulePlan plan,Map<String,String> map){
        plan.setTitle(map.get("title"));
        plan.setTime(map.get("time"));
        plan.setCreateMonth(map.get("createMonth"));
        plan.setCreateDay(map.get("createDay"));
        plan.setMessage(map.get("message"));
    }


//    在数据库中添加一条记录,map集合中保存的是一条item的所有数据
    public static boolean addScheduleItem(Map<String,String> map){
        SchedulePlan plan = new SchedulePlan();
        //把设置参数的代码封装到函数中，以便复用
        setScheduleItemArgs(plan,map);
        //调用save（）直接保存进数据库
        return plan.save();
    }

//    在数据库中删除一条记录，id表示所删除的item在数据库中的id
    public static boolean deleteScheduleItem(int index){
        boolean isDeletedSuccessful = DataSupport.delete(SchedulePlan.class, getId(index,Constant.TABLE_NAME_SCHEDULE_PLAN)) > 0;
        return isDeletedSuccessful;
    }

//    更新数据库中指定的记录
    public static boolean updateScheduleItem(int index,Map<String,String> map){
        SchedulePlan plan = DataSupport.find(SchedulePlan.class,getId(index,Constant.TABLE_NAME_SCHEDULE_PLAN));
        setScheduleItemArgs(plan,map);
        return plan.save();
    }

//    查询数据库中的指定id的记录
    public static SchedulePlan selectScheduleItem(int index){

        return DataSupport.find(SchedulePlan.class,getId(index,Constant.TABLE_NAME_SCHEDULE_PLAN));
    }

//    获得数据库中指定表的条目数
    public static int getTableItemCount(String tableName){
        return DataSupport.count(tableName);
    }

//----------------------------------------------------上面的是备忘录的数据表--------------------------------------------------
}
