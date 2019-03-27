package com.example.administrator.easycure.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by Administrator on 2018/10/27 0027.
 */

public class SecuritySettingsActivity extends BaseActivity implements View.OnClickListener{

    private EditText activity_security_settings_et1;

    private Button activity_security_settings_btn;

    private String securityPhoneNumber = "";

    private String userName = "";
    private String phoneNumber = "";
    private String password = "";

    private Intent intent;

    private Map<String,String> map;

    private String data = "";
    private String message = "";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Toast.makeText(SecuritySettingsActivity.this,message,Toast.LENGTH_SHORT).show();

            switch(msg.what){
                case 0: //注册成功
                    Intent intent = new Intent(SecuritySettingsActivity.this,LoginActivity.class);
                    startActivity(intent);
                    SecuritySettingsActivity.this.finish();
                    break;
                case 1: //注册失败
                    Intent intent1 = new Intent(SecuritySettingsActivity.this,LoginActivity.class);
                    startActivity(intent1);
                    SecuritySettingsActivity.this.finish();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_settings);

        init();
    }

    public void init(){

        intent = getIntent();
        userName = SpUtil.getTmpData(this,"usernameTmp");
        phoneNumber = SpUtil.getTmpData(this,"phonenumberTmp");
        password = SpUtil.getTmpData(this,"passwordTmp");

        /**
         * et1：绑定安全手机号码
         */
        activity_security_settings_et1 = (EditText)findViewById(R.id.activity_security_settings_et1);

        activity_security_settings_btn = (Button)findViewById(R.id.activity_security_settings_btn);

        activity_security_settings_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_security_settings_btn:

                //关闭软键盘
                closeSoftKeyBoard();

                //先判断安全号码是否设置正确，全部设置完毕之后完成注册，切换至登录界面

                if(!activity_security_settings_et1.getText().toString().equals("")){

                    Pattern pattern = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
                    Matcher matcher = pattern.matcher(activity_security_settings_et1.getText().toString());

                    if(matcher.find()){
                        //进入这里表说手机号码格式正确
                        securityPhoneNumber = activity_security_settings_et1.getText().toString().trim();

                        map = new HashMap<>();
                        map.put(Constant.USERNAME,userName);
                        map.put(Constant.PHONENUMBER,phoneNumber);
                        map.put(Constant.PASSWORD,password);
                        map.put(Constant.SECURITY_NUMBER,securityPhoneNumber);

                        Toast.makeText(this,getResources().getString(R.string.checking),Toast.LENGTH_SHORT).show();

                        activity_security_settings_btn.setEnabled(false);

                        //注册，map保存用户的注册数据
                        signUp(map);

                    }else{
                        Toast.makeText(this,getResources().getString(R.string.phone_num_format_error),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,getResources().getString(R.string.security_num_not_empty),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //用户注册
    public void signUp(final Map<String,String> map){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlStr = "http://119.23.208.63/ECure-system/public/index.php/signup";

                InputStream is;

                try{
                    URL url = new URL(urlStr);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    Set<String> set = map.keySet();
                    Iterator<String> iterator = set.iterator();

                    while(iterator.hasNext()){

                        String key = iterator.next();
                        String val = map.get(key);

                        data = data + key + "=" + URLEncoder.encode(val) + "&";
                    }

                    data = data.substring(0,data.length() - 1);

                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");     //设置发送给服务器的请求数据的格式
                    con.setRequestProperty("Content-Length",data.length() + "");  //封装要输出的参数长度
                    con.setDoOutput(true);  //设置允许输出

                    OutputStream os = con.getOutputStream();

                    os.write(data.getBytes());

                    int code = con.getResponseCode();
                    if(code == 200){
                        is = con.getInputStream();

                        String jsonStr = StrUtil.stream2String(is);

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        JSONObject myJson = new JSONObject(jsonStr);

                        message = myJson.getString("msg_" + LangGetUtil.langGet());

                        Message msg = handler.obtainMessage();

                        if(Integer.parseInt(myJson.getString("code")) == 200){
                            //如果进入这里表示用户注册成功，然后把用户的信息写入到sp文件，方便用户下次进入软件自动登录
                            SpUtil.saveAllUserInfo(SecuritySettingsActivity.this,
                                    map.get(Constant.PHONENUMBER),map.get(Constant.PASSWORD),map.get(Constant.USERNAME));
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
