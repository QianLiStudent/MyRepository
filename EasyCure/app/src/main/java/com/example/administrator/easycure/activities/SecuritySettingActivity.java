package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by Administrator on 2018/11/3 0003.
 */

public class SecuritySettingActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    private ImageView activity_security_setting_iv;
    private EditText activity_security_setting_et1,activity_security_setting_et2,activity_security_setting_et3,
            activity_security_setting_et4,activity_security_setting_et5,activity_security_setting_et6;
    private Spinner activity_security_setting_sp1,activity_security_setting_sp2,activity_security_setting_sp3;
    private Button activity_security_setting_btn;


    private String securityQuestion1 = "";
    private String securityQuestion2 = "";
    private String securityQuestion3 = "";

    private ArrayAdapter arrayAdapter1;
    private ArrayAdapter arrayAdapter2;
    private ArrayAdapter arrayAdapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setting);

        init();
    }

    public void init(){
        activity_security_setting_iv = (ImageView)findViewById(R.id.activity_security_setting_iv);

        activity_security_setting_et1 = (EditText)findViewById(R.id.activity_security_setting_et1);
        activity_security_setting_et2 = (EditText)findViewById(R.id.activity_security_setting_et2);
        activity_security_setting_et3 = (EditText)findViewById(R.id.activity_security_setting_et3);
        activity_security_setting_et4 = (EditText)findViewById(R.id.activity_security_setting_et4);
        activity_security_setting_et5 = (EditText)findViewById(R.id.activity_security_setting_et5);
        activity_security_setting_et6 = (EditText)findViewById(R.id.activity_security_setting_et6);

        activity_security_setting_sp1 = (Spinner)findViewById(R.id.activity_security_setting_sp1);
        activity_security_setting_sp2 = (Spinner)findViewById(R.id.activity_security_setting_sp2);
        activity_security_setting_sp3 = (Spinner)findViewById(R.id.activity_security_setting_sp3);

        activity_security_setting_btn = (Button)findViewById(R.id.activity_security_setting_btn);

        arrayAdapter1 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array1));

        arrayAdapter2 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array2));

        arrayAdapter3 = new ArrayAdapter(this,R.layout.spinner_item,
                getResources().getStringArray(R.array.security_question_array3));

        arrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        arrayAdapter3.setDropDownViewResource(R.layout.spinner_item);

        activity_security_setting_sp1.setAdapter(arrayAdapter1);
        activity_security_setting_sp2.setAdapter(arrayAdapter2);
        activity_security_setting_sp3.setAdapter(arrayAdapter3);

        activity_security_setting_iv.setOnClickListener(this);
        activity_security_setting_btn.setOnClickListener(this);

        activity_security_setting_sp1.setOnItemSelectedListener(this);
        activity_security_setting_sp2.setOnItemSelectedListener(this);
        activity_security_setting_sp3.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_security_setting_iv:
                finish();
                break;
            case R.id.activity_security_setting_btn:
                String et1_str = activity_security_setting_et1.getText().toString().trim();
                String et2_str = activity_security_setting_et2.getText().toString().trim();
                String et3_str = activity_security_setting_et3.getText().toString().trim();
                String et4_str = activity_security_setting_et4.getText().toString().trim();
                String et5_str = activity_security_setting_et5.getText().toString().trim();
                String et6_str = activity_security_setting_et6.getText().toString().trim();
                if(et1_str.equals("") && et2_str.equals("") && et3_str.equals("")
                        && et4_str.equals("") && et5_str.equals("") && et6_str.equals("")){
                    Toast.makeText(this,getResources().getString(R.string.unmodified_content),Toast.LENGTH_SHORT).show();
                }else{
                    if(et2_str.equals(et3_str) && (et2_str.length() == 0 || et2_str.length() >= 6)){

                        Map<String,String> map = new HashMap<>();
                        map.put(Constant.SECURITY_NUMBER,et1_str);
                        map.put(Constant.PASSWORD,et2_str);

                        if((et4_str.length() > 0 && et5_str.length() > 0 && et6_str.length() > 0) || (et4_str.length() == 0 && et5_str.length() == 0 &&et6_str.length() == 0)){

                            if(et4_str.length() > 0 && et5_str.length() > 0 && et6_str.length() > 0){
                                map.put(Constant.SECURITY_QUESTION1,securityQuestion1);
                                map.put(Constant.SECURITY_QUESTION2,securityQuestion2);
                                map.put(Constant.SECURITY_QUESTION3,securityQuestion3);
                                map.put(Constant.SECURITY_ANSWER1,et4_str);
                                map.put(Constant.SECURITY_ANSWER2,et5_str);
                                map.put(Constant.SECURITY_ANSWER3,et6_str);
                            }
                            //把修改的内容写入数据库更新，同时更新SP文件
                            DBControler.updateAccountItem((SpUtil.getUserInfo(this)).get(Constant.PHONENUMBER),map);
                            if(et2_str.length() >= 6){
                                //表示用户要修改密码，就更新SP文件，下次重启login界面就会更新密码了
                                SpUtil.saveUserInfo(this,et2_str,(SpUtil.getUserInfo(this)).get(Constant.PHONENUMBER));
                            }
                            Toast.makeText(this,getResources().getString(R.string.setting_successful), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this,LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(this,getResources().getString(R.string.update_all_security_question),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(et2_str.equals(et3_str)){
                            Toast.makeText(this,getResources().getString(R.string.password_length), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this,getResources().getString(R.string.password_empty_or_inconsistent), Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.activity_security_setting_sp1:
                securityQuestion1 = parent.getItemAtPosition(position).toString().trim();
                break;
            case R.id.activity_security_setting_sp2:
                securityQuestion1 = parent.getItemAtPosition(position).toString().trim();
                break;
            case R.id.activity_security_setting_sp3:
                securityQuestion1 = parent.getItemAtPosition(position).toString().trim();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
