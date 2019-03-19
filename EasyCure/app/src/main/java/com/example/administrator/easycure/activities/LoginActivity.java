package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.User;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.SpUtil;

import java.util.Map;

/**
 * Created by Administrator on 2018/10/19 0019.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private TextView activity_login_tv_forgotPwd,activity_login_tv_signUp;
    private EditText activity_login_et_phone,activity_login_et_pwd;
    private Button activity_login_btn_signin;

    private Intent intent;

    private Map<String,String> map;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件、对象和数据
        init();

        //从SP文件中读用户信息，如果本地没有保存信息则让用户自己写，否则自动填写
        initData();
    }

    public void init(){
        //忘记密码
        activity_login_tv_forgotPwd = (TextView)findViewById(R.id.activity_login_tv_forgotPwd);
        //注册
        activity_login_tv_signUp = (TextView)findViewById(R.id.activity_login_tv_signUp);
        //email输入框
        activity_login_et_phone = (EditText)findViewById(R.id.activity_login_et_phone);
        //密码输入框
        activity_login_et_pwd = (EditText)findViewById(R.id.activity_login_et_pwd);
        //登录按钮
        activity_login_btn_signin = (Button)findViewById(R.id.activity_login_btn_signin);

        activity_login_tv_forgotPwd.setOnClickListener(this);
        activity_login_tv_signUp.setOnClickListener(this);
        activity_login_btn_signin.setOnClickListener(this);
    }

    public void initData(){
        map = SpUtil.getUserInfo(this);

        activity_login_et_phone.setText(map.get(Constant.PHONENUMBER));
        activity_login_et_pwd.setText(map.get(Constant.PASSWORD));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_login_tv_forgotPwd:
                //跳转到密码找回页面
                intent = new Intent(this,ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_login_tv_signUp:
                //跳转到注册页面
                intent = new Intent(this,SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_login_btn_signin:
                /**
                 * 如果本地没有SP文件则直接访问服务器端的数据库进行密码验证
                 * 如果本地有SP文件并且里面保存了上一次登录的账号的账号和密码，则直接读SP文件
                 */
                if(activity_login_et_phone.getText().toString().trim().length() > 0){
                    if(activity_login_et_pwd.getText().toString().trim().length() > 0){
                        User user = DBControler.selectAccountItem(activity_login_et_phone.getText().toString().trim());
                        if(user != null){
                            if(activity_login_et_pwd.getText().toString().trim().equals(user.getPassword())){
                                intent = new Intent(this,MainActivity.class);
                                //把登录账号（手机号）写入SP文件，便于后面控制账号的某些功能
                                SpUtil.saveUserInfo(this,activity_login_et_pwd.getText().toString().trim(),activity_login_et_phone.getText().toString().trim());
                                SpUtil.saveLoginStatus(this,true);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(this,getResources().getString(R.string.wrong_pwd),Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this,getResources().getString(R.string.account_is_empty),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.password_can_not_be_empty),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.account_not_empty),Toast.LENGTH_SHORT).show();
                }
        }
    }
}
