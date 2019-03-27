package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_forgot_password_iv;
    private EditText activity_forgot_password_et0,activity_forgot_password_et;
    private Button activity_forgot_password_btn,activity_forgot_password_btn1;

    private EasyCureTimer timer;
    public EventHandler eh;

    //用来记录发送验证码的手机，以防用户在获取验证码之后改了手机号，然后造成其他用户的账号被修改
    private String tmpPhone = "";
    private String tmpSecurityPhoneNumber = "";

    //标识，1表示验证码验证成功，0表示验证失败
    private int flag = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    //只要用户手机号通过已经注册过的验证就会来到这里执行发送短信验证码的逻辑
                    sendSmsCheckCode(tmpSecurityPhoneNumber);
                    timer = new EasyCureTimer(60000,1000);
                    timer.start();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();
    }

    public void init(){
        //返回上一界面
        activity_forgot_password_iv = (ImageView)findViewById(R.id.activity_forgot_password_iv);

        //第一个输入框填写你的要找回密码的手机号，第二个输入框是填写验证码的
        activity_forgot_password_et0 = (EditText)findViewById(R.id.activity_forgot_password_et0);
        activity_forgot_password_et = (EditText)findViewById(R.id.activity_forgot_password_et);

        //第一个按钮发送验证码到安全手机号，第二个按钮时下一步，点击切换到重置密码界面
        activity_forgot_password_btn = (Button)findViewById(R.id.activity_forgot_password_btn);
        activity_forgot_password_btn1 = (Button)findViewById(R.id.activity_forgot_password_btn1);

        activity_forgot_password_iv.setOnClickListener(this);
        activity_forgot_password_btn.setOnClickListener(this);
        activity_forgot_password_btn1.setOnClickListener(this);

        activity_forgot_password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(activity_forgot_password_et.getText().toString().trim().length() == 6){
                    SMSSDK.submitVerificationCode("86",tmpSecurityPhoneNumber,activity_forgot_password_et.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                //发送验证码之前需要判断手机号是否有效以及是否注册过
                if(activity_forgot_password_et0.getText().toString().trim().length() > 0){

                    Pattern pattern = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
                    Matcher matcher = pattern.matcher(activity_forgot_password_et0.getText().toString());

                    if(matcher.find()){
                        //手机号格式正确，判断账号是否存在,如果存在则发送验证码
                        isUserSignUped(activity_forgot_password_et0.getText().toString().trim());

                    }else{
                        //手机号格式不正确
                        Toast.makeText(this,getResources().getString(R.string.phone_num_format_error),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;

            //点击下一步进入重置密码界面
            case R.id.activity_forgot_password_btn1:
                if(activity_forgot_password_et0.getText().toString().trim().length() > 0){
                    //进入这里表示手机号栏有写东西，接下来验证手机号格式是否合法
                    Pattern pattern = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
                    Matcher matcher = pattern.matcher(activity_forgot_password_et0.getText().toString());

                    if(matcher.find()){
                        //进入这里表示填写的手机号合法
                        if(!activity_forgot_password_et.getText().toString().equals("")){
                            //这里要先判断验证码是否正确
                            if(flag == 1){
                                //验证码正确
                                    //判断手机号和验证码是否对应的上，因为验证码是根据需要重置账号的手机号找到关联的安全手机号，然后发送验证码的，如果手机号和发送验证码之前的手机号不一致则验证无效
                                    Intent intent = new Intent(this,ResetAccountActivity.class);
                                    intent.putExtra(Constant.PHONENUMBER,activity_forgot_password_et0.getText().toString().trim());
                                    startActivity(intent);
                            }else{
                                //验证错误
                                Toast.makeText(ForgotPasswordActivity.this,getResources().getString(R.string.verification_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            //验证码为空
                            Toast.makeText(this,getResources().getString(R.string.code_not_empty),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.phone_num_format_error),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //判断手机号码是否注册过
    public void isUserSignUped(final String phonenumber){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlStr = "http://119.23.208.63/ECure-system/public/index.php/getSecurityPhoneNumber";
                InputStream is;

                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("charset","utf-8");
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                    con.setDoOutput(true);

                    con.setUseCaches(false);
                    con.setInstanceFollowRedirects(true);
                    con.connect();
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());

                    String param = "phonenumber=" + phonenumber;

                    out.writeBytes(param);
                    //流用完记得关
                    out.flush();
                    out.close();

                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();

                        String jsonStr = StrUtil.stream2String(is);

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        JSONObject json = new JSONObject(jsonStr);

                        int statusCode = json.getInt("code");

                        if(statusCode == 200){
                            //表示该手机号注册过了
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgotPasswordActivity.this,getResources().getString(R.string.sms_send),Toast.LENGTH_SHORT).show();
                                }
                            });
                            //临时记录需要用到验证码的手机号（即需要重置账号信息的手机号），用来与验证码关联
                            tmpPhone = phonenumber;
                            tmpSecurityPhoneNumber = json.getString("security_phone_number");

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            handler.sendMessageDelayed(msg,10);

                        }else if(statusCode == 400){
                            //表示该手机号没有注册过
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgotPasswordActivity.this,getResources().getString(R.string.account_not_signup),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendSmsCheckCode(String security_phone_number){

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
                                activity_forgot_password_et0.setEnabled(false);
                                activity_forgot_password_et.setEnabled(false);
                                flag = 1;
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

        //发送短信验证码，这里默认只支持中国的手机号码
        SMSSDK.getVerificationCode("86",security_phone_number);

        timer = new EasyCureTimer(60000,1000);
        timer.start();
    }

    class EasyCureTimer extends CountDownTimer {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(eh != null){
            SMSSDK.unregisterEventHandler(eh);
        }
    }
}
