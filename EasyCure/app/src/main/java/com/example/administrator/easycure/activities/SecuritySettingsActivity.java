package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.SpUtil;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class SecuritySettingsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    private EditText activity_security_settings_et1,activity_security_settings_et2,
            activity_security_settings_et3,activity_security_settings_et4;

    private Spinner activity_security_settings_sp1,activity_security_settings_sp2,activity_security_settings_sp3;

    private Button activity_security_settings_btn;

    private ArrayAdapter arrayAdapter1;
    private ArrayAdapter arrayAdapter2;
    private ArrayAdapter arrayAdapter3;

    private String securityQuestion1 = "";
    private String securityQuestion2 = "";
    private String securityQuestion3 = "";

    private String securityAnswer1 = "";
    private String securityAnswer2 = "";
    private String securityAnswer3 = "";

    private String userName = "";
    private String phoneNumber = "";
    private String password = "";

    private Intent intent;

    private Map<String,String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_settings);

        init();

        initData();
    }

    public void init(){

        intent = getIntent();
        userName = intent.getStringExtra(Constant.USERNAME);
        phoneNumber = intent.getStringExtra(Constant.PHONENUMBER);
        password = intent.getStringExtra(Constant.PASSWORD);

        /**
         * et1：绑定安全手机号码
         * et2：第一个密保问题的答案
         * et3：第二个密保问题的答案
         * et4：第三个密保问题的答案
         */
        activity_security_settings_et1 = (EditText)findViewById(R.id.activity_security_settings_et1);
        activity_security_settings_et2 = (EditText)findViewById(R.id.activity_security_settings_et2);
        activity_security_settings_et3 = (EditText)findViewById(R.id.activity_security_settings_et3);
        activity_security_settings_et4 = (EditText)findViewById(R.id.activity_security_settings_et4);

        activity_security_settings_sp1 = (Spinner)findViewById(R.id.activity_security_settings_sp1);
        activity_security_settings_sp2 = (Spinner)findViewById(R.id.activity_security_settings_sp2);
        activity_security_settings_sp3 = (Spinner)findViewById(R.id.activity_security_settings_sp3);

        activity_security_settings_btn = (Button)findViewById(R.id.activity_security_settings_btn);

        activity_security_settings_sp1.setOnItemSelectedListener(this);
        activity_security_settings_sp2.setOnItemSelectedListener(this);
        activity_security_settings_sp3.setOnItemSelectedListener(this);

        activity_security_settings_btn.setOnClickListener(this);

        arrayAdapter1 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array1));

        arrayAdapter2 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array2));

        arrayAdapter3 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array3));

        arrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        arrayAdapter3.setDropDownViewResource(R.layout.spinner_item);

        activity_security_settings_sp1.setAdapter(arrayAdapter1);
        activity_security_settings_sp2.setAdapter(arrayAdapter2);
        activity_security_settings_sp3.setAdapter(arrayAdapter3);
    }

    public void initData(){
        securityQuestion1= getResources().getStringArray(R.array.security_question_array1)[0];
        securityQuestion2= getResources().getStringArray(R.array.security_question_array2)[0];
        securityQuestion3= getResources().getStringArray(R.array.security_question_array3)[0];
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
            case R.id.activity_security_settings_btn:
                //先判断安全号码和密保问题是否设置，全部设置完毕之后完成注册，切换至登录界面
                if(activity_security_settings_et1.length() > 0){
                    if(activity_security_settings_et2.length() > 0 && activity_security_settings_et3.length() > 0 && activity_security_settings_et4.length() > 0){

                        securityAnswer1 = activity_security_settings_et2.getText().toString().trim();
                        securityAnswer2 = activity_security_settings_et3.getText().toString().trim();
                        securityAnswer3 = activity_security_settings_et4.getText().toString().trim();

                        Toast.makeText(this,getResources().getString(R.string.finish_enroll),Toast.LENGTH_SHORT).show();

                        map = new HashMap<>();
                        map.put(Constant.USERNAME,userName);
                        map.put(Constant.PHONENUMBER,phoneNumber);
                        map.put(Constant.PASSWORD,password);
                        map.put(Constant.SECURITY_NUMBER,activity_security_settings_et1.getText().toString().trim());
                        map.put(Constant.SECURITY_QUESTION1,securityQuestion1);
                        map.put(Constant.SECURITY_QUESTION2,securityQuestion2);
                        map.put(Constant.SECURITY_QUESTION3,securityQuestion3);
                        map.put(Constant.SECURITY_ANSWER1,securityAnswer1);
                        map.put(Constant.SECURITY_ANSWER2,securityAnswer2);
                        map.put(Constant.SECURITY_ANSWER3,securityAnswer3);

                        //用户注册数据保存到数据库中
                        DBControler.addAccountItem(map);

                        intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        finish();


                    }else{
                        Toast.makeText(this,getResources().getString(R.string.secret_answer_not_empty),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.security_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
