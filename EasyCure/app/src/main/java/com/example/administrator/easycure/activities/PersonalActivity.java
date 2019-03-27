package com.example.administrator.easycure.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.ProblemLvAdapter;
import com.example.administrator.easycure.adapters.UserSettingLvAdapter;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/10/26 0026.
 */

public class PersonalActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView activity_personal_iv;
    private ListView activity_personal_lv;
    private Button activity_personal_btn;
    private TextView activity_personal_username;

    private UserSettingLvAdapter userSettingLvAdapter;

    private List<Map<String,Object>> list = new ArrayList<>();
    private Map<String,Object> map;

    private Intent intent;

    private String message = "";

    private String newUsername = "";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Toast.makeText(PersonalActivity.this,message, Toast.LENGTH_SHORT).show();

            switch(msg.what){
                case 0:     //修改成功
                    activity_personal_username.setText(newUsername);
                    SpUtil.modifyUsername(PersonalActivity.this,newUsername);
                    break;
                case 1:

                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        init();

        initData();
    }

    public void init(){
        activity_personal_iv = (ImageView)findViewById(R.id.activity_personal_iv);
        activity_personal_lv = (ListView)findViewById(R.id.activity_personal_lv);
        activity_personal_btn = (Button)findViewById(R.id.activity_personal_btn);
        activity_personal_username = (TextView)findViewById(R.id.activity_personal_username);

        activity_personal_iv.setOnClickListener(this);
        activity_personal_btn.setOnClickListener(this);
        activity_personal_lv.setOnItemClickListener(this);
        activity_personal_username.setOnClickListener(this);
    }

    public void initData(){

        String username = SpUtil.getUsername(this);
        activity_personal_username.setText(username);

        //求助反馈，
        map = new HashMap<>();
        map.put("iv",R.mipmap.activity_personal_img1);
        map.put("tv",getResources().getString(R.string.help));
        list.add(map);

        //病例
        map = new HashMap<>();
        map.put("iv",R.mipmap.activity_personal_img2);
        map.put("tv",getResources().getString(R.string.Case));
        list.add(map);

        //重置账号信息
        map = new HashMap<>();
        map.put("iv",R.mipmap.activity_personal_img3);
        map.put("tv",getResources().getString(R.string.reset_account));
        list.add(map);

        //关于，这些图片命名不用在意，因为之前有第五条item，但后面被我去掉了，图片名字就不改了
        map = new HashMap<>();
        map.put("iv",R.mipmap.activity_personal_img6);
        map.put("tv",getResources().getString(R.string.about));
        list.add(map);

        //这里只是复用而已，因为item里面的格式是一样的，故直接复用advice的适配器
        userSettingLvAdapter = new UserSettingLvAdapter(this,list);

        activity_personal_lv.setAdapter(userSettingLvAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        list.clear();

        initData();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_personal_iv:
                finish();
                break;
            case R.id.activity_personal_btn:

                //退出登录按钮
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.signout_dialog_title))
                        .setMessage(getResources().getString(R.string.signout_dialog_message))
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                close();
                                SpUtil.saveLoginStatus(PersonalActivity.this,false);
                                intent = new Intent(PersonalActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.activity_personal_username:   //点击昵称修改密码

                final View view = LayoutInflater.from(this).inflate(R.layout.dialog_modifyusername,null);

                final EditText et = view.findViewById(R.id.dialog_modifyusername_et);

                //设置EditText自动获取焦点
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.modify_username))
                        .setView(view)
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //关闭软键盘
                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(),  InputMethodManager.HIDE_NOT_ALWAYS);

                                newUsername = et.getText().toString();

                                if(!newUsername.equals("")){
                                    if(newUsername.length() <= 10){
                                        Pattern p = Pattern.compile("[a-zA-Z0-9]{1,10}");
                                        Matcher m = p.matcher(newUsername);

                                        if(m.find()){
                                            //用户名格式正确，可进行修改（不含非法字符）
                                            modifyUsernameOnServer(SpUtil.getPhonenumber(PersonalActivity.this),newUsername);

                                        }else{
                                            Toast.makeText(PersonalActivity.this,getResources().getString(R.string.illegal_character),Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(PersonalActivity.this,getResources().getString(R.string.username_length),Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(PersonalActivity.this,getResources().getString(R.string.not_empty),Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //关闭软键盘
                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }).create();

                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                        dialog.show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:     //求助反馈
                intent = new Intent(this,HelpActivity.class);
                startActivity(intent);
                break;
            case 1:     //病例管理，这块专门用来存放病例
                final View v = LayoutInflater.from(this).inflate(R.layout.dialog_modifyusername,null);

                final EditText et = v.findViewById(R.id.dialog_modifyusername_et);
                et.setHint(getResources().getString(R.string.password_pro) + ": ");
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et.setTransformationMethod(new PasswordTransformationMethod());

                //设置EditText自动获取焦点
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.authentication))
                        .setView(v)
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //关闭软键盘
                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(),  InputMethodManager.HIDE_NOT_ALWAYS);

                                String password = et.getText().toString();

                                if(!password.equals("")){
                                    if(password.length() <= 13 && password.length() >= 6){
                                        Pattern p = Pattern.compile("[a-zA-Z0-9]{6,13}");
                                        Matcher m = p.matcher(password);

                                        if(m.find()){
                                            //密码格式正确，接下来验证正确性
                                            if(password.equals(SpUtil.getPassword(PersonalActivity.this))){
                                                //密码正确，身份验证成功，可进入病例仓库
                                                intent = new Intent(PersonalActivity.this,CaseManagementActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(PersonalActivity.this,getResources().getString(R.string.wrong_pwd),Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(PersonalActivity.this,getResources().getString(R.string.illegal_character),Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(PersonalActivity.this,getResources().getString(R.string.password_length),Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(PersonalActivity.this,getResources().getString(R.string.password_can_not_be_empty),Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //关闭软键盘
                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }).create();

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                dialog.show();
                break;
            case 2:     //重置账号
                intent = new Intent(this,ResetAccountActivity.class);
                startActivity(intent);
                break;
            case 3:     //关于
                intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void modifyUsernameOnServer(final String phonenumber,final String username){
        new Thread(new Runnable() {

            String urlStr = "http://119.23.208.63/ECure-system/public/index.php/modifyusername";
            InputStream is;

            @Override
            public void run() {

                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    String data = "phonenumber=" + URLEncoder.encode(phonenumber) + "&username=" +URLEncoder.encode(username);

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

                        int statusCode = Integer.parseInt(json.getString("code"));

                        Message msg = handler.obtainMessage();

                        if(statusCode == 200){
                            msg.what = 0;
                        }else if(statusCode == 500){
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
}
