package com.example.administrator.easycure.JavaBean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/11/3 0003.
 */

public class VersionInfo extends DataSupport {

    private String versionName;
    private String versionNum;

    public String getVersionName(){
        return this.versionName;
    }
    public void setVersionName(String versionName){
        this.versionName = versionName;
    }

    public String getVersionNum(){
        return this.versionNum;
    }
    public void setVersionNum(String versionNum){
        this.versionNum = versionNum;
    }
}
