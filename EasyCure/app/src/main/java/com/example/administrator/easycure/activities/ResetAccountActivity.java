package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SPHelper;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class ResetAccountActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_reset_password_iv;
    private Button activity_reset_password_btn1,activity_reset_password_btn2;
    private EditText activity_reset_password_etu,activity_reset_password_etpwd1,activity_reset_password_etpwd2,
            activity_reset_password_et1,activity_reset_password_et2;

    private EventHandler eh;

    private EasyCureTimer timer;

    private String data = "";

    private String message = "";

    private Intent intent;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Toast.makeText(ResetAccountActivity.this,message,Toast.LENGTH_SHORT).show();

            switch(msg.what){
                case 0:

                    SpUtil.removePassword(ResetAccountActivity.this);
                    SpUtil.saveRememberPasswordState(ResetAccountActivity.this,false);

                    Intent intent = new Intent(ResetAccountActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_account);

        init();

    }

    public void init(){

        intent = getIntent();

        activity_reset_password_iv = (ImageView)findViewById(R.id.activity_reset_password_iv);

        activity_reset_password_btn1 = (Button)findViewById(R.id.activity_reset_password_btn1);
        activity_reset_password_btn2 = (Button)findViewById(R.id.activity_reset_password_btn2);

        activity_reset_password_etu = (EditText)findViewById(R.id.activity_reset_password_etu);
        activity_reset_password_etpwd1 = (EditText)findViewById(R.id.activity_reset_password_etpwd1);
        activity_reset_password_etpwd2 = (EditText)findViewById(R.id.activity_reset_password_etpwd2);
        activity_reset_password_et1 = (EditText)findViewById(R.id.activity_reset_password_et1);
        activity_reset_password_et2 = (EditText)findViewById(R.id.activity_reset_password_et2);

        activity_reset_password_iv.setOnClickListener(this);
        activity_reset_password_btn1.setOnClickListener(this);
        activity_reset_password_btn2.setOnClickListener(this);

        activity_reset_password_et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(activity_reset_password_et2.getText().toString().trim().length() == 6){
                    SMSSDK.submitVerificationCode("86",activity_reset_password_et1.getText().toString().trim()
                            ,activity_reset_password_et2.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        String security_phone_number = activity_reset_password_et1.getText().toString().trim();
        String pwd_et1 = activity_reset_password_etpwd1.getText().toString().trim();
        String pwd_et2 = activity_reset_password_etpwd2.getText().toString().trim();
        String username = activity_reset_password_etu.getText().toString().trim();

        switch(v.getId()){
            //返回上一界面
            case R.id.activity_reset_password_iv:
                finish();
                break;

            //发送验证码
            case R.id.activity_reset_password_btn1:
                if(security_phone_number.length() > 0){
                    Pattern pattern = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
                    Matcher matcher = pattern.matcher(security_phone_number);

                    if(matcher.find()){
                        Toast.makeText(this,getResources().getString(R.string.sms_send),Toast.LENGTH_SHORT).show();
                        SpUtil.saveUserInfoTmp(this,"securityPhoneNumberTmp",security_phone_number);
                        sendSmsCheckCode();
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.phone_num_format_error),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;
            //点击下一步进入重置密码界面
            case R.id.activity_reset_password_btn2:
                if(security_phone_number.length() > 0){
                        //这里要先判断验证码是否验证成功，若成功则把所有的数据更新到数据库中
                        if((!activity_reset_password_et2.isEnabled())){
                            if(pwd_et1.equals(pwd_et2) && (pwd_et1.length() >= 6 && pwd_et1.length() <= 13)){
                                if(username.length() > 0 ){
                                    Map<String,String> map = new HashMap<>();
                                    map.put(Constant.PHONENUMBER,SpUtil.getPhonenumber(this));
                                    map.put(Constant.USERNAME,username);
                                    map.put(Constant.PASSWORD,pwd_et1);
                                    map.put(Constant.SECURITY_NUMBER,security_phone_number);

                                    updateUserInfoToServer(map);

                                    SpUtil.saveAllUserInfo(this,SpUtil.getPhonenumber(this),pwd_et1,username);

                                    Intent intent = new Intent(this,LoginActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(this,getResources().getString(R.string.username_can_not_be_empty),Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                if(!(pwd_et1.length() >= 6 && pwd_et1.length() <= 13)){
                                    Toast.makeText(this,getResources().getString(R.string.password_length),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(this,getResources().getString(R.string.password_inconsistent),Toast.LENGTH_SHORT).show();
                                }

                            }

                        }else{
                            Toast.makeText(this,getResources().getString(R.string.verification_failed),Toast.LENGTH_SHORT).show();
                        }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void sendSmsCheckCode(){

        activity_reset_password_btn1.setEnabled(false);

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
                                activity_reset_password_btn1.setText(getResources().getString(R.string.check_successful));
                                activity_reset_password_btn1.setEnabled(false);
                                activity_reset_password_et2.setEnabled(false);
                                activity_reset_password_et1.setEnabled(false);
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

        //这里默认只支持中国的手机号码，发送到目的手机号
        SMSSDK.getVerificationCode("86",activity_reset_password_et1.getText().toString().trim());

        timer = new EasyCureTimer(60000,1000);
        timer.start();
    }

    public void updateUserInfoToServer(final Map<String,String> map){
        new Thread(new Runnable() {

            InputStream is;

            @Override
            public void run() {
                String urlStr = "http://119.23.208.63/ECure-system/public/index.php/reset_all_user_info";

                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    data = "phonenumber=" + URLEncoder.encode(map.get(Constant.PHONENUMBER)) + "" +
                            "&username=" + URLEncoder.encode(map.get(Constant.USERNAME)) + "" +
                            "&password=" + URLEncoder.encode(map.get(Constant.PASSWORD)) + "" +
                            "&security_number=" + URLEncoder.encode(map.get(Constant.SECURITY_NUMBER));

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

                        message = json.getString("msg_" + LangGetUtil.langGet());

                        Message msg = handler.obtainMessage();

                        if(Integer.parseInt(json.getString("code")) == 200){
                            msg.what = 0;

                        }else{
                            msg.what = 1;
                        }

                        handler.sendMessageDelayed(msg,10);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class EasyCureTimer extends CountDownTimer {

        public EasyCureTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            activity_reset_password_btn1.setText((millisUntilFinished / 1000) + "s");
        }

        @Override
        public void onFinish() {
            activity_reset_password_btn1.setEnabled(true);
            activity_reset_password_btn1.setText(getResources().getString(R.string.send));
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
