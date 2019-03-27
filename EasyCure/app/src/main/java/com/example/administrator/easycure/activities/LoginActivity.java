package com.example.administrator.easycure.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.User;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/10/19 0019.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private TextView activity_login_tv_forgotPwd,activity_login_tv_signUp;
    private EditText activity_login_et_phone,activity_login_et_pwd;
    private Button activity_login_btn_signin;
    private CheckBox activity_login_cb;

    private Intent intent;

    private Map<String,String> map;

    private String loginMsg = null;
    private String data = "";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SpUtil.saveRememberPasswordState(LoginActivity.this,activity_login_cb.isChecked());

            switch(msg.what){
                case 0:
                    //登录成功
                    Toast.makeText(LoginActivity.this,loginMsg,Toast.LENGTH_SHORT).show();
                    Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    //登录失败
                    Toast.makeText(LoginActivity.this,loginMsg,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
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
        //手机号输入框
        activity_login_et_phone = (EditText)findViewById(R.id.activity_login_et_phone);
        //密码输入框
        activity_login_et_pwd = (EditText)findViewById(R.id.activity_login_et_pwd);
        //登录按钮
        activity_login_btn_signin = (Button)findViewById(R.id.activity_login_btn_signin);
        //记住密码
        activity_login_cb = (CheckBox)findViewById(R.id.activity_login_cb);

        activity_login_tv_forgotPwd.setOnClickListener(this);
        activity_login_tv_signUp.setOnClickListener(this);
        activity_login_btn_signin.setOnClickListener(this);
        activity_login_cb.setOnClickListener(this);
    }

    //用户来到登录界面会自动填写上次登录的账号和密码
    public void initData(){
        map = SpUtil.getUserInfo(this);

        activity_login_et_phone.setText(map.get(Constant.PHONENUMBER));

        if(Boolean.valueOf(map.get(Constant.REMEMBER_PASSWORD))){
            activity_login_et_pwd.setText(map.get(Constant.PASSWORD));
        }

        activity_login_cb.setChecked(Boolean.valueOf(map.get(Constant.REMEMBER_PASSWORD)));
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
            case R.id.activity_login_cb:    //点击记住密码（选中或是反选都行）就关闭软键盘（如果开着的话）
                closeSoftKeyBoard();
                break;
            case R.id.activity_login_btn_signin:

                closeSoftKeyBoard();

                String phonenumber_et = activity_login_et_phone.getText().toString().trim();
                String password_et = activity_login_et_pwd.getText().toString().trim();
                /**
                 * 如果本地SP文件没有记录则直接访问服务器端的数据库进行密码验证
                 * 如果本地有SP文件并且里面保存了上一次登录的账号的账号和密码，则直接读SP文件
                 */
                if(phonenumber_et.length() > 0){
                    if(password_et.length() > 0){

                        Map<String,String> map = SpUtil.getUserInfo(this);
                        String phonenumber = map.get(Constant.PHONENUMBER);
                        String password = map.get(Constant.PASSWORD);

                        Pattern pattern = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
                        Matcher matcher = pattern.matcher(phonenumber_et);

                        Pattern p = Pattern.compile("[a-zA-z0-9]{6,13}");
                        Matcher m = p.matcher(password_et);

                        //查信息验证之前先验证输入数据的合法性
                        if(matcher.find()){
                            if(password_et.length() >= 6 && password_et.length() <= 13){
                                if(!m.find()){
                                    Toast.makeText(this,getResources().getString(R.string.wrong_password_format),Toast.LENGTH_SHORT).show();
                                    return ;
                                }
                            }else{
                                Toast.makeText(this,getResources().getString(R.string.password_length),Toast.LENGTH_SHORT).show();
                                return ;
                            }
                        }else{
                            Toast.makeText(this,getResources().getString(R.string.phone_num_format_error),Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        if(NetworkUsable.isNetworkConnected(this)){
                            //网络可用
                            if(phonenumber.equals("") || password.equals("")){
                                //表示本地sp文件找不到用户信息，这时候需要去服务器端验证用户登录信息
                                checkUserInfoFromServer(phonenumber_et,password_et);

                            }else {
                                if (phonenumber_et.equals(phonenumber) && password_et.equals(password)) {

                                    //用户输入的手机号和密码完全和sp匹配上，即用户登录成功

                                    SpUtil.saveLoginStatus(this, true);

                                    SpUtil.saveRememberPasswordState(this, activity_login_cb.isChecked());

                                    intent = new Intent(this, MainActivity.class);

                                    startActivity(intent);
                                    finish();
                                } else if (!phonenumber_et.equals(phonenumber)) {
                                    //手机号和本地sp文件不匹配，需要去请求数据库进行验证
                                    checkUserInfoFromServer(phonenumber_et, password_et);
                                } else {
                                    //来到这里表示本地sp文件存储了用户登录数据，手机号能成功匹配上，但是密码匹配还不上，说明密码错误
                                    Toast.makeText(this, getResources().getString(R.string.wrong_pwd), Toast.LENGTH_SHORT).show();
                                    activity_login_et_pwd.setText("");
                                }
                            }
                        }else{
                            //网络不可用
                            Toast.makeText(this,getResources().getString(R.string.network_anomaly),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.password_can_not_be_empty),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.account_not_empty),Toast.LENGTH_SHORT).show();
                }
        }
    }

    //从服务器端检查用户的登录账号和密码
    public void checkUserInfoFromServer(final String phonenumber,final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream is;

                try{
                    String urlStr = "http://119.23.208.63/ECure-system/public/index.php/signin";

                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    data = "phonenumber=" + phonenumber + "&password=" + password;

                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length",data.length() + "");
                    con.setDoOutput(true);

                    OutputStream os = con.getOutputStream();

                    os.write(data.getBytes());

                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();

                        String jsonStr = StrUtil.stream2String(is);

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                         }

                        JSONObject json = new JSONObject(jsonStr);

                        loginMsg = json.getString("msg_" + LangGetUtil.langGet());

                        Message msg = handler.obtainMessage();

                        switch(Integer.parseInt(json.getString("code"))){
                            case 200:
                                String username = json.getString("username");
                                //把密码、手机号（登录账号）、用户昵称 写入SP文件，便于后面控制账号的某些功能
                                SpUtil.saveAllUserInfo(LoginActivity.this,activity_login_et_phone.getText().toString().trim(),
                                        activity_login_et_pwd.getText().toString().trim(),username);

                                SpUtil.saveLoginStatus(LoginActivity.this,true);

                                msg.what = 0;
                                break;
                            case 400:
                                msg.what = 1;
                                break;
                        }
                        handler.sendMessageDelayed(msg,10);
                    }
                    System.out.println("网络请求失败");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //关闭软键盘
    public void closeSoftKeyBoard(){
        //判断软键盘是否打开
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if(imm.isActive() && getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
