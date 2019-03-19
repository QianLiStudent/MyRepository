package com.example.administrator.easycure.MobUtils;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.User;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.LoginActivity;
import com.example.administrator.easycure.activities.SecuritySettingsActivity;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.SpUtil;

import java.util.HashMap;
import java.util.Map;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by Administrator on 2018/10/26 0026.
 */

public class MobUtil {

    public static Map<String,String> map;

    public static void sendCode(final Context context, final String userNameValue, final String passwordValue){

        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);

        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 验证码验证成功之后执行这个逻辑
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phoneValue = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    User user = DBControler.selectAccountItem(phoneValue);
                    if(user != null){
                        Toast.makeText(context,context.getResources().getString(R.string.account_is_exist),Toast.LENGTH_SHORT).show();
                    }else{
                        //把用户名、密码、手机号写入SP文件
                        SpUtil.saveUserInfo(context,passwordValue,phoneValue);

                        //验证成功之后进入账号找回设置界面，设置安全号码和密保问题
                        Intent intent = new Intent(context, SecuritySettingsActivity.class);
                        intent.putExtra(Constant.USERNAME,userNameValue);
                        intent.putExtra(Constant.PHONENUMBER,phoneValue);
                        intent.putExtra(Constant.PASSWORD,passwordValue);
                        context.startActivity(intent);
                    }
                } else{
                    //验证失败处理
                    Toast.makeText(context,context.getResources().getString(R.string.get_code_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        page.show(context);
    }
}
