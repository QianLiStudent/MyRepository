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

//在User数据表中加入一条数据，表示一个用户注册账号了
    public static boolean addAccountItem(Map<String,String> map){
        User user = new User();
        //把设置参数的代码封装到函数中，以便复用
        setUserItemArgs(user,map);
        //调用save（）直接保存进数据库
        return user.save();
    }

    //查找指定手机号码的用户数据
    public static User selectAccountItem(String phoneNumber){
        Cursor cursor = DataSupport.findBySQL("select * from " + Constant.TABLE_NAME_USER + " where phoneNumber='" + phoneNumber + "'");
        User user = null;

        if(cursor != null){
            if(cursor.moveToFirst()){

                user = new User();
                //列参数不要用之前定义的常量，否则取不到相应的数据
                user.setUserName(cursor.getString(cursor.getColumnIndex("username")));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phonenumber")));
                user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                user.setSecurityPhoneNumber(cursor.getString(cursor.getColumnIndex("securityphonenumber")));
                user.setSecurityQuestion1(cursor.getString(cursor.getColumnIndex("securityquestion1")));
                user.setSecurityQuestion2(cursor.getString(cursor.getColumnIndex("securityquestion2")));
                user.setSecurityQuestion3(cursor.getString(cursor.getColumnIndex("securityquestion3")));
                user.setSecurityAnswer1(cursor.getString(cursor.getColumnIndex("securityanswer1")));
                user.setSecurityAnswer2(cursor.getString(cursor.getColumnIndex("securityanswer2")));
                user.setSecurityAnswer3(cursor.getString(cursor.getColumnIndex("securityanswer3")));
            }
        }
        return user;
    }

    //修改用户数据
    public static boolean updateAccountItem(String phoneNumber,Map<String,String> map){
        User user = selectUserItem(getIdInAccountTable(phoneNumber));
        setUserItemArgs(user,map);
        return user.save();
    }

    //通过User表中某条记录的id找到对应的数据并封装进user对象
    public static User selectUserItem(int id){

        return DataSupport.find(User.class,id);
    }

    //用于获取用户表中指定手机号的那条数据的id号
    public static int getIdInAccountTable(String phoneNumber){
        Cursor cursor = DataSupport.findBySQL("select * from " + Constant.TABLE_NAME_USER + " where " + Constant.PHONENUMBER + "=" + phoneNumber);
        if(cursor != null && cursor.moveToFirst()){
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            cursor.close();
            return id;
        }
        return -1;
    }

    public static void setUserItemArgs(User user,Map<String,String> map){
        if(map.get(Constant.USERNAME) != null && map.get(Constant.USERNAME).length() > 0){
            user.setUserName(map.get(Constant.USERNAME));
        }
        if(map.get(Constant.PHONENUMBER) != null && map.get(Constant.PHONENUMBER).length() > 0){
            user.setPhoneNumber(map.get(Constant.PHONENUMBER));
        }
        if(map.get(Constant.PASSWORD) != null && map.get(Constant.PASSWORD).length() > 0){
            user.setPassword(map.get(Constant.PASSWORD));
        }
        if(map.get(Constant.SECURITY_NUMBER) != null && map.get(Constant.SECURITY_NUMBER).length() > 0){
            user.setSecurityPhoneNumber(map.get(Constant.SECURITY_NUMBER));
        }
        if(map.get(Constant.SECURITY_QUESTION1) != null && map.get(Constant.SECURITY_QUESTION1).length() > 0){
            user.setSecurityQuestion1(map.get(Constant.SECURITY_QUESTION1));
        }
        if(map.get(Constant.SECURITY_QUESTION2) != null && map.get(Constant.SECURITY_QUESTION2).length() > 0){
            user.setSecurityQuestion2(map.get(Constant.SECURITY_QUESTION2));
        }
        if(map.get(Constant.SECURITY_QUESTION3) != null && map.get(Constant.SECURITY_QUESTION3).length() > 0){
            user.setSecurityQuestion3(map.get(Constant.SECURITY_QUESTION3));
        }
        if(map.get(Constant.SECURITY_ANSWER1) != null && map.get(Constant.SECURITY_ANSWER1).length() > 0){
            user.setSecurityAnswer1(map.get(Constant.SECURITY_ANSWER1));
        }
        if(map.get(Constant.SECURITY_ANSWER2) != null && map.get(Constant.SECURITY_ANSWER2).length() > 0){
            user.setSecurityAnswer2(map.get(Constant.SECURITY_ANSWER2));
        }
        if(map.get(Constant.SECURITY_ANSWER3) != null && map.get(Constant.SECURITY_ANSWER3).length() > 0){
            user.setSecurityAnswer3(map.get(Constant.SECURITY_ANSWER3));
        }

    }

//----------------------------------------------------上面的是用户账号的数据表--------------------------------------------------
//后面要把用户账号数据库搬运到服务器端
    
    public static void setVersionInfoItemArgs(VersionInfo versionInfo,Map<String,String> map){
        versionInfo.setVersionName(map.get("versionName"));
        versionInfo.setVersionNum(map.get("versionNum"));
    }
    
    //在VersionInfo数据表中加入一条数据，表示增加了一条新的版本信息
    public static boolean addVersionInfoItem(Map<String,String> map){
        VersionInfo versionInfo = new VersionInfo();
        //把设置参数的代码封装到函数中，以便复用
        setVersionInfoItemArgs(versionInfo,map);
        //调用save（）直接保存进数据库
        return versionInfo.save();
    }
    
    //查询VersionInfo数据表中的一条数据，因为版本信息只设计为让用户浏览而不能做任何操作，因此只需要一次取出所有数据即可
    //这里不需要考虑分页查询，因为版本更新产生的一条一条的数据量并不大
    public static List<VersionInfo> selectAllVersionInfo(){
        List<VersionInfo> list = new ArrayList<>();
        Cursor cursor = DataSupport.findBySQL("select * from " + Constant.TABLE_NAME_VERSION_INFO);
        while(cursor != null && cursor.moveToNext()){
            String versionName = cursor.getString(cursor.getColumnIndex("versionname"));
            String versionNum = cursor.getString(cursor.getColumnIndex("versionnum"));
            
            VersionInfo versionInfo = new VersionInfo();
            versionInfo.setVersionName(versionName);
            versionInfo.setVersionNum(versionNum);
            list.add(versionInfo);
        }
        
        cursor.close();
        
        return list;
    }
    
    //修改某一条版本信息的内容，这个功能只有开发者享有
    public static boolean updateVersionInfoItem(int index,String versionName,String versionNum){

        VersionInfo versionInfo = null;
        
        Cursor cursor = DataSupport.findBySQL("select * from (select * from " + Constant.TABLE_NAME_VERSION_INFO +
                " order by id limit 0," + index + ") a order by id desc limit 0,1");
        
        if(cursor != null && cursor.moveToFirst()){
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            versionInfo = DataSupport.find(VersionInfo.class,id);

            Map<String,String> map = new HashMap<>();
            map.put("versionName",versionName);
            map.put("versionNum",versionNum);
            setVersionInfoItemArgs(versionInfo,map);
            
            cursor.close();

            return versionInfo.save();
        }
        return false;
       
    }
    
    //删除某一条版本信息的内容，这个功能只有开发者享有,index表示相对位置
    public static boolean deleteVersionInfoItem(int index){
        boolean isDeletedSuccessful = DataSupport.delete(VersionInfo.class, getId(index,Constant.TABLE_NAME_VERSION_INFO)) > 0;
        return isDeletedSuccessful;
    }
    
}
