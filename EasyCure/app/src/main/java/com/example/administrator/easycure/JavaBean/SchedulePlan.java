package com.example.administrator.easycure.JavaBean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/10/23 0023.
 */

public class SchedulePlan extends DataSupport {

    /**
     * id 表示在表中的id
     * title 表示备忘录标题
     * time 表示备忘录中设置的准备执行计划的事件
     * createMonth 表示备忘录创建的月份
     * createDay 表示备忘录创建当天在该月中的第几号
     * message 表示备忘录正文
     */
    private int id;

    private String title;

    private String time;

    private String createMonth;

    private String createDay;

    private String message;

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getTime(){
        return this.time;
    }
    public void setTime(String time){
        this.time = time;
    }

    public String getCreateMonth(){
        return this.createMonth;
    }
    public void setCreateMonth(String createMonth){
        this.createMonth = createMonth;
    }

    public String getCreateDay(){
        return this.createDay;
    }
    public void setCreateDay(String createDay){
        this.createDay = createDay;
    }

    public String getMessage(){
        return this.message;
    }
    public void setMessage(String message){
        this.message = message;
    }
}
