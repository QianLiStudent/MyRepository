package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.administrator.easycure.utils.SpUtil;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class ResetAccountActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private ImageView activity_reset_password_iv;
    private Button activity_reset_password_btn1,activity_reset_password_btn2;
    private EditText activity_reset_password_etu,activity_reset_password_etpwd1,activity_reset_password_etpwd2,
            activity_reset_password_et1,activity_reset_password_et2,activity_reset_password_et3,
            activity_reset_password_et4,activity_reset_password_et5;
    private Spinner activity_reset_password_sp1,activity_reset_password_sp2,activity_reset_password_sp3;

    private ArrayAdapter arrayAdapter1;
    private ArrayAdapter arrayAdapter2;
    private ArrayAdapter arrayAdapter3;

    private EventHandler eh;

    private EasyCureTimer timer;

    private Intent intent;

    private String securityQuestion1 = "";
    private String securityQuestion2 = "";
    private String securityQuestion3 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_account);

        init();

        initData();
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
        activity_reset_password_et3 = (EditText)findViewById(R.id.activity_reset_password_et3);
        activity_reset_password_et4 = (EditText)findViewById(R.id.activity_reset_password_et4);
        activity_reset_password_et5 = (EditText)findViewById(R.id.activity_reset_password_et5);

        activity_reset_password_sp1 = (Spinner)findViewById(R.id.activity_reset_password_sp1);
        activity_reset_password_sp2 = (Spinner)findViewById(R.id.activity_reset_password_sp2);
        activity_reset_password_sp3 = (Spinner)findViewById(R.id.activity_reset_password_sp3);

        activity_reset_password_iv.setOnClickListener(this);
        activity_reset_password_btn1.setOnClickListener(this);
        activity_reset_password_btn2.setOnClickListener(this);

        arrayAdapter1 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array1));

        arrayAdapter2 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array2));
        arrayAdapter3 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array3));

        arrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        arrayAdapter3.setDropDownViewResource(R.layout.spinner_item);

        activity_reset_password_sp1.setAdapter(arrayAdapter1);
        activity_reset_password_sp2.setAdapter(arrayAdapter2);
        activity_reset_password_sp3.setAdapter(arrayAdapter3);

        activity_reset_password_sp1.setOnItemSelectedListener(this);
        activity_reset_password_sp2.setOnItemSelectedListener(this);
        activity_reset_password_sp3.setOnItemSelectedListener(this);

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

    public void initData(){
        securityQuestion1 = getResources().getStringArray(R.array.security_question_array1)[0];
        securityQuestion2 = getResources().getStringArray(R.array.security_question_array2)[0];
        securityQuestion3 = getResources().getStringArray(R.array.security_question_array3)[0];
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.activity_security_settings_sp1:
                securityQuestion1 = parent.getItemAtPosition(position).toString().trim();
                break;
            case R.id.activity_security_settings_sp2:
                securityQuestion2 = parent.getItemAtPosition(position).toString().trim();
                break;
            case R.id.activity_security_settings_sp3:
                securityQuestion3 = parent.getItemAtPosition(position).toString().trim();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //返回上一界面
            case R.id.activity_reset_password_iv:
                finish();
                break;

            //发送验证码
            case R.id.activity_reset_password_btn1:
                if(activity_reset_password_et1.getText().toString().trim().length() > 0){
                        Toast.makeText(this,getResources().getString(R.string.sms_send),Toast.LENGTH_SHORT).show();
                        sendSmsCheckCode();
                }else{
                    Toast.makeText(this,getResources().getString(R.string.phone_number_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;

            //点击下一步进入重置密码界面
            case R.id.activity_reset_password_btn2:
                if(activity_reset_password_et1.getText().toString().trim().length() > 0){
                        //这里要先判断验证码是否验证成功，若成功则把所有的数据更新到数据库中
                        if((!activity_reset_password_et2.isEnabled())){
                            if(activity_reset_password_etpwd1.getText().toString().trim().equals(
                                    activity_reset_password_etpwd2.getText().toString().trim()) &&
                                    activity_reset_password_etpwd1.getText().toString().trim().length() >= 6){
                                if(activity_reset_password_etu.getText().toString().trim().length() > 0 &&
                                        activity_reset_password_et3.getText().toString().trim().length() > 0 &&
                                        activity_reset_password_et4.getText().toString().trim().length() > 0 &&
                                        activity_reset_password_et5.getText().toString().trim().length() > 0){
                                    Map<String,String> map = new HashMap<>();
                                    map.put(Constant.USERNAME,activity_reset_password_etu.getText().toString().trim());
                                    map.put(Constant.PASSWORD,activity_reset_password_etpwd1.getText().toString().trim());
                                    map.put(Constant.SECURITY_NUMBER,activity_reset_password_et1.getText().toString().trim());
                                    map.put(Constant.SECURITY_QUESTION1,securityQuestion1);
                                    map.put(Constant.SECURITY_QUESTION2,securityQuestion2);
                                    map.put(Constant.SECURITY_QUESTION3,securityQuestion3);
                                    map.put(Constant.SECURITY_ANSWER1,activity_reset_password_et3.getText().toString().trim());
                                    map.put(Constant.SECURITY_ANSWER2,activity_reset_password_et4.getText().toString().trim());
                                    map.put(Constant.SECURITY_ANSWER3,activity_reset_password_et5.getText().toString().trim());

                                    //所有内容均正确填写，更新数据库
                                    if(DBControler.updateAccountItem(intent.getStringExtra(Constant.PHONENUMBER),map)){
                                        Toast.makeText(this,getResources().getString(R.string.reset_successful),Toast.LENGTH_SHORT).show();
                                    }
                                    SpUtil.saveUserInfo(this,activity_reset_password_etpwd1.getText().toString().trim(),intent.getStringExtra(Constant.PHONENUMBER));

                                    Intent intent = new Intent(this,LoginActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(this,getResources().getString(R.string.some_info_empty),Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this,getResources().getString(R.string.password_empty_or_inconsistent),Toast.LENGTH_SHORT).show();
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
        SMSSDK.getVerificationCode("86",activity_reset_password_et1.getText().toString().trim());

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
            activity_reset_password_btn1.setText((millisUntilFinished / 1000) + "s");
        }

        @Override
        public void onFinish() {
            activity_reset_password_btn1.setEnabled(true);
            activity_reset_password_btn1.setText(getResources().getString(R.string.send));
        }
    }
}
