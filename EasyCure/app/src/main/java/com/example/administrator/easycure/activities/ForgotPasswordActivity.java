package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.User;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;

import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_forgot_password_iv;
    private TextView activity_forgot_password_tv1,activity_forgot_password_tv2,activity_forgot_password_tv3;
    private EditText activity_forgot_password_et0,activity_forgot_password_et,activity_forgot_password_et1,activity_forgot_password_et2,
            activity_forgot_password_et3;
    private Button activity_forgot_password_btn,activity_forgot_password_btn1;

    private Map<String,String> map;

    private String securityQuestion1 = "";
    private String securityQuestion2 = "";
    private String securityQuestion3 = "";

    private User user;

    private EasyCureTimer timer;
    public EventHandler eh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        initData();
    }

    public void init(){
        //返回上一界面
        activity_forgot_password_iv = (ImageView)findViewById(R.id.activity_forgot_password_iv);

        //3个密保问题
        activity_forgot_password_tv1 = (TextView)findViewById(R.id.activity_forgot_password_tv1);
        activity_forgot_password_tv2 = (TextView)findViewById(R.id.activity_forgot_password_tv2);
        activity_forgot_password_tv3 = (TextView)findViewById(R.id.activity_forgot_password_tv3);

        //第一个输入框是填写验证码的，后面三个填写密保问题的答案
        activity_forgot_password_et0 = (EditText)findViewById(R.id.activity_forgot_password_et0);
        activity_forgot_password_et = (EditText)findViewById(R.id.activity_forgot_password_et);
        activity_forgot_password_et1 = (EditText)findViewById(R.id.activity_forgot_password_et1);
        activity_forgot_password_et2 = (EditText)findViewById(R.id.activity_forgot_password_et2);
        activity_forgot_password_et3 = (EditText)findViewById(R.id.activity_forgot_password_et3);

        //第一个按钮发送验证码到安全手机号，第二个按钮时下一步，点击切换到重置密码界面
        activity_forgot_password_btn = (Button)findViewById(R.id.activity_forgot_password_btn);
        activity_forgot_password_btn1 = (Button)findViewById(R.id.activity_forgot_password_btn1);

        activity_forgot_password_et0.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user = DBControler.selectAccountItem(activity_forgot_password_et0.getText().toString().trim());
                if(user != null){
                    securityQuestion1 = user.getSecurityQuestion1();
                    securityQuestion2 = user.getSecurityQuestion2();
                    securityQuestion3 = user.getSecurityQuestion3();

                    activity_forgot_password_tv1.setText(securityQuestion1);
                    activity_forgot_password_tv2.setText(securityQuestion2);
                    activity_forgot_password_tv3.setText(securityQuestion3);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activity_forgot_password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(user != null){
                    if(activity_forgot_password_et.getText().toString().trim().length() == 6){
                        SMSSDK.submitVerificationCode("86",activity_forgot_password_et0.getText().toString().trim()
                                ,activity_forgot_password_et.getText().toString().trim());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activity_forgot_password_iv.setOnClickListener(this);
        activity_forgot_password_btn.setOnClickListener(this);
        activity_forgot_password_btn1.setOnClickListener(this);

    }

    public void initData(){

        //初始化3个密保问题
        activity_forgot_password_tv1.setText(getResources().getString(R.string.nothing_to_show));
        activity_forgot_password_tv2.setText(getResources().getString(R.string.nothing_to_show));
        activity_forgot_password_tv3.setText(getResources().getString(R.string.nothing_to_show));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //返回上一界面
            case R.id.activity_forgot_password_iv:
                finish();
                break;

            //发送验证码
            case R.id.activity_forgot_password_btn:
                if(activity_forgot_password_et0.getText().toString().trim().length() > 0){
                    if(user != null){
                        Toast.makeText(this,getResources().getString(R.string.sms_send),Toast.LENGTH_SHORT).show();
                        sendSmsCheckCode();
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.account_is_empty),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;

            //点击下一步进入重置密码界面
            case R.id.activity_forgot_password_btn1:
                if(activity_forgot_password_et0.getText().toString().trim().length() > 0){
                    if(user != null){
                        //这里要先判断验证码是否正确或者3个密保问题是否都填对了，如果验证成功才进入重置界面
                        if(!activity_forgot_password_et.isEnabled()
                                || (user.getSecurityAnswer1().equals(activity_forgot_password_et1.getText().toString().trim())
                                && user.getSecurityAnswer2().equals(activity_forgot_password_et2.getText().toString().trim())
                                && user.getSecurityAnswer3().equals(activity_forgot_password_et3.getText().toString().trim()))){
                            Intent intent = new Intent(this,ResetAccountActivity.class);
                            intent.putExtra(Constant.PHONENUMBER,activity_forgot_password_et0.getText().toString().trim());
                            startActivity(intent);
                        }else{
                            Toast.makeText(this,getResources().getString(R.string.verification_failed),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.account_is_empty),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void sendSmsCheckCode(){

        activity_forgot_password_btn.setEnabled(false);

        eh = new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.cancel();
                                activity_forgot_password_btn.setText(getResources().getString(R.string.check_successful));
                                activity_forgot_password_btn.setEnabled(false);
                                activity_forgot_password_et.setEnabled(false);
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        //这里默认只支持中国的手机号码
        if(user != null){
            SMSSDK.getVerificationCode("86",user.getSecurityPhoneNumber());
        }

        timer = new EasyCureTimer(60000,1000);
        timer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(eh != null){
            SMSSDK.unregisterEventHandler(eh);
        }
    }

    class EasyCureTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public EasyCureTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            activity_forgot_password_btn.setText((millisUntilFinished / 1000) + "s");
        }

        @Override
        public void onFinish() {
            activity_forgot_password_btn.setEnabled(true);
            activity_forgot_password_btn.setText(getResources().getString(R.string.send));
        }
    }
}
