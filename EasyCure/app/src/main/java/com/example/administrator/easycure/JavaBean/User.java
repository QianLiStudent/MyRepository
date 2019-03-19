package com.example.administrator.easycure.JavaBean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class User extends DataSupport {
    //用户名
    private String userName;

    //手机号
    private String phoneNumber;

    //密码
    private String password;

    //安全手机号码
    private String securityPhoneNumber;

    //3个密保问题
    private String securityQuestion1;
    private String securityQuestion2;
    private String securityQuestion3;

    //3个密保答案
    private String securityAnswer1;
    private String securityAnswer2;
    private String securityAnswer3;

    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityPhoneNumber() {
        return securityPhoneNumber;
    }

    public void setSecurityPhoneNumber(String securityPhoneNumber) {
        this.securityPhoneNumber = securityPhoneNumber;
    }

    public String getSecurityQuestion1() {
        return securityQuestion1;
    }

    public void setSecurityQuestion1(String securityQuestion1) {
        this.securityQuestion1 = securityQuestion1;
    }

    public String getSecurityQuestion2() {
        return securityQuestion2;
    }

    public void setSecurityQuestion2(String securityQuestion2) {
        this.securityQuestion2 = securityQuestion2;
    }

    public String getSecurityQuestion3() {
        return securityQuestion3;
    }

    public void setSecurityQuestion3(String securityQuestion3) {
        this.securityQuestion3 = securityQuestion3;
    }

    public String getSecurityAnswer1() {
        return securityAnswer1;
    }

    public void setSecurityAnswer1(String securityAnswer1) {
        this.securityAnswer1 = securityAnswer1;
    }

    public String getSecurityAnswer2() {
        return securityAnswer2;
    }

    public void setSecurityAnswer2(String securityAnswer2) {
        this.securityAnswer2 = securityAnswer2;
    }

    public String getSecurityAnswer3() {
        return securityAnswer3;
    }

    public void setSecurityAnswer3(String securityAnswer3) {
        this.securityAnswer3 = securityAnswer3;
    }
}
