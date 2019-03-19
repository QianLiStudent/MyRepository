package com.example.administrator.easycure.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.easycure.MobUtils.MobUtil;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

/**
 * Created by Administrator on 2018/10/19 0019.
 */

public class SignupActivity extends BaseActivity implements View.OnClickListener{

    private EditText activity_signup_et_userName,activity_signup_et_pwd;
    private Button activity_signup_btn_signup;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);

        init();
    }

    public void init(){

        activity_signup_et_userName = (EditText)findViewById(R.id.activity_signup_et_userName);
        activity_signup_et_pwd = (EditText)findViewById(R.id.activity_signup_et_pwd);

        activity_signup_btn_signup = (Button)findViewById(R.id.activity_signup_btn_signup);

        activity_signup_btn_signup.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId()){
            /**
             * 下一步按钮：点击进入验证码界面
             */
            case R.id.activity_signup_btn_signup:

                //调用该方法把填写的用户名和密码保存进服务器端的数据库和SP文件
                checkInfo();
                break;
        }
    }

    public void checkInfo(){
        String et_userName = activity_signup_et_userName.getText().toString().trim();
        String et_password = activity_signup_et_pwd.getText().toString().trim();

        if(et_userName.length() > 0){
            if(et_password.length() > 0){
                if(et_password.length() >= 6){
                    //把用户名和密码写入数据库和SP文件，这个操作在手机验证完成后再完成
                    MobUtil.sendCode(this,et_userName,et_password);
                }else{
                    Toast.makeText(this,getResources().getString(R.string.password_length),Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,getResources().getString(R.string.password_can_not_be_empty),Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,getResources().getString(R.string.username_can_not_be_empty),Toast.LENGTH_SHORT).show();
        }
    }
}
